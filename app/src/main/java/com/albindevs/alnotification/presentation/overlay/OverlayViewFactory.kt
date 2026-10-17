package com.albindevs.alnotification.presentation.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.albindevs.alnotification.domain.model.ShieldConfig

/**
 * Fábrica (Factory Pattern) responsable de la creación, configuración visual
 * y listeners táctiles de las vistas superpuestas administradas por [WindowManager].
 *
 * Cumple con el Principio de Responsabilidad Única (SRP): aísla la construcción
 * de vistas del ciclo de vida del Servicio.
 */
class OverlayViewFactory(private val context: Context) {

    @SuppressLint("ClickableViewAccessibility")
    fun createEdgeShieldView(
        isTop: Boolean,
        isCalibration: Boolean,
        label: String,
        onTouchBlocked: (x: Int, y: Int) -> Unit
    ): View {
        val frame = FrameLayout(context)

        if (isCalibration) {
            val color = if (isTop) 0x66E53935.toInt() else 0x661E88E5.toInt()
            val drawable = GradientDrawable().apply {
                setColor(color)
                setStroke(2, Color.WHITE)
            }
            frame.background = drawable

            val text = TextView(context).apply {
                this.text = label
                setTextColor(Color.WHITE)
                textSize = 10f
                gravity = Gravity.CENTER
            }
            frame.addView(
                text,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        } else {
            frame.setBackgroundColor(Color.TRANSPARENT)
        }

        frame.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onTouchBlocked(event.rawX.toInt(), event.rawY.toInt())
            }
            true // Absorbe y descarta el evento para que no baje ni active la barra
        }

        return frame
    }

    @SuppressLint("ClickableViewAccessibility")
    fun createCustomShieldView(
        isCalibration: Boolean,
        windowManager: WindowManager,
        onPositionChanged: (newX: Int, newY: Int) -> Unit,
        onTouchBlocked: (x: Int, y: Int) -> Unit
    ): View {
        val frame = FrameLayout(context)

        if (isCalibration) {
            val drawable = GradientDrawable().apply {
                setColor(0x88FB8C00.toInt())
                setStroke(3, Color.YELLOW)
                cornerRadius = 8f
            }
            frame.background = drawable

            val text = TextView(context).apply {
                this.text = "Zona Fantasma\n(Arrastra aquí)"
                setTextColor(Color.WHITE)
                textSize = 9f
                gravity = Gravity.CENTER
            }
            frame.addView(
                text,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )

            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            frame.setOnTouchListener { view, event ->
                val params = view.layoutParams as WindowManager.LayoutParams
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(view, params)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        onPositionChanged(params.x, params.y)
                        true
                    }
                    else -> false
                }
            }
        } else {
            frame.setBackgroundColor(Color.TRANSPARENT)
            frame.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onTouchBlocked(event.rawX.toInt(), event.rawY.toInt())
                }
                true
            }
        }

        return frame
    }

    @SuppressLint("ClickableViewAccessibility")
    fun createFloatingBubbleView(
        windowManager: WindowManager,
        onBubbleClicked: () -> Unit
    ): View {
        val bubble = FrameLayout(context)
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(0xCC212121.toInt())
            setStroke(2, 0xFF4CAF50.toInt())
        }
        bubble.background = drawable

        val label = TextView(context).apply {
            this.text = "🛡️"
            textSize = 14f
            gravity = Gravity.CENTER
        }
        bubble.addView(label, FrameLayout.LayoutParams(60, 60, Gravity.CENTER))

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = false

        bubble.setOnTouchListener { view, event ->
            val params = view.layoutParams as WindowManager.LayoutParams
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (initialTouchX - event.rawX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {
                        isClick = false
                    }
                    params.x = initialX + deltaX
                    params.y = initialY + deltaY
                    windowManager.updateViewLayout(view, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        onBubbleClicked()
                    }
                    true
                }
                else -> false
            }
        }

        return bubble
    }

    fun createPausedBubbleView(onResumeClicked: () -> Unit): View {
        val bubble = FrameLayout(context)
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(0xCCFF5722.toInt())
            setStroke(2, Color.WHITE)
        }
        bubble.background = drawable

        val label = TextView(context).apply {
            this.text = "⏸️15s"
            textSize = 10f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        bubble.addView(label, FrameLayout.LayoutParams(70, 70, Gravity.CENTER))

        bubble.setOnClickListener {
            onResumeClicked()
        }

        return bubble
    }

    fun createEdgeLayoutParams(heightPx: Int, isTop: Boolean): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            heightPx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = if (isTop) Gravity.TOP or Gravity.START else Gravity.BOTTOM or Gravity.START
            x = 0
            y = 0
            if (isTop && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
        }
    }

    fun createCustomLayoutParams(config: ShieldConfig): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            config.customPointWidth,
            config.customPointHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = config.customPointX
            y = config.customPointY
        }
    }

    fun createBubbleLayoutParams(width: Int = 70, height: Int = 70): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            width,
            height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 30
            y = 70
        }
    }
}
