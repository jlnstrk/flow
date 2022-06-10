package de.julianostarek.flow.ui.common.view.base

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class ContentLayerOutsideTouchShield @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val temp = IntArray(2)

    private var trackedPointerId: Int = -1

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val viewPager = getChildAt(0) as ViewPager2
        val currentPage = (((viewPager.getChildAt(0) as RecyclerView)
            .getChildAt(viewPager.currentItem) as FrameLayout)
            .getChildAt(0) as CoordinatorLayout)
            .getChildAt(0) as FrameLayout
        if (ev.actionMasked == MotionEvent.ACTION_DOWN && !ev.isOrWasInBounds(temp, currentPage)) {
            return false
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        return getChildAt(0).dispatchApplyWindowInsets(insets)
    }

    private fun MotionEvent.isOrWasInBounds(temp: IntArray, view: View): Boolean {
        view.getLocationOnScreen(temp)
        val (x, y) = temp
        val w = view.width
        val h = view.height

        return !(rawX < x || rawX > x + w || rawY < y || rawY > y + h)
    }
}