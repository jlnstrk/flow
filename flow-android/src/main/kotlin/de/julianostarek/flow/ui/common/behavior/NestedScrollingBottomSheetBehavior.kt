package de.julianostarek.flow.ui.common.behavior

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ScrollingView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.abs

class NestedScrollingBottomSheetBehavior<V : View>(context: Context, attrs: AttributeSet?) :
    BottomSheetBehavior<V>(context, attrs) {
    internal var nestedScrollingChild: ScrollingView? = null
    private var initialY: Float = 0.0F
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        event: MotionEvent
    ): Boolean {
        if (nestedScrollingChild != null) {
            if (nestedScrollingChild!!.computeVerticalScrollOffset() > 0) {
                isDraggable = false
            } else if (state == STATE_EXPANDED) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        this.initialY = event.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isDraggable
                            && (event.y - initialY < 0.0F)
                            && abs(event.y - initialY) >= touchSlop
                        ) {
                            isDraggable = false
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        this.initialY = 0.0F
                    }
                }
            }
        }
        val result = super.onInterceptTouchEvent(parent, child, event)
        if (nestedScrollingChild?.computeVerticalScrollOffset() == 0)
            isDraggable = true
        return result
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: V, state: Parcelable) {
        SavedState::class.java.getDeclaredField("state")
            .also { it.isAccessible = true }.set(state, STATE_HALF_EXPANDED)
        super.onRestoreInstanceState(parent, child, state)
    }

}
