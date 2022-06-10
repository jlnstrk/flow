package de.julianostarek.flow.ui.common.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.core.content.ContextCompat
import de.julianostarek.flow.R
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.util.context.styles
import de.jlnstrk.transit.common.model.Line
import de.julianostarek.flow.util.featureIconResId
import kotlin.math.*

@Deprecated(
    message = "Use LineChipSpan2",
    replaceWith = ReplaceWith("LineChipSpan2", "de.jlnstrk.sequence.util.draw.LineChipSpan2")
)
class LineChipSpan(
    private val context: Context,
    private val line: Line,
    private var mode: Mode? = null
) : ReplacementSpan() {
    private var width: Float = 0F
    private val shapePaint: Paint = Paint()
    private val foregroundColor: Int?
    private var drawable: Drawable? = null

    private val height: Float = 18F.dp(context)
    private val cornerRadius: Float = 4F.dp(context)
    private val smallCornerRadius: Float = 2F.dp(context)
    private val drawablePadding: Float = 4F.dp(context)
    private val nominalPadding: Float = 6F.dp(context)
    private val cathete: Float = 10F.dp(context)
    private val cathetePadding: Float = 2F.dp(context)
    private val overlapPadding: Float = 3F.dp(context)

    enum class Mode {
        START, MID, END, ONLY
    }

    init {
        val style = context.styles.resolveLineStyle(line)
        shapePaint.flags = Paint.ANTI_ALIAS_FLAG
        if (style == null || style.fill == StyledProfile.LineStyle.Fill.STROKE) {
            shapePaint.style = Paint.Style.STROKE
            shapePaint.strokeWidth = 1F.dp(context)
            shapePaint.strokeJoin = Paint.Join.ROUND
            if (style == null) {
                shapePaint.color = ContextCompat.getColor(context, R.color.divider)
                foregroundColor = null
            } else {
                shapePaint.strokeWidth *= 1.5F
                shapePaint.color = style.shapePrimaryColor
                foregroundColor = shapePaint.color
            }
        } else {
            shapePaint.style = Paint.Style.FILL
            shapePaint.color = style.shapePrimaryColor
            foregroundColor = style.shapeTextColor
            if (style.featureIconRes != null) {
                drawable = ContextCompat.getDrawable(context, style.featureIconResId(context)!!)
                drawable?.setBounds(0, 0, drawable!!.intrinsicWidth, drawable!!.intrinsicHeight)
            }
        }
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val textPaint = Paint()
        textPaint.set(paint)
        textPaint.textAlign = Paint.Align.CENTER
        if (foregroundColor != null) {
            textPaint.color = foregroundColor
            if (shapePaint.style == Paint.Style.STROKE) {
                textPaint.isFakeBoldText = true
            }
        }

        canvas.save()
        canvas.translate(x, top + ((bottom - top) - height) / 2F)

        val edgeOffset = shapePaint.strokeWidth / 2F
        if (mode == Mode.ONLY) {
            canvas.drawRoundRect(
                edgeOffset,
                edgeOffset,
                width - edgeOffset,
                height - edgeOffset,
                cornerRadius,
                cornerRadius,
                shapePaint
            )
        } else {
            val effectiveHeight = height - shapePaint.strokeWidth

            val angle = atan2(effectiveHeight, cathete)
            val yOffsetSmallAngle = sin(angle) * smallCornerRadius
            val xOffsetSmallAngle = cos(angle) * smallCornerRadius

            val path: Path = Path().apply {
                if (mode == Mode.START) {
                    moveTo(edgeOffset + cornerRadius, height - edgeOffset)
                } else {
                    moveTo(edgeOffset + smallCornerRadius, height - edgeOffset)
                }

                if (mode == Mode.START || mode == Mode.MID) {
                    lineTo(
                        width - edgeOffset - cathete - smallCornerRadius,
                        height - edgeOffset
                    )
                    quadTo(
                        width - edgeOffset - cathete,
                        height - edgeOffset,
                        width - edgeOffset - cathete + xOffsetSmallAngle,
                        height - edgeOffset - yOffsetSmallAngle
                    )
                    lineTo(width - edgeOffset - xOffsetSmallAngle, edgeOffset + yOffsetSmallAngle)
                    quadTo(
                        width - edgeOffset,
                        edgeOffset,
                        width - edgeOffset - smallCornerRadius,
                        edgeOffset
                    )
                } else {
                    lineTo(width - edgeOffset - cornerRadius, height - edgeOffset)
                    quadTo(
                        width - edgeOffset,
                        height - edgeOffset,
                        width - edgeOffset,
                        height - edgeOffset - cornerRadius
                    )
                    lineTo(width - edgeOffset, cornerRadius)
                    quadTo(
                        width,
                        edgeOffset,
                        width - edgeOffset - cornerRadius,
                        edgeOffset
                    )
                }
                if (mode == Mode.MID
                    || mode == Mode.END
                ) {
                    lineTo(cathete + smallCornerRadius, edgeOffset)
                    quadTo(
                        cathete,
                        edgeOffset,
                        cathete - xOffsetSmallAngle,
                        edgeOffset + yOffsetSmallAngle
                    )
                    lineTo(
                        edgeOffset + xOffsetSmallAngle,
                        height - edgeOffset - yOffsetSmallAngle
                    )
                    quadTo(
                        edgeOffset,
                        height - edgeOffset,
                        edgeOffset + smallCornerRadius,
                        height - edgeOffset
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
                        height - edgeOffset - cornerRadius
                    )
                    quadTo(
                        edgeOffset,
                        height - edgeOffset,
                        edgeOffset + cornerRadius,
                        height - edgeOffset
                    )
                }
                close()

            }
            canvas.drawPath(path, shapePaint)
        }

        var contentOffset = 0F
        if (mode == Mode.START) {
            contentOffset -= ((cathete + cathetePadding) - nominalPadding) / 2F
        } else if (mode == Mode.END) {
            contentOffset += ((cathete + cathetePadding) - nominalPadding) / 2F
        }

        var textX = width / 2F
        if (drawable != null) {
            textX -= (drawablePadding + drawable!!.intrinsicWidth) / 2F
        }
        val textY =
            height / 2F + (textPaint.descent() - textPaint.ascent()) / 2F - textPaint.descent()
        canvas.drawText(line.label, textX + contentOffset, textY, textPaint)
        println("${this::class.java.simpleName} cO $contentOffset x $textX")
        if (drawable != null) {
            var drawableX = width - drawable!!.intrinsicWidth
            if (mode == Mode.ONLY
                || mode == Mode.END
            ) {
                drawableX -= nominalPadding
            } else {
                drawableX -= cathete + cathetePadding
            }
            canvas.translate(
                drawableX,
                (height - drawable!!.intrinsicHeight) / 2F
            )
            drawable?.draw(canvas)
        }

        canvas.restore()
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (fm != null) {
            val need = max(0F, height - (fm.bottom - fm.top))
            val claim = (need / 2F).roundToInt()
            fm.ascent -= claim
            fm.top -= claim
            fm.descent += claim
            fm.bottom += claim
        }
        val minWidth = 40F.dp(context)
        var computedWidth = paint.measureText(line.label)
        if (drawable != null) {
            computedWidth += drawablePadding
            computedWidth += drawable!!.intrinsicWidth
        }


        if (mode == null) {
            mode = when {
                start == 0 && end == text.length -> Mode.ONLY
                start == 0 -> Mode.START
                end == text.length -> Mode.END
                else -> Mode.MID
            }
        }

        if (mode == Mode.ONLY) {
            computedWidth += nominalPadding * 2
            width = max(minWidth, computedWidth)
            return width.roundToInt()
        } else {
            if (mode == Mode.START || mode == Mode.END) {
                // left or right -> nominal padding
                computedWidth += nominalPadding
                // other side -> cathete length + cathete padding
                computedWidth += cathete + cathetePadding
            } else {
                // both sides are triangular
                computedWidth += cathete * 2
                computedWidth += cathetePadding * 2
            }
            width = computedWidth
            if (mode != Mode.END) {
                return (width - cathete + overlapPadding).roundToInt()
            }
            return width.roundToInt()
        }
    }

}