package de.julianostarek.flow.ui.main.trips.results.timeline

import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.*
import java.lang.reflect.Field
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


class TimelineSnapHelper : LinearSnapHelper() {
    private var verticalHelper: OrientationHelper? = null
    private var horizontalHelper: OrientationHelper? = null
    private val _recyclerView: Field = SnapHelper::class.java.getDeclaredField("mRecyclerView")
    private val recyclerView: RecyclerView? get() = _recyclerView.get(this) as RecyclerView?

    private var flingVelocityX: Int = 0
    private var flingVelocityY: Int = 0

    init {
        _recyclerView.isAccessible = true
    }

    override fun createScroller(layoutManager: RecyclerView.LayoutManager): LinearSmoothScroller {
        return object : LinearSmoothScroller(recyclerView?.context) {

            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                if (recyclerView == null) {
                    // The associated RecyclerView has been removed so there is no action to take.
                    return
                }
                val snapDistances = calculateDistanceToFinalSnap(
                    recyclerView!!.layoutManager!!,
                    targetView
                )
                val dx = snapDistances[0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(max(abs(dx), abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }

                this@TimelineSnapHelper.flingVelocityX = 0
                this@TimelineSnapHelper.flingVelocityY = 0
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 75F / displayMetrics.densityDpi
            }

            override fun calculateTimeForScrolling(dx: Int): Int {
                val displayMetrics = recyclerView?.resources?.displayMetrics!!
                val speedPerPixel = if (
                    this@TimelineSnapHelper.flingVelocityX != 0
                    && this@TimelineSnapHelper.flingVelocityY != 0
                    && mTargetVector != null
                ) {
                    calculateFlingSpeedPerPixel()
                } else calculateSpeedPerPixel(displayMetrics)
                return ceil(abs(dx) * speedPerPixel).toInt()
            }

            private fun calculateFlingSpeedPerPixel(): Float {
                return 1F / ((abs(mTargetVector.x * flingVelocityX) + abs(mTargetVector.y * flingVelocityY)) / 1000F)
            }

        }
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        return when (layoutManager.getPosition(targetView)) {
            // target is first child, snap to its top left
            0 -> getStartTopSnapVector(layoutManager, targetView)
            // target is last child, snap to its bottom right
            // layoutManager.itemCount - 1 -> getEndBottomSnapVector(layoutManager, targetView)
            // target is mid-list, snap to its center
            else -> getCenterSnapVector(layoutManager, targetView)
        }
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager !is TimelineLayoutManager)
            return null
        val isFirstCompletelyVisible = layoutManager.findFirstCompletelyVisibleItemPosition() == 0
        val isLastCompletelyVisible =
            layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.itemCount - 1

        val centerView = findHorizontalCenterChild(layoutManager) ?: return null
        val centerDistance = getManhattanDistance(getCenterSnapVector(layoutManager, centerView))
        val helper = horizontalHelper(layoutManager)
        val centerDelta = helper.getDecoratedCenter(centerView) - helper.centerAfterPadding
        // Check if we're coming from a center snap that just made the first/last view completely visible
        if (centerDistance == 0
            || (centerDistance == abs(centerDelta)
                    && recyclerView?.canScrollHorizontally(centerDelta) == false)
        ) {
            return centerView
        }
        when {
            isFirstCompletelyVisible && !isLastCompletelyVisible -> {
                return layoutManager.findViewByPosition(0)
            }
            !isFirstCompletelyVisible && isLastCompletelyVisible -> {
                return layoutManager.findViewByPosition(layoutManager.itemCount - 1)
            }
        }
        return centerView
    }

