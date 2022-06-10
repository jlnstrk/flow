package de.julianostarek.flow.ui.common.mapbackdrop

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.julianostarek.flow.databinding.FragmentSceneMapBackdropBinding
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.mapbackdrop.transition.MapBackdropSceneEnterTransition
import de.julianostarek.flow.ui.common.mapbackdrop.transition.MapBackdropSceneExitTransition
import de.julianostarek.flow.ui.common.map.BackdropMapView
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.max

abstract class MapBackdropSceneFragment : ContentLayerSceneFragment(),
    OnApplyWindowInsetsListener, View.OnClickListener {
    abstract val dragHandle: View
    private val bottomSheetBehavior: BottomSheetBehavior<*>
        get() = BottomSheetBehavior.from(genericViewBinding.bottomSheet)
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
                if (bottomSheetBehavior.isHideable) {
                    bottomSheetBehavior.isHideable = false
                }
                // onBottomSheetStateChanged(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (trackSettleDirection) {
                    if (oldRatio != -1.0F) {
                        genericViewBinding.mapView.setFocusMode(
                            if (slideOffset >= bottomSheetBehavior.halfExpandedRatio
                                || slideOffset - oldRatio > 0F
                            ) {
                                BackdropMapView.FocusMode.TOP
                            } else {
                                BackdropMapView.FocusMode.FULLSCREEN
                            }, !isFirstUpdate)
                        if (isFirstUpdate) {
                            isFirstUpdate = false
                        }
                        trackSettleDirection = false
                        oldRatio = -1.0F
                    } else {
                        oldRatio = slideOffset
                    }
                }
                val paddingTop = max(
                    0F,
                    currentTopInset.toFloat() * (slideOffset - bottomSheetBehavior.halfExpandedRatio) * (1.0F / (1.0F - bottomSheetBehavior.halfExpandedRatio))
                )// currentTopInset * triggerRatio * factor
                if (paddingTop >= 0.0F || bottomSheet.paddingTop > 0.0F) {
                    (bottomSheet as ViewGroup)
                        .forEach {
                            it.translationY = paddingTop
                        }
                }
                if (slideOffset > bottomSheetBehavior.halfExpandedRatio
                    && dragHandle.alpha > 0.0F
                ) {
                    dragHandle.clearAnimation()
                    dragHandle.alpha = 0.0F
                } else if (slideOffset <= bottomSheetBehavior.halfExpandedRatio
                    && dragHandle.alpha == 0.0F
                ) {
                    dragHandle.clearAnimation()
                    dragHandle.animate()
                        .alpha(1.0F)
                        .start()
                }
            }
        }

    abstract override val nestedScrollingChild: RecyclerView
    protected lateinit var genericViewBinding: FragmentSceneMapBackdropBinding

    private var suppressMapLifecycle: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        suppressMapLifecycle = true
        (enterTransition as? Transition)
            ?.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    genericViewBinding.backButton.show()
                    genericViewBinding.infoButton.show()

                    genericViewBinding.mapView.onCreate(savedInstanceState)
                    genericViewBinding.mapView.getMapAsync {
                        it.setOnMapLoadedCallback {
                            genericViewBinding.mapView.animate()
                                .alpha(1.0F)
                                .start()
                        }
                    }
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        genericViewBinding.mapView.onStart()
                    }
                    if (isResumed) {
                        genericViewBinding.mapView.onResume()
                    }
                    onMapViewCreated(genericViewBinding.mapView)
                    suppressMapLifecycle = false
                }
            })
    }

    override fun applyTransitionForContext(context: Context, exit: Boolean, up: Boolean) {
        when {
            exit && up -> exitTransition = MapBackdropSceneExitTransition(context)
            !exit && !up -> enterTransition = MapBackdropSceneEnterTransition(context)
            else -> super.applyTransitionForContext(context, exit, up)
        }
    }

    final override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        genericViewBinding = FragmentSceneMapBackdropBinding.inflate(inflater, container, false)
        onCreateBottomSheetView(inflater, genericViewBinding.bottomSheet, savedInstanceState)
        return genericViewBinding.root
    }

    abstract fun onCreateBottomSheetView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

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
            genericViewBinding.mapView.onResume()
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

    protected fun startBottomSheetSettling() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    open fun onMapViewCreated(mapView: BackdropMapView) = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view, this)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        genericViewBinding.backButton.setOnClickListener(this)
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
        bottomSheetBehavior.halfExpandedRatio = 1.0F - (MAP_HEIGHT_DIP.dp(this)
                / (resources.displayMetrics.heightPixels - systemBarInsets.bottom))
        this.currentTopInset = systemBarInsets.top
        nestedScrollingChild.updatePadding(bottom = genericViewBinding.footer.height + systemBarInsets.top)
        /*mapView?.updateMapPadding(insets.systemWindowInsetTop, sheetBehavior.peekHeight,
            ((1.0F - sheetBehavior.halfExpandedRatio) * resources.displayMetrics.heightPixels).roundToInt())*/
        return ViewCompat.dispatchApplyWindowInsets(genericViewBinding.mapView, insets)
    }

    companion object {
        private const val MAP_HEIGHT_DIP = 256F
    }

}