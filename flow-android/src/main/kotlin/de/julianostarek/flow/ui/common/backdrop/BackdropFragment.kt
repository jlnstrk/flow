package de.julianostarek.flow.ui.common.backdrop

import android.animation.TimeInterpolator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.main.MainFragment
import de.julianostarek.flow.ui.transition.BackdropSharedElementTransition

abstract class BackdropFragment : Fragment(),
    ContentLayerFragment.Anchor,
    Toolbar.OnMenuItemClickListener {
    abstract val maxNumSharedElements: Int
    protected abstract val headerRes: Int
    protected abstract val menuRes: Int
    protected abstract val anchorPosition: Int
    private val contentLayerTag: String by lazy { "f$anchorPosition" }

    protected abstract val linearLayout: LinearLayout

    protected val mainFragment: MainFragment?
        get() = parentFragment as? MainFragment

    internal fun findContentLayer(): ContentLayerFragment<*>? {
        return parentFragmentManager.findFragmentByTag(contentLayerTag) as? ContentLayerFragment<*>
    }

    private val layoutChangeListener: View.OnLayoutChangeListener =
        object : View.OnLayoutChangeListener {
            private val update = Runnable {
                val contentLayer = findContentLayer()
                contentLayer?.invalidateAnchorPosition()
            }

            override fun onLayoutChange(
                view: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                if (bottom != oldBottom) {
                    val contentLayer = findContentLayer()
                    view.removeCallbacks(update)
                    if (contentLayer == null) {
                        view.post(update)
                    } else update.run()
                }
            }
        }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = BackdropSharedElementTransition()
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val wrapper = ContextThemeWrapper(requireContext(), R.style.ThemeOverlay_Sequence_Dark)
        return super.onGetLayoutInflater(savedInstanceState)
            .cloneInContext(wrapper)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayout.addOnLayoutChangeListener(layoutChangeListener)
        mainFragment?.contextualToolbar?.apply {
            setTitle(headerRes)
            menu.clear()
            if (menuRes != 0) {
                inflateMenu(menuRes)
            }
            setOnMenuItemClickListener(this@BackdropFragment)
            setNavigationOnClickListener {
                findContentLayer()?.navigateUp()
            }
        }
    }

    protected fun animateLayoutChange() {
        if (!isResumed)
            return
        val sceneRoot = requireView().parent.parent as ViewGroup
        TransitionManager.endTransitions(sceneRoot)
        val transition = getTransitionForLayoutChange()
        transition.interpolator = BOTTOM_SHEET_INTERPOLATOR
        TransitionManager.beginDelayedTransition(sceneRoot, transition)
    }

    protected open fun getTransitionForLayoutChange(): Transition = ChangeBounds()

    open fun applyEnterTransition(context: Context, direction: Int) = Unit

    open fun applyExitTransition(context: Context) = Unit

    abstract fun onCollectSharedElements(): List<Pair<View, String>>

    open fun getRevealedBackdropHeight(): Int {
        return linearLayout.height
    }

    fun getGroupBelowInput(): ViewGroup {
        return linearLayout.getChildAt(linearLayout.childCount - 1) as ViewGroup
    }

    abstract fun getConcealedBackdropHeight(): Int

    override fun getOffsetForState(layerMode: ContentLayerFragment.Mode): Int {
        return getConcealedBackdropHeight()
    }

    override fun getRevealedOffset(): Int {
        return getRevealedBackdropHeight()
    }

    companion object {
        val BOTTOM_SHEET_INTERPOLATOR: TimeInterpolator

        init {
            val field = ViewDragHelper::class.java.getDeclaredField("sInterpolator")
            field.isAccessible = true
            BOTTOM_SHEET_INTERPOLATOR = field.get(null) as TimeInterpolator
        }

    }

}