package de.julianostarek.flow.ui.common.backdrop

import android.animation.ValueAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.behavior.NestedScrollingBottomSheetBehavior
import de.julianostarek.flow.ui.common.map.BackdropMapView
import de.julianostarek.flow.ui.main.MainFragment
import de.julianostarek.flow.ui.main.map.MapControlViewModel
import de.julianostarek.flow.util.graphics.dp
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.set
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.reflect.KClass

abstract class ContentLayerFragment<S : ContentLayerFragment.Scene> : Fragment(),
    OnApplyWindowInsetsListener,
    MainFragment.MapAnimationListener {
    internal val mainFragment: MainFragment?
        get() = parentFragment as? MainFragment

    private val mapControl: MapControlViewModel by activityViewModels()

    protected fun requireMain(): MainFragment = parentFragment as MainFragment

    internal fun findBackdrop(): BackdropFragment? {
        val backdrop = mainFragment?.backdropFragment
        if (backdrop?.findContentLayer() === this)
            return backdrop
        return null
    }

    protected fun requireBackdrop(): BackdropFragment = requireMain().backdropFragment!!

    internal val bottomSheetBehavior: NestedScrollingBottomSheetBehavior<*>
        get() = BottomSheetBehavior.from((requireView() as ViewGroup).getChildAt(0))
                as NestedScrollingBottomSheetBehavior<*>

    private val behaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        private var trackSettleDirection: Boolean = false
        private var oldRatio: Float = -1.0F

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val factor = 1.0F - min(slideOffset + 1.0F, 1.0F)
            val maxPadding = requireMain().requireView().measuredHeight -
                    bottomSheetBehavior.peekHeight
            setContentLayerOffsetInternal((factor * maxPadding).roundToInt())

            if (trackSettleDirection) {
                if (oldRatio != -1.0F) {

                    mapControl.setFocusMode(
                        when {
                            slideOffset >= bottomSheetBehavior.halfExpandedRatio
                                    || slideOffset - oldRatio > 0F -> BackdropMapView.FocusMode.TOP
                            else -> BackdropMapView.FocusMode.FULLSCREEN
                        }
                    )
                    trackSettleDirection = false
                    oldRatio = -1.0F
                } else {
                    oldRatio = slideOffset
                }
            }

            val paddingTop = max(
                0F, topInsets.toFloat() *
                        (slideOffset - bottomSheetBehavior.halfExpandedRatio) *
                        (1.0F / (1.0F - bottomSheetBehavior.halfExpandedRatio))
            )
            if (paddingTop >= 0.0F || bottomSheet.paddingTop > 0.0F) {
                for (child in (bottomSheet as ViewGroup)) {
                    child.translationY = paddingTop
                }
            }
            /*if (slideOffset > bottomSheetBehavior.halfExpandedRatio
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
            }*/
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_SETTLING) {
                trackSettleDirection = true
            }
            if (newState != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.isHideable = false
            }
            if (scene.mode != effectiveMode) {
                // Apply new mode if settled to desired state
                if ((newState == BottomSheetBehavior.STATE_COLLAPSED
                            && (scene.mode == Mode.ANCHORED
                            || scene.mode == Mode.EXPANDED
                            || scene.mode == Mode.DRAGGABLE))
                    || (newState == BottomSheetBehavior.STATE_HALF_EXPANDED
                            && (scene.mode == Mode.DRAGGABLE))
                    || (newState == BottomSheetBehavior.STATE_EXPANDED
                            && (scene.mode == Mode.IMMERSED))
                ) {
                    // Animation lag workaround, wait for map to get off screen before applying
                    if (requireMain().isMapAnimating) {
                        requireMain().mapAnimationListener = this@ContentLayerFragment
                    } else {
                        applyMode(force = true)
                    }
                }
            }
        }
    }

    override fun onMapAnimationFinished(visible: Boolean) {
        applyMode(force = true)
    }

    abstract val initialScene: S

    lateinit var scene: S
        private set
    var effectiveMode: Mode? = null
        private set
    var isShifted: Boolean = false
        private set
    private var topInsets: Int = 0
    var bottomOffset: Int = 0
        private set

    private val sceneBackStack = LinkedList<S>()
    private val sceneInstances = LinkedHashMap<S, WeakReference<ContentLayerSceneFragment>>()

    private val fragmentContainerId: Int
        get() = (requireView() as ViewGroup).getChildAt(0).id

    internal val currentScene: ContentLayerSceneFragment?
        get() = childFragmentManager.findFragmentById(fragmentContainerId) as ContentLayerSceneFragment?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scene = initialScene
    }

    final override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_content_layer, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.doOnAttach { (view.parent as ViewGroup).clipChildren = false }
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES) {
            bottomSheetBehavior.toggleLightStatusOnAnimation()
        }
        bottomSheetBehavior.addBottomSheetCallback(behaviorCallback)
        ViewCompat.setOnApplyWindowInsetsListener(view, this)
    }

    fun navigateUp(): Boolean {
        if (isShifted) {
            setShifted(false)
            return true
        }
        if (sceneBackStack.isNotEmpty()) {
            val last = sceneBackStack.pop()
            setScene(last, isPop = true)
            return true
        }
        return false
    }

    @CallSuper
    open fun setScene(scene: S, isPop: Boolean = false) {
        setShiftedInternal(false)
        if (this.scene == scene)
            return
        onMoveToScene(this.scene, scene)
        if (!isPop) {
            sceneBackStack.push(this.scene)
        }
        this.scene = scene
        mainFragment?.viewBinding?.viewPager?.isUserInputEnabled = scene.mode == Mode.ANCHORED
        mainFragment?.dismissSnackBar()
        when (scene.mode) {
            Mode.ANCHORED -> {
                mainFragment?.setContextualToolbarVisible(false)
            }
            Mode.EXPANDED -> {
                mainFragment?.setContextualToolbarVisible(true)

            }
            Mode.IMMERSED -> {

            }
            Mode.DRAGGABLE -> {
                if (mainFragment?.isMapVisible != true) {
                    mainFragment?.isMapVisible = true
                }
            }
        }
        if (scene.mode != Mode.DRAGGABLE && mainFragment?.isMapVisible == true) {
            mainFragment?.isMapVisible = false
        }
        applyMode()
        view?.post(::invalidate)
    }

    private fun getOrInstantiateSceneFragment(): ContentLayerSceneFragment {
        val existing = sceneInstances[scene]?.get()
        if (existing != null) {
            return existing
        }
        val newInstance = scene.newInstance()
        sceneInstances[scene] = WeakReference(newInstance)
        return newInstance
    }

    @CallSuper
    protected open fun applyMode(force: Boolean = false) {
        if (this.effectiveMode == scene.mode)
            return
        val isDown = effectiveMode == null || scene.mode > this.effectiveMode!!
        if (!force) {
            currentScene?.let {
                it.applyTransitionForContext(requireContext(), exit = true, up = !isDown)
                childFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(it)
                    .commit()
            }
            return
        }

        val oldScene = currentScene
        this.effectiveMode = scene.mode
        if (view != null) {
            val newScene = getOrInstantiateSceneFragment()
            newScene.applyTransitionForContext(requireContext(), exit = false, up = !isDown)
            oldScene?.applyTransitionForContext(requireContext(), exit = true, up = !isDown)
            childFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_content_layer, newScene)
                .runOnCommit {
                    bottomSheetBehavior.nestedScrollingChild =
                        if (effectiveMode == Mode.DRAGGABLE) newScene.nestedScrollingChild else null
                }
                .commit()
        }
    }

    @CallSuper
    open fun setShifted(shifted: Boolean) {
        check(scene.mode != Mode.IMMERSED) { "Cannot toggle shifted when immersed" }
        if (this.isShifted != shifted) {
            setShiftedInternal(shifted)
            invalidate()
        }
    }

    @CallSuper
    protected open fun setShiftedInternal(shifted: Boolean) {
        if (this.isShifted != shifted) {
            this.isShifted = shifted
            mainFragment?.viewBinding?.viewPager?.isUserInputEnabled = !shifted
            currentScene?.view?.post { currentScene?.onContentLayerShiftChanged(shifted) }
            findBackdrop()?.let {
                requireView().post { it.onContentLayerShiftChanged(shifted) }
            }
        }
    }

    @CallSuper
    protected fun setContentLayerOffsetInternal(offset: Int) {
        currentScene?.view?.post { currentScene?.onContentLayerOffsetChanged(offset) }
        findBackdrop()?.let {
            requireView().post { it.onContentLayerOffsetChanged(offset) }
        }
    }

    final override fun onApplyWindowInsets(
        v: View,
        insets: WindowInsetsCompat
    ): WindowInsetsCompat {
        this.topInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        if (scene.mode == Mode.IMMERSED) {
            val sceneView = (v as? ViewGroup)?.getChildAt(0)
            if (sceneView != null) {
                return ViewCompat.dispatchApplyWindowInsets(sceneView, insets)
            }
        }
        return insets
    }

    fun invalidateAnchorPosition() {
        if (scene.mode == Mode.ANCHORED
            || (scene.mode == Mode.EXPANDED && isShifted)
        ) {
            invalidate()
        }
    }

    private fun invalidate() {
        if (view == null)
            return
        val minimumHeight = 48F.dp(this).roundToInt()
        var desiredHeight = requireMain().maximumContentLayerHeight - topInsets

        when {
            isShifted -> {
                desiredHeight -= findBackdrop()!!.getRevealedOffset()
            }
            scene.mode == Mode.ANCHORED -> {
                desiredHeight -= findBackdrop()!!.getOffsetForState(scene.mode)
            }
            scene.mode == Mode.DRAGGABLE -> {
                desiredHeight = minimumHeight
            }
        }

        val halfExpandedRatio = if (scene.mode == Mode.DRAGGABLE) {
            (requireMain().requireView().measuredHeight - 256F.dp(this)) /
                    requireMain().requireView().measuredHeight
        } else Float.MIN_VALUE

        val computedHeight = max(desiredHeight, minimumHeight)
        val newBehaviorState = when (scene.mode) {
            Mode.ANCHORED,
            Mode.EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
            Mode.DRAGGABLE -> BottomSheetBehavior.STATE_HALF_EXPANDED
            Mode.IMMERSED -> BottomSheetBehavior.STATE_EXPANDED
        }

        fun invalidatePostLayout() {
            if (requireView().isInLayout || requireView().isLayoutRequested) {
                requireView().post(::invalidatePostLayout)
                return
            }
            invalidateSheet(newBehaviorState, computedHeight, halfExpandedRatio)
        }

        invalidatePostLayout()
    }

    private fun invalidateSheet(newState: Int, newPeekHeight: Int, newHalfExpandedRatio: Float) {
        val animate = bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.setPeekHeight(newPeekHeight, animate)

        val changeRatioAfterState =
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED
                    && newState != BottomSheetBehavior.STATE_HALF_EXPANDED
        if (!changeRatioAfterState) {
            bottomSheetBehavior.halfExpandedRatio = newHalfExpandedRatio
        }
        bottomSheetBehavior.state = newState
        if (changeRatioAfterState) {
            bottomSheetBehavior.halfExpandedRatio = newHalfExpandedRatio
        }

        bottomSheetBehavior.isDraggable = scene.mode == Mode.DRAGGABLE
        if (mainFragment != null) {
            this.bottomOffset = if (scene.mode != Mode.IMMERSED) {
                requireMain().requireView().measuredHeight - newPeekHeight
            } else 0
        }
        setContentLayerOffsetInternal(bottomOffset)
    }

    protected open fun onMoveToScene(fromScene: S, toScene: S) = Unit

    interface Anchor : ContentLayerSceneCallbacks {
        fun getOffsetForState(layerMode: Mode): Int

        fun getRevealedOffset(): Int
    }

    enum class Mode {
        ANCHORED, EXPANDED, DRAGGABLE, IMMERSED
    }

    interface Scene {
        val mode: Mode
        val type: KClass<out ContentLayerSceneFragment>

        operator fun compareTo(other: Scene): Int {
            return mode.compareTo(other.mode)
        }

        fun newInstance(): ContentLayerSceneFragment = type.java.newInstance()

    }

    private fun BottomSheetBehavior<*>.toggleLightStatusOnAnimation() {
        val insetsController =
            WindowCompat.getInsetsController(requireActivity().window, requireView())
        val updateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            val movingUp = true
            val fraction = when {
                movingUp -> animation.animatedFraction
                else -> 1.0F - animation.animatedFraction
            }
            insetsController?.isAppearanceLightStatusBars = fraction >= if (movingUp) 0.25 else 0.75
        }
        interpolatorAnimator.addUpdateListener(updateListener)
    }


    companion object {
        private val interpolatorAnimator_ = BottomSheetBehavior::class.java
            .getDeclaredField("interpolatorAnimator")

        init {
            interpolatorAnimator_.isAccessible = true
        }

        private val BottomSheetBehavior<*>.interpolatorAnimator: ValueAnimator
            get() = interpolatorAnimator_.get(this) as ValueAnimator
    }

}