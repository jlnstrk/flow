package de.julianostarek.flow.ui.main.map

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentMapSlideInBinding
import de.julianostarek.flow.ui.common.backdrop.BackdropFragment
import de.julianostarek.flow.ui.common.map.BackdropMapView
import de.julianostarek.flow.ui.main.stops.StopsViewModel
import de.julianostarek.flow.ui.main.trips.TripsViewModel
import de.julianostarek.flow.util.graphics.dp
import de.jlnstrk.transit.common.response.base.ServiceResult.Success

class SlideInMapFragment : Fragment(),
    OnApplyWindowInsetsListener, View.OnClickListener {
    private var currentTopInset: Int = 0
    private val bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            private var isFirstUpdate: Boolean = true
            private var trackSettleDirection: Boolean = false
            private var oldRatio: Float = -1.0F

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    trackSettleDirection = true
                }
                // onBottomSheetStateChanged(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (trackSettleDirection) {
                    if (oldRatio != -1.0F) {
                        /*genericViewBinding.mapView.setFocusMode(
                            if (slideOffset >= bottomSheetBehavior.halfExpandedRatio
                                || slideOffset - oldRatio > 0F
                            ) {
                                BackdropMapView.FocusMode.TOP
                            } else {
                                BackdropMapView.FocusMode.FULLSCREEN
                            }, !isFirstUpdate)*/
                        if (isFirstUpdate) {
                            isFirstUpdate = false
                        }
                        trackSettleDirection = false
                        oldRatio = -1.0F
                    } else {
                        oldRatio = slideOffset
                    }
                }
                /*val paddingTop = max(
                    0F,
                    currentTopInset.toFloat() * (slideOffset - bottomSheetBehavior.halfExpandedRatio) * (1.0F / (1.0F - bottomSheetBehavior.halfExpandedRatio))
                )// currentTopInset * triggerRatio * factor
                if (paddingTop >= 0.0F || bottomSheet.paddingTop > 0.0F) {
                    (bottomSheet as ViewGroup)
                        .forEach {
                            it.translationY = paddingTop
                        }
                }*/
            }
        }

    private lateinit var genericViewBinding: FragmentMapSlideInBinding

    private val mapViewModel: MapControlViewModel by activityViewModels()
    private val stopsViewModel: StopsViewModel by activityViewModels()
    private val tripsViewModeL: TripsViewModel by activityViewModels()

    private var suppressMapLifecycle: Boolean = false

    internal val mapView: BackdropMapView
        get() = genericViewBinding.mapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = Slide(Gravity.TOP).apply {
            duration = 500
            interpolator = BackdropFragment.BOTTOM_SHEET_INTERPOLATOR
        }
        exitTransition = Slide(Gravity.TOP).apply {
            duration = 500
            interpolator = AccelerateInterpolator()
        }
        // suppressMapLifecycle = true
        (enterTransition as? Transition)
            ?.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    view?.post {
                        genericViewBinding.mapView.translationY = 0F
                        requireView().translationY = 0F
                    }
                    genericViewBinding.backButton.show()
                    genericViewBinding.infoButton.show()
                }
            })

        (exitTransition as? Transition)
            ?.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionStart(transition: Transition) {
                    genericViewBinding.backButton.hide()
                    genericViewBinding.infoButton.hide()
                }
                override fun onTransitionEnd(transition: Transition) {
                    view?.post(::setTranslationForTransition)
                }
            })
    }

    fun setTranslationForTransition() {
        val absTranslation = requireView().measuredHeight - 304F.dp(this@SlideInMapFragment)
        genericViewBinding.mapView.translationY = absTranslation
        requireView().translationY = -absTranslation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        genericViewBinding = FragmentMapSlideInBinding.inflate(inflater, container, false)
        return genericViewBinding.root
    }

    override fun onStart() {
        super.onStart()
        if (!suppressMapLifecycle) {
            genericViewBinding.mapView.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!suppressMapLifecycle) {
            genericViewBinding.mapView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!suppressMapLifecycle) {
            genericViewBinding.mapView.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!suppressMapLifecycle) {
            genericViewBinding.mapView.onStop()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!suppressMapLifecycle) {
            genericViewBinding.mapView.onSaveInstanceState(outState)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (!suppressMapLifecycle) {
            genericViewBinding.mapView.onLowMemory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!suppressMapLifecycle) {
            genericViewBinding.mapView.onDestroy()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ViewGroup).clipChildren = false
        view.background = MaterialShapeDrawable.createWithElevationOverlay(requireContext())
            .apply {
                shapeAppearanceModel = ShapeAppearanceModel.builder(requireContext(), R.style.ShapeAppearance_Sequence_LargeComponent, Resources.ID_NULL)
                    .setBottomLeftCornerSize(24F.dp(this@SlideInMapFragment))
                    .setBottomRightCornerSize(24F.dp(this@SlideInMapFragment))
                    .build()
            }
        view.clipToOutline = true
        view.outlineProvider = ViewOutlineProvider.BACKGROUND
        ViewCompat.setOnApplyWindowInsetsListener(view, this)
        genericViewBinding.backButton.setOnClickListener(this)
        genericViewBinding.mapView.onCreate(savedInstanceState)

        mapViewModel.focusMode.observe(viewLifecycleOwner) { focusMode ->
            genericViewBinding.mapView.setFocusMode(focusMode, animate = isResumed)
        }
        stopsViewModel.journeyDetails.observe(viewLifecycleOwner) { response ->
            if (response is Success) {
                genericViewBinding.mapView.installJourney(response.result)
            }
        }
        tripsViewModeL.selectedTrip.observe(viewLifecycleOwner) { trip ->
            if (trip != null) {
                genericViewBinding.mapView.installTrip(trip)
            }
        }

        view.doOnLayout { setTranslationForTransition() }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            genericViewBinding.backButton.id -> activity?.onBackPressed()
        }
    }

    override fun onApplyWindowInsets(view: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val insetsTopF = systemBarInsets.top.toFloat()
        genericViewBinding.backButton.translationY = insetsTopF
        genericViewBinding.infoButton.translationY = insetsTopF
        this.currentTopInset = systemBarInsets.top
        /*mapView?.updateMapPadding(insets.systemWindowInsetTop, sheetBehavior.peekHeight,
            ((1.0F - sheetBehavior.halfExpandedRatio) * resources.displayMetrics.heightPixels).roundToInt())*/
        return ViewCompat.dispatchApplyWindowInsets(genericViewBinding.mapView, insets)
    }

    companion object {
        private const val MAP_HEIGHT_DIP = 256F
    }

}