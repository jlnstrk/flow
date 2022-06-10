package de.julianostarek.flow.ui.main.trips.results.timeline.trip

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.core.content.ContextCompat
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.ui.component.linechip.LineChipDrawable
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.graphics.findBalancedIconScale
import de.julianostarek.flow.util.graphics.setBoundsScaledWidth
import de.jlnstrk.transit.common.model.Line
import de.julianostarek.flow.util.iconRawOrRegularResId
import kotlin.math.max
import kotlin.math.roundToInt

class PublicLegDrawable(
    context: Context,
    offsetDuration: Long,
    segmentDuration: Long,
    private val isFirst: Boolean,
    private val isLast: Boolean,
    line: Line
) : TimelineTripDrawable(
    context.resources.displayMetrics,
    offsetDuration,
    segmentDuration
) {
    private val productStyle: StyledProfile.ProductStyle =
        context.styles.resolveProductStyle(line.product)
    private val lineStyle: StyledProfile.LineStyle? = context.styles.resolveLineStyle(line)
    private val lineChip = LineChipDrawable(context, line)
    private val icon: Drawable = ContextCompat.getDrawable(
        context,
        productStyle.iconRawOrRegularResId(context)
    )!!
    private val icon2: Drawable = ContextCompat.getDrawable(context, productStyle.iconRes.let { context.resources.getIdentifier(it, null, null) })!!

    private val chipHeightRequirement: Float = 16F.dp + LineChipDrawable.NOMINAL_HEIGHT_DP.dp

    // 8 + 8 + 24 + 8
    private val chipIconHeightRequirement: Float = 48F.dp + LineChipDrawable.NOMINAL_HEIGHT_DP.dp



    init {
        fillPaint.style = Paint.Style.FILL
        lineChip.setBounds(0, 0, 48F.dp.toInt(), lineChip.intrinsicHeight)
        icon.bounds = icon.findBalancedIconScale(context)
        icon2.setBoundsScaledWidth(24F.dp.roundToInt())
    }

    override fun setMinuteScale(minuteScale: Int) {
        super.setMinuteScale(minuteScale)
        val alphaColor = productStyle.productColor and 0x80FFFFFF.toInt()
        if (segmentHeight >= chipIconHeightRequirement || (segmentHeight >= chipHeightRequirement && lineStyle != null)) {
            val colors = intArrayOf(alphaColor, productStyle.productColor)
            val positions = floatArrayOf(0F, max((chipIconReq2 + 12F.dp) / segmentHeight, 1.0F))
            fillPaint.shader =
                LinearGradient(0F, 0F, 0F, segmentHeight, colors, positions, Shader.TileMode.CLAMP)
        } else {
            fillPaint.color = productStyle.productColor
        }
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        buildSegmentPath(isFirst, isLast)
    }

    override fun draw(canvas: Canvas) {
        draw2(canvas)
    }

    private fun draw1(canvas: Canvas) {
        canvas.save()
        canvas.translate(0F, offsetHeight)
        canvas.drawPath(segmentPath, fillPaint)
        if (segmentHeight >= chipHeightRequirement) {
            canvas.save()
            canvas.translate(bounds.width() / 2F - lineChip.bounds.width() / 2F, 8F.dp)
            lineChip.draw(canvas)
            canvas.restore()

            if (segmentHeight >= chipIconHeightRequirement) {
                canvas.translate(
                    0F,
                    chipHeightRequirement /* + (height - minHeightChipIcon) / 2F */
                )
                canvas.translate(bounds.width() / 2F - icon.bounds.width() / 2F, 0F)
                icon.draw(canvas)
            }
        }
        canvas.restore()
    }

    private inline val chipReq2: Float get() = LineChipDrawable.NOMINAL_HEIGHT_DP.dp + 16F.dp
    private inline val chipIconReq2: Float get() = chipReq2 + 32F.dp

    private fun draw2(canvas: Canvas) {
        canvas.save()
        canvas.translate(0F, offsetHeight)
        canvas.drawPath(segmentPath, fillPaint)
        if (segmentHeight >= chipReq2) {
            canvas.translate(bounds.width() / 2F - lineChip.bounds.width() / 2F, 8F.dp)

            if (segmentHeight >= chipIconReq2) {
                icon2.draw(canvas)
                canvas.translate(0F, 28F.dp)
            }

            lineChip.draw(canvas)
        }
        canvas.restore()
    }

    override fun applyTextPaint(textPaint: TextPaint) {
        super.applyTextPaint(textPaint)
        lineChip.adjustTextPaint(textPaint)
    }

}