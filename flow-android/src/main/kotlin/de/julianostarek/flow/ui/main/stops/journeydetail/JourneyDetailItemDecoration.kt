package de.julianostarek.flow.ui.main.stops.journeydetail

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.graphics.intAlpha
import kotlin.math.roundToInt

class JourneyDetailItemDecoration(
    context: Context,
    private val keylinePx: Int
) : ItemDecoration() {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val sidelineWidth = 16F.dp(context)

    init {
        circlePaint.color = 0xFFFFFFFF.toInt()
    }

    var lineColor: Int
        get() = linePaint.color
        set(value) {
            linePaint.color = value
        }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 16F.dp(parent).roundToInt()
        }
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            outRect.bottom = 16F.dp(parent).roundToInt()
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: State) {
        super.onDrawOver(c, parent, state)
        val topView = parent.getChildAt(0) ?: return
        val top = when (parent.getChildAdapterPosition(topView)) {
            0 -> topView.top + topView.height / 2 - sidelineWidth / 2
            else -> topView.top - sidelineWidth / 2
        }
        val bottomView = parent.getChildAt(parent.childCount - 1)
        val bottom = when (parent.getChildAdapterPosition(bottomView)) {
            parent.adapter!!.itemCount - 1 -> bottomView.top + bottomView.height / 2 + sidelineWidth / 2
            else -> bottomView.bottom + sidelineWidth / 2
        }

        linePaint.alpha = bottomView.alpha.intAlpha()
        c.drawRoundRect(
            keylinePx.toFloat(),
            top,
            keylinePx.toFloat() + sidelineWidth,
            bottom,
            sidelineWidth / 2F,
            sidelineWidth / 2F,
            linePaint
        )

        val centerX = keylinePx + sidelineWidth / 2F
        val radius = sidelineWidth / 4F
        for (child in parent.children) {
            circlePaint.alpha = (0x80 * child.alpha).roundToInt()
            val centerY = (child.top + child.height / 2).toFloat()
            c.drawOval(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius,
                circlePaint
            )
        }
    }

}