    private fun findHorizontalCenterChild(
        layoutManager: RecyclerView.LayoutManager
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        val helper = horizontalHelper(layoutManager)
        var minDistance = Int.MAX_VALUE
        val center = helper.centerAfterPadding
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i) ?: continue
            val distance = abs(helper.getDecoratedCenter(child) - center)
            /** if child center is closer than previous closest, set it as closest   */
            if (distance < minDistance) {
                minDistance = distance
                closestChild = child
            }
        }
        return closestChild
    }

    private fun getStartTopSnapVector(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val vector = IntArray(2)
        val horizontalHelper = horizontalHelper(layoutManager)
        vector[0] =
            horizontalHelper.getDecoratedStart(targetView) - horizontalHelper.startAfterPadding
        if (layoutManager.canScrollVertically()) {
            val verticalHelper = verticalHelper(layoutManager)
            vector[1] = targetView.top - verticalHelper.startAfterPadding
        }
        return vector
    }

    private fun getEndBottomSnapVector(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val vector = IntArray(2)
        val horizontalHelper = horizontalHelper(layoutManager)
        vector[0] = horizontalHelper.getDecoratedEnd(targetView) - horizontalHelper.endAfterPadding
        if (layoutManager.canScrollVertically()) {
            val verticalHelper = verticalHelper(layoutManager)
            vector[1] = verticalHelper.getDecoratedEnd(targetView) - verticalHelper.endAfterPadding
        }
        return vector
    }

    private fun getCenterSnapVector(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray {
        val vector = IntArray(2)
        val horizontalHelper = horizontalHelper(layoutManager)
        vector[0] =
            horizontalHelper.getDecoratedCenter(targetView) - horizontalHelper.centerAfterPadding
        if (layoutManager.canScrollVertically()) {
            val verticalHelper = verticalHelper(layoutManager)
            vector[1] = targetView.center - verticalHelper.centerAfterPadding
            if (vector[1] < 0) {
                // make sure we don't hard-hit the top
                if (targetView.top == verticalHelper.startAfterPadding) {
                    val topSnapDelta = getStartTopSnapVector(layoutManager, targetView)[1]
                    vector[1] = max(vector[1], topSnapDelta)
                }
            } else if (vector[1] > 0) {
                // make sure we don't hard-hit the bottom
                if (targetView.bottom == verticalHelper.endAfterPadding) {
                    val bottomSnapDelta = getEndBottomSnapVector(layoutManager, targetView)[1]
                    vector[1] = min(vector[1], bottomSnapDelta)
                }
            }
        }
        return vector
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager, velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager !is TimelineLayoutManager) {
            return RecyclerView.NO_POSITION
        }
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }
        val currentView =
            findHorizontalCenterChild(layoutManager) ?: return RecyclerView.NO_POSITION

        val currentPosition = layoutManager.getPosition(currentView)
        if (currentPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        val diff = findNextPositionForFling(layoutManager, velocityX, velocityY)
        if (diff == 0) {
            return RecyclerView.NO_POSITION
        }
        this.flingVelocityX = velocityX
        this.flingVelocityY = velocityY
        return max(0, min(currentPosition + diff, layoutManager.itemCount - 1))
    }

    private fun findNextPositionForFling(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int, velocityY: Int
    ): Int {
        val distances = calculateScrollDistance(velocityX, velocityY)
        val helper = horizontalHelper(layoutManager)
        val midChild = layoutManager.getChildAt(max(0, layoutManager.childCount - 2))
        val distancePerChild = helper.getDecoratedEnd(midChild) - helper.getDecoratedStart(midChild)
        return distances[0] / distancePerChild
    }

    private fun verticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (verticalHelper == null || verticalHelper?.layoutManager !== layoutManager) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return verticalHelper!!
    }

    private fun horizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (horizontalHelper == null || horizontalHelper?.layoutManager !== layoutManager) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return horizontalHelper!!
    }

    private fun getManhattanDistance(vector: IntArray): Int {
        var sum = 0
        for (i in vector.indices) {
            sum += abs(vector[i])
        }
        return sum
    }

    private fun OrientationHelper.getDecoratedCenter(targetView: View): Int {
        return getDecoratedStart(targetView) + getDecoratedMeasurement(targetView) / 2
    }

    private val View.center: Int get() = top + height / 2

    private val OrientationHelper.centerAfterPadding: Int
        get() = startAfterPadding + totalSpace / 2

    companion object {
        private const val INVALID_DISTANCE = 1f
    }
}
