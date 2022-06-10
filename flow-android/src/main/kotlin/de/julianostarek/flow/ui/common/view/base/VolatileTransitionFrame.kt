package de.julianostarek.flow.ui.common.view.base

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import de.julianostarek.flow.ui.common.backdrop.BackdropFragment

class VolatileTransitionFrame @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val fragmentClass: Class<out BackdropFragment> by lazy {
        findFragment<Fragment>()::class.java as Class<out BackdropFragment>
    }

    override fun isTransitionGroup(): Boolean {
        val index = (transitionName[transitionName.length - 1].code xor 48) - 1
        if (ENTERING_FRAGMENT == null || EXITING_FRAGMENT == null)
            return true
        return when (fragmentClass) {
            EXITING_FRAGMENT -> index >= EXITING_MAX_SHARED
            ENTERING_FRAGMENT -> index >= ENTERING_MAX_SHARED
            else -> true
        }
    }

    companion object {
        var EXITING_FRAGMENT: Class<out BackdropFragment>? = null
        var ENTERING_FRAGMENT: Class<out BackdropFragment>? = null
        var ENTERING_MAX_SHARED = 0
        var EXITING_MAX_SHARED = 0
    }

}