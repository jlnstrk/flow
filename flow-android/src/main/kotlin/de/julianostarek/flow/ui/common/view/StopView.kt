package de.julianostarek.flow.ui.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import de.julianostarek.flow.R
import de.julianostarek.flow.util.graphics.dp

class StopView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                         defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val colorPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val whitePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    @StopType
    var stopType: Int = STOP_TYPE_INTERMEDIATE
        set(value) {
            val isChange = field != value
            field = value
            if (isChange) {
                invalidate()
            }
        }

    var stopColor: Int
        get() = colorPaint.color
        set(value) {
            colorPaint.color = value
            invalidate()
        }
    var drawElseWithAlpha: Boolean = false

    init {
        whitePaint.color = Color.WHITE
        colorPaint.strokeWidth = 16F.dp(this)
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.StopView,
                    0, 0)
            stopType = typedArray.getInteger(R.styleable.StopView_stopType,
                    STOP_TYPE_INTERMEDIATE
            )
            stopColor = typedArray.getColor(R.styleable.StopView_stopColor, Color.BLACK)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val centerX = width / 2F
        val centerY = height / 2F
        val startY = if ((stopType == STOP_TYPE_INTERMEDIATE || stopType == STOP_TYPE_PASS)
                || stopType == STOP_TYPE_DESTINATION
        ) {
            0F
        } else {
            if (drawElseWithAlpha) {
                colorPaint.alpha = ALPHA_DISABLED
                canvas?.drawLine(centerX, 0F, centerX, centerY, colorPaint)
                colorPaint.alpha = ALPHA_ENABLED
            }
            centerY
        }
        val stopY = if (stopType == STOP_TYPE_ORIGIN
                || (stopType == STOP_TYPE_INTERMEDIATE || stopType == STOP_TYPE_PASS)) {
            height.toFloat()
        } else {
            if (drawElseWithAlpha) {
                colorPaint.alpha = ALPHA_DISABLED
                canvas?.drawLine(centerX, centerY, centerX, height.toFloat(), colorPaint)
                colorPaint.alpha = ALPHA_ENABLED
            }
            centerY
        }
        canvas?.drawLine(centerX, startY, centerX, stopY, colorPaint)

        if (stopType != STOP_TYPE_PASS) {
            canvas?.drawCircle(centerX, centerY,
                8F.dp(this), colorPaint)
            canvas?.drawCircle(centerX, centerY,
                4F.dp(this), whitePaint)
        }
    }

    companion object {
        @IntDef(STOP_TYPE_ORIGIN, STOP_TYPE_INTERMEDIATE, STOP_TYPE_DESTINATION, STOP_TYPE_PASS)
        annotation class StopType

        const val STOP_TYPE_ORIGIN = 0
        const val STOP_TYPE_INTERMEDIATE = 1
        const val STOP_TYPE_DESTINATION = 2
        const val STOP_TYPE_PASS = 3

        private const val ALPHA_DISABLED = 153
        private const val ALPHA_ENABLED = 255
    }

}