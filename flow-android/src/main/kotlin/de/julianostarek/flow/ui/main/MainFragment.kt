package de.julianostarek.flow.ui.main

import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.Toolbar
import androidx.core.view.*
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentMainBinding
import de.julianostarek.flow.ui.common.view.base.VolatileTransitionFrame
import de.julianostarek.flow.ui.common.backdrop.BackdropFragment
import de.julianostarek.flow.ui.common.backdrop.ContentLayerFragment
import de.julianostarek.flow.ui.main.info.InfoBackdropFragment
import de.julianostarek.flow.ui.main.info.InfoViewModel
import de.julianostarek.flow.ui.main.map.SlideInMapFragment
import de.julianostarek.flow.ui.main.network.NetworkBackdropFragment
import de.julianostarek.flow.ui.main.stops.StopsBackdropFragment
import de.julianostarek.flow.ui.main.stops.StopsViewModel
import de.julianostarek.flow.ui.main.trips.TripsBackdropFragment
import de.julianostarek.flow.ui.main.trips.TripsViewModel
import de.julianostarek.flow.util.view.adjustTheme
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.view.recyclerView
import de.julianostarek.flow.viewmodel.LocationSourceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class MainFragment : Fragment(), TabLayout.OnTabSelectedListener, OnApplyWindowInsetsListener {
    internal var backdropFragment: BackdropFragment? = null
    private var snackBar: Snackbar? = null

    var mapAnimationListener: MapAnimationListener? = null

    internal fun findContentLayer(): ContentLayerFragment<*>? {
        return childFragmentManager.findFragmentByTag("f${viewBinding.viewPager.currentItem}")
                as? ContentLayerFragment<*>
    }

    private val locationViewModel: LocationSourceViewModel by activityViewModels()

    private val stopsViewModel: StopsViewModel by activityViewModels()
    private val tripsViewModel: TripsViewModel by activityViewModels()
    private val infoViewModel: InfoViewModel by activityViewModels()

    internal fun setContextualToolbarVisible(visible: Boolean) {
        viewBinding.tabLayout.clearAnimation()
        viewBinding.contextualToolbar.clearAnimation()
        val inInterpolator = DecelerateInterpolator()
        val outInterpolator = FastOutSlowInInterpolator()
        val viewIn = if (visible) viewBinding.contextualToolbar else viewBinding.tabLayout
        val viewOut = if (visible) viewBinding.tabLayout else viewBinding.contextualToolbar
        viewOut.animate()
            .alpha(0.0F)
            .setInterpolator(outInterpolator)
            .setDuration(DURATION_OUT)
            .withEndAction {
                viewOut.visibility = View.INVISIBLE
                viewIn.animate()
                    .alpha(1.0F)
                    .setInterpolator(inInterpolator)
                    .setDuration(DURATION_IN)
                    .withStartAction {
                        viewIn.visibility = View.VISIBLE
                    }
            }
    }

    lateinit var viewBinding: FragmentMainBinding
    val contextualToolbar: Toolbar
        get() = viewBinding.contextualToolbar

    val maximumContentLayerHeight: Int
        get() = requireView().height - viewBinding.tabLayout.height

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postponeEnterTransition()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_container)!!
        childFragmentManager.beginTransaction()
            .hide(mapFragment)
            .commitNowAllowingStateLoss()

        (mapFragment.enterTransition as? Transition)?.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) {
                isMapAnimating = true
            }

            override fun onTransitionEnd(transition: Transition) {
                isMapAnimating = false
                mapAnimationListener?.onMapAnimationFinished(true)
            }
        })
        (mapFragment.exitTransition as? Transition)?.addListener(object : TransitionListenerAdapter() {
            override fun onTransitionStart(transition: Transition) {
                isMapAnimating = true
            }

            override fun onTransitionEnd(transition: Transition) {
                isMapAnimating = false
                mapAnimationListener?.onMapAnimationFinished(false)
            }
        })

        viewBinding.tabLayout.addOnTabSelectedListener(this)

        viewBinding.viewPager.recyclerView.clipChildren = false
        viewBinding.viewPager.offscreenPageLimit = 3
        viewBinding.viewPager.adapter = MainAdapter(this)

        viewBinding.viewPager.setPageTransformer(PageTransformer(requireContext()))
        viewBinding.viewPager.recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        viewBinding.viewPager.registerOnPageChangeCallback(PageChangeCallback())
        lifecycleScope.launchWhenCreated {
            val startPagePreference = withContext(Dispatchers.IO) {
                PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getString("start_page", null)?.toInt() ?: 0
            }
            viewBinding.viewPager.setCurrentItem(startPagePreference, false)
        }
        ViewCompat.setOnApplyWindowInsetsListener(view.parent as View, this)
    }

    var isMapAnimating: Boolean = false

    var isMapVisible: Boolean
        get() = !childFragmentManager.findFragmentById(R.id.map_container)!!.isHidden
        set(value) {
            val frag = childFragmentManager.findFragmentById(R.id.map_container)!!
            val transaction = childFragmentManager.beginTransaction()
            if (value) {
                transaction.show(frag)
            } else {
                (frag as SlideInMapFragment).setTranslationForTransition()
                transaction.hide(frag)
            }
            transaction.commit()
        }

    override fun onApplyWindowInsets(
        v: View,
        insets: WindowInsetsCompat
    ): WindowInsetsCompat {
        if (backdropFragment?.view != null) {
            ViewCompat.dispatchApplyWindowInsets(backdropFragment!!.requireView(), insets)
        }
        ViewCompat.dispatchApplyWindowInsets(viewBinding.viewPager, insets)
        return ViewCompat.onApplyWindowInsets(requireView(), insets)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        viewBinding.viewPager.setCurrentItem(tab.position, true)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
        tab.view.alpha = 0.0F
        tab.view.animate()
            .alpha(1.0F)
            .start()
        // do nothing
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // do nothing
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        stopsViewModel.setLocationSource(locationViewModel.location)
        tripsViewModel.setLocationSource(locationViewModel.location)
        infoViewModel.setLocationSource(locationViewModel.location)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackBar?.dismiss()
        snackBar = null
    }

    @SuppressLint("ShowToast")
    fun requestSnackBar(textRes: Int, length: Int): Snackbar {
        this.snackBar?.dismiss()
        this.snackBar = Snackbar.make(requireView(), textRes, length)
            .adjustTheme()
        return this.snackBar!!
    }

    fun dismissSnackBar() = this.snackBar?.dismiss()

    interface MapAnimationListener {

        fun onMapAnimationFinished(visible: Boolean)

    }

    inner class PageChangeCallback : ViewPager2.OnPageChangeCallback() {
        private var oldFragment: BackdropFragment? = null
        private var effectivePosition: Int = -1
        private var incomingPosition: Int = -1

        override fun onPageSelected(position: Int) {
            if (viewBinding.tabLayout.selectedTabPosition != position) {
                viewBinding.tabLayout.getTabAt(position)?.select()
            }

            val appearDirection = if (effectivePosition > position) Gravity.START else Gravity.END
            val backdropFragment: BackdropFragment = when (position) {
                0 -> StopsBackdropFragment()
                1 -> TripsBackdropFragment()
                2 -> InfoBackdropFragment()
                3 -> NetworkBackdropFragment()
                else -> throw IllegalStateException()
            }
            val transaction = childFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
            var numSharedElements = 0
            if (this@MainFragment.backdropFragment != null
                && this@MainFragment.backdropFragment?.view != null
            ) {
                val sharedElements =
                    this@MainFragment.backdropFragment?.onCollectSharedElements()
                numSharedElements = sharedElements?.size ?: 0
                sharedElements
                    ?.take(backdropFragment.maxNumSharedElements)
                    ?.forEach { (view, string) ->
                        transaction.addSharedElement(view, string)
                    }
            }

            this@MainFragment.backdropFragment?.applyExitTransition(requireContext())
            if (effectivePosition >= 0) {
                backdropFragment.applyEnterTransition(requireContext(), appearDirection)
            } else {
                backdropFragment.enterTransition = (
                        TransitionInflater.from(requireContext())
                            .inflateTransition(R.transition.fragment_main_enter) as TransitionSet
                        ).apply {
                        val field = ViewDragHelper::class.java.getDeclaredField("sInterpolator")
                        field.isAccessible = true
                        interpolator = field.get(null) as TimeInterpolator

                    }
            }
            transaction.replace(
                viewBinding.backdropContainer.id, backdropFragment,
                FRAGMENT_TAG_BACKDROP
            )

            val actualNumShared = min(numSharedElements, backdropFragment.maxNumSharedElements)
            VolatileTransitionFrame.EXITING_MAX_SHARED = actualNumShared
            VolatileTransitionFrame.ENTERING_MAX_SHARED = actualNumShared
            VolatileTransitionFrame.ENTERING_FRAGMENT = backdropFragment::class.java
            VolatileTransitionFrame.EXITING_FRAGMENT =
                this@MainFragment.backdropFragment?.let { it::class.java }
            transaction.runOnCommit {
                this.oldFragment = this@MainFragment.backdropFragment
                this@MainFragment.backdropFragment = backdropFragment
                this.effectivePosition = position
                this.incomingPosition = -1
                startPostponedEnterTransition()
            }
            if (!isResumed) {
                viewBinding.viewPager.recyclerView.doOnLayout { transaction.commit() }
            } else {
                transaction.commit()
            }
            this.incomingPosition = position
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (positionOffsetPixels == 0) {
                this.oldFragment = null
            }
            val fragment =
                if (this@MainFragment.view != null) this@MainFragment.backdropFragment else oldFragment
            if (position >= effectivePosition) {
                // Settling from/dragging to right
                if (fragment?.view != null) {
                    fragment.getGroupBelowInput()
                        .translationX = -positionOffsetPixels.toFloat()
                }
            } else if (position < effectivePosition) {
                // Settling from/dragging to left
                val view = fragment?.view
                if (view != null) {
                    fragment.getGroupBelowInput()
                        .translationX = view.width - positionOffsetPixels.toFloat()
                }
            }
        }

    }

    class PageTransformer(context: Context) : ViewPager2.PageTransformer {
        private val pageSpacing = 16F.dp(context)

        override fun transformPage(page: View, position: Float) {
            page.translationX = pageSpacing * min(max(position, -1.0F), 1.0F)
        }

    }

    companion object {
        private const val DURATION_IN = 200L
        private const val DURATION_OUT = 100L
        private const val FRAGMENT_TAG_BACKDROP = "backdrop"
    }

}