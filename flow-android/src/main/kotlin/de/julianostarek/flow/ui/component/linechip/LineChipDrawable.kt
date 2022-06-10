package de.julianostarek.flow.ui.component.linechip

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.content.ContextCompat
import de.julianostarek.flow.R
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.profile.StyledProfile.LineStyle.Fill.SOLID
import de.julianostarek.flow.profile.StyledProfile.LineStyle.Fill.STROKE
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.graphics.dp
import de.jlnstrk.transit.common.model.Line
import de.julianostarek.flow.util.featureIconResId
import kotlin.math.*

class LineChipDrawable(context: Context, private val line: Line) : Drawable() {
    private val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val nestedDrawable: Drawable?

    private val style: StyledProfile.LineStyle? = context.styles.resolveLineStyle(line)
    private var effectiveLabel: String = line.label

    /* Desired/computed bounds properties */
    private val minWidth: Float = NOMINAL_WIDTH_DP.dp(context)
    private var measuredTextWidth: Float = Float.NaN
    private var desiredWidth: Float = Float.NaN
    private val desiredHeight: Float = NOMINAL_HEIGHT_DP.dp(context)

    private val shapePath = Path()

    /* Padding properties */
    private val cornerRadius: Float = 4F.dp(context)
    private val smallCornerRadius: Float = 2F.dp(context)
    private val drawablePadding: Float = 4F.dp(context)
    private val nominalPadding: Float = 6F.dp(context)
    private val cathetus: Float = 10F.dp(context)
    private val cathetusPadding: Float = 2F.dp(context)
    private val overlapPadding: Float = 3F.dp(context)

    companion object {
        const val NOMINAL_WIDTH_DP: Float = 40F
        const val NOMINAL_HEIGHT_DP: Float = 18F
    }

    enum class Mode {
        START, MID, END, ONLY
    }

    var mode: Mode = Mode.ONLY
        set(value) {
            field = value
            invalidateSelf()
        }

    val overlapTranslation: Int
        get() = when (mode) {
            Mode.ONLY, Mode.END -> 0
            else -> (cathetus - overlapPadding).roundToInt()
        }

    init {
        textPaint.textAlign = Paint.Align.CENTER
        val featureIconRes = style?.featureIconRes
        if (featureIconRes != null) {
            nestedDrawable = ContextCompat.getDrawable(context, style!!.featureIconResId(context)!!)
            nestedDrawable?.setBounds(
                0,
                0,
                nestedDrawable.intrinsicWidth,
                nestedDrawable.intrinsicHeight
            )
        } else {
            nestedDrawable = null
        }
        if (style == null || style.fill == STROKE) {
            shapePaint.style = Paint.Style.STROKE
            shapePaint.strokeWidth = 1F.dp(context)
            shapePaint.strokeJoin = Paint.Join.ROUND
            if (style == null) {
                val attrs =
                    context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
                textPaint.color = attrs.getColor(0, Color.BLACK)
                shapePaint.color = ContextCompat.getColor(context, R.color.divider)
                attrs.recycle()
            } else if (style.fill == STROKE) {
                shapePaint.strokeWidth *= 1.5F
                val color = style.shapePrimaryColor
                shapePaint.color = color
                textPaint.color = color
            }
        } else {
            shapePaint.style = Paint.Style.FILL
            textPaint.color = style.shapeTextColor
            if (style.shapeSecondaryColor == 0) {
                shapePaint.color = style.shapePrimaryColor
            }
        }
    }

    override fun getIntrinsicWidth(): Int {
        if (desiredWidth.isNaN()) {
            desiredWidth = computeRequiredWidth()
        }
        return desiredWidth.roundToInt()
    }

    override fun getIntrinsicHeight(): Int {
        return desiredHeight.roundToInt()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateShapePath()
        if (bounds.width() < intrinsicWidth) {
            effectiveLabel = TextUtils.ellipsize(
                line.label,
                textPaint,
                bounds.width() - measuredTextWidth,
                TextUtils.TruncateAt.END
            ).toString()
        }

        if (style != null && style.fill == SOLID && style.shapeSecondaryColor != 0) {
            val stripeColors = intArrayOf(style.shapePrimaryColor, style.shapeSecondaryColor)
            val colorCount = stripeColors.size


            val colors = intArrayOf(
                style.shapePrimaryColor,
                style.shapePrimaryColor,
                style.shapeSecondaryColor,
                style.shapeSecondaryColor
            )
            val stops = floatArrayOf(0.0F, 0.5F, 0.5F, 1.0F)
            val centerX = bounds.width() / 2F
            val centerY = bounds.height() / 2F
            shapePaint.shader = LinearGradient(
                centerX - centerY, centerY - centerX,
                centerX + centerY, centerY + centerX,
                colors, stops,
                Shader.TileMode.REPEAT
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        shapePaint.alpha = alpha
        textPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // ignore
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    private fun computeRequiredWidth(): Float {
        this.measuredTextWidth = textPaint.measureText(line.label)
        var computedWidth = this.measuredTextWidth
        if (nestedDrawable != null) {
            computedWidth += drawablePadding
            computedWidth += nestedDrawable.intrinsicWidth
        }

        if (mode == Mode.ONLY) {
            computedWidth += nominalPadding * 2
            computedWidth = max(minWidth, computedWidth)
        } else {
            if (mode == Mode.START || mode == Mode.END) {
                // left or right -> nominal padding
                computedWidth += nominalPadding
                // opposite side -> cathetus length + cathetus padding
                computedWidth += cathetus + cathetusPadding
            } else {
                // both sides are triangular
                computedWidth += cathetus * 2
                computedWidth += cathetusPadding * 2
            }
        }
        return computedWidth
    }

    private fun invalidateShapePath() {
        shapePath.reset()

        val widthF = bounds.width().toFloat()
        val heightF = bounds.height().toFloat()

        val edgeOffset = shapePaint.strokeWidth / 2F
        val effectiveHeight = desiredHeight - shapePaint.strokeWidth

        if (mode == Mode.ONLY) {
            shapePath.apply {
                // top left, right of corner
                moveTo(edgeOffset + cornerRadius, edgeOffset)

                // top edge
                lineTo(widthF - edgeOffset - cornerRadius, edgeOffset)

                // top/right corner
                quadTo(
                    widthF - edgeOffset, edgeOffset,
                    widthF - edgeOffset, edgeOffset + cornerRadius
                )

                // right edge
                lineTo(widthF - edgeOffset, heightF - edgeOffset - cornerRadius)

                // bottom/right corner
                quadTo(
                    widthF - edgeOffset, heightF - edgeOffset,
                    widthF - edgeOffset - cornerRadius, heightF - edgeOffset
                )

                // bottom edge
                lineTo(edgeOffset + cornerRadius, heightF - edgeOffset)

                // bottom/left corner
                quadTo(
                    edgeOffset, heightF - edgeOffset,
                    edgeOffset, heightF - edgeOffset - cornerRadius
                )

                // left edge
                lineTo(edgeOffset, edgeOffset + cornerRadius)

                // top/left corner
                quadTo(
                    edgeOffset, edgeOffset,
                    edgeOffset + cornerRadius, edgeOffset
                )

                close()
            }
            return
        }

        val angle = atan2(effectiveHeight, cathetus)
        val yOffsetSmallAngle = sin(angle) * smallCornerRadius
        val xOffsetSmallAngle = cos(angle) * smallCornerRadius

        shapePath.apply {
            if (mode == Mode.START) {
                moveTo(edgeOffset + cornerRadius, heightF - edgeOffset)
            } else {
                moveTo(edgeOffset + smallCornerRadius, heightF - edgeOffset)
            }

            if (mode == Mode.START || mode == Mode.MID) {
                lineTo(
                    widthF - edgeOffset - cathetus - smallCornerRadius,
                    heightF - edgeOffset
                )
                quadTo(
                    widthF - edgeOffset - cathetus,
                    heightF - edgeOffset,
                    widthF - edgeOffset - cathetus + xOffsetSmallAngle,
                    heightF - edgeOffset - yOffsetSmallAngle
                )
                lineTo(
                    widthF - edgeOffset - xOffsetSmallAngle,
                    edgeOffset + yOffsetSmallAngle
                )
                quadTo(
                    widthF - edgeOffset,
                    edgeOffset,
                    widthF - edgeOffset - smallCornerRadius,
                    edgeOffset
                )
            } else {
                lineTo(widthF - edgeOffset - cornerRadius, heightF - edgeOffset)
                quadTo(
                    widthF - edgeOffset,
                    heightF - edgeOffset,
                    widthF - edgeOffset,
                    heightF - edgeOffset - cornerRadius
                )
                lineTo(widthF - edgeOffset, cornerRadius)
                quadTo(
                    widthF,
                    edgeOffset,
                    widthF - edgeOffset - cornerRadius,
                    edgeOffset
                )
            }
            if (mode == Mode.MID || mode == Mode.END) {
                lineTo(cathetus + smallCornerRadius, edgeOffset)
                quadTo(
                    cathetus,
                    edgeOffset,
                    cathetus - xOffsetSmallAngle,
                    edgeOffset + yOffsetSmallAngle
                )
                lineTo(
                    edgeOffset + xOffsetSmallAngle,
                    heightF - edgeOffset - yOffsetSmallAngle
                )
                quadTo(
                    edgeOffset,
                    heightF - edgeOffset,
                    edgeOffset + smallCornerRadius,
                    heightF - edgeOffset
                )
            } else {
                lineTo(edgeOffset + cornerRadius, edgeOffset)
                quadTo(
                    edgeOffset,
                    edgeOffset,
                    edgeOffset,
                    edgeOffset + cornerRadius
                )
                lineTo(
                    edgeOffset,
                    heightF - edgeOffset - cornerRadius
                )
                quadTo(
                    edgeOffset,
                    heightF - edgeOffset,
                    edgeOffset + cornerRadius,
                    heightF - edgeOffset
                )
            }
            close()
        }
    }

    fun adjustTextPaint(textPaint: Paint) {
        // Save old values
        val color = this.textPaint.color
        val align = this.textPaint.textAlign

        // Apply new values
        this.textPaint.set(textPaint)

        // Restore what we don't want overridden
        this.textPaint.color = color
        this.textPaint.textAlign = align
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(shapePath, shapePaint)

        canvas.save()

        val widthF = bounds.width().toFloat()
        val heightF = bounds.height().toFloat()

        var contentOffset = 0F
        if (mode == Mode.START) {
            contentOffset -= ((cathetus + cathetusPadding) - nominalPadding) / 2F
        } else if (mode == Mode.END) {
            contentOffset += ((cathetus + cathetusPadding) - nominalPadding) / 2F
        }

        var textX = widthF / 2F
        if (nestedDrawable != null) {
            textX -= (drawablePadding + nestedDrawable.intrinsicWidth) / 2F
        }
        val textY =
            heightF / 2F - (textPaint.fontMetrics.bottom + textPaint.fontMetrics.top) / 2F
        canvas.drawText(effectiveLabel, textX + contentOffset, textY, textPaint)
        if (nestedDrawable != null) {
            var drawableX = widthF - nestedDrawable.intrinsicWidth
            if (mode == Mode.ONLY
                || mode == Mode.END
            ) {
                drawableX -= nominalPadding
            } else {
                drawableX -= cathetus + cathetusPadding
            }
            canvas.translate(
                drawableX,
                (heightF - nestedDrawable.intrinsicHeight) / 2F
            )
            nestedDrawable.draw(canvas)
        }

        canvas.restore()
    }

}