package de.julianostarek.flow.ui.main.trips.results.timeline

import android.content.Context
import android.graphics.PointF
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TimelineLayoutManager(
    context: Context?,
    private val getSpecs: () -> TimelineAdapter.Specs
) :
    LinearLayoutManager(context, HORIZONTAL, false) {
    internal var verticalScrollOffset: Int = 0

    private var childWidth: Int = 0
    private var maxChildHeight: Int = 0

    private val verticalHelper: OrientationHelper =
        OrientationHelper.createVerticalHelper(this)

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val positive = targetPosition > getPosition(getChildAt(0)!!)
        val x = childWidth.toFloat()
        val specs = getSpecs()
        val y = specs.averageOffsetStepMinutes * specs.minuteScale
        return PointF(
            if (positive) x else -x,
            if (positive) y else -y
        )
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        var scrolled = if (dy < 0) {
            -min(abs(dy), verticalScrollOffset)
        } else {
            min(dy, maxChildHeight - verticalScrollOffset)
        }
        scrolled = dy
        if (scrolled == 0)
            return 0
        verticalHelper.offsetChildren(-scrolled)
        this.verticalScrollOffset += scrolled
        return scrolled
    }

    override fun layoutDecoratedWithMargins(
        child: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.layoutDecoratedWithMargins(child, left, top, right, bottom)
        this.maxChildHeight = max(this.maxChildHeight, bottom - top)
        if (this.childWidth == 0) {
            this.childWidth = right - left
        }
        if (verticalHelper.getDecoratedStart(child) != verticalScrollOffset) {
            verticalHelper.offsetChild(child, -verticalScrollOffset)
        }
    }

}