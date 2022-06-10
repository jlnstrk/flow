package de.julianostarek.flow.ui.common.map

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import de.julianostarek.flow.R
import kotlin.math.abs
import kotlin.math.max

class BackdropMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet, defStyleAttr: Int = 0
) : SequenceMapView(context, attrs, defStyleAttr), OnApplyWindowInsetsListener {
    private var topWindowBounds: LatLngBounds? = null
    private var fullWindowBounds: LatLngBounds? = null

    private val uiPadding: Int
    private val contentPadding: Int
    private val topWindowHeight: Int
    private var currentInsets: WindowInsetsCompat? = null

    init {
        val attributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BackdropMapView,
            defStyleAttr,
            0
        )
        try {
            uiPadding = attributes.getDimensionPixelSize(
                R.styleable.BackdropMapView_mapUiPadding, 0
            )
            contentPadding = attributes.getDimensionPixelSize(
                R.styleable.BackdropMapView_mapContentPadding, 0
            )
            topWindowHeight = attributes.getDimensionPixelSize(
                R.styleable.BackdropMapView_topWindowHeight, 0
            )
        } finally {
            attributes.recycle()
        }
        ViewCompat.setOnApplyWindowInsetsListener(this, this)
    }

    override fun onUpdateMapBounds(bounds: LatLngBounds, shouldAnimate: Boolean) {
        this.fullWindowBounds = bounds
        this.topWindowBounds = buildTopWindowBounds(bounds)
        updateMapFocus(shouldAnimate)
    }

    enum class FocusMode {
        FULLSCREEN,
        TOP
    }

    private var focusMode: FocusMode = FocusMode.TOP

    fun setFocusMode(focusMode: FocusMode, animate: Boolean = true) {
        this.focusMode = focusMode
        updateMapFocus(animate)
    }

    private fun updateMapFocus(animate: Boolean) = getMapAsync { map ->
        val bounds = when (focusMode) {
            FocusMode.FULLSCREEN -> fullWindowBounds
            FocusMode.TOP -> topWindowBounds
        } ?: return@getMapAsync
        val update = CameraUpdateFactory.newLatLngBounds(
            bounds,
            contentPadding
        )
        if (animate) {
            map.animateCamera(update, DEFAULT_MAP_CAMERA_DURATION, null)
        } else {
            map.moveCamera(update)
        }
    }

    private fun updateMapPadding() = getMapAsync { map ->
        map.setPadding(
            uiPadding + (currentInsets?.systemWindowInsetLeft ?: 0),
            uiPadding + (currentInsets?.systemWindowInsetTop ?: 0),
            uiPadding + (currentInsets?.systemWindowInsetRight ?: 0),
            uiPadding + (currentInsets?.systemWindowInsetBottom ?: 0)
        )
    }

    private fun buildTopWindowBounds(centerRouteBounds: LatLngBounds): LatLngBounds {
        val north = centerRouteBounds.northeast?.latitude!!
        val west = centerRouteBounds.southwest?.longitude!!
        val east = centerRouteBounds.northeast?.longitude!!
        val south = centerRouteBounds.southwest?.latitude!!
        val centerY = centerRouteBounds.center?.latitude!!

        var topInset = contentPadding
        var leftInset = contentPadding
        var bottomInset = contentPadding
        var rightInset = contentPadding
        if (this.currentInsets != null) {
            bottomInset += currentInsets!!.systemWindowInsetBottom
            topInset += currentInsets!!.systemWindowInsetTop
            leftInset += currentInsets!!.systemWindowInsetLeft
            rightInset += currentInsets!!.systemWindowInsetRight
        }

        val boundsWidthWgs84 = abs(east - west)
        val boundsHeightWgs84 = abs(north - south)
        val viewportWidth = resources.displayMetrics.widthPixels - topInset - bottomInset
        val viewportHeight = topWindowHeight - topInset - contentPadding
        val screenWidth = resources.displayMetrics.widthPixels - leftInset - rightInset
        val screenHeight = resources.displayMetrics.heightPixels - topInset - bottomInset

        val boundsRatio = boundsWidthWgs84 / boundsHeightWgs84
        val viewportRatio = viewportWidth.toDouble() / viewportHeight
        val screenRatio = screenWidth.toDouble() / screenHeight

        val ratio = viewportRatio / screenRatio
        val bias = (boundsRatio / viewportRatio) / 2.0
        val offset = max(0.0, boundsHeightWgs84 * max(1.0, bias)) * ratio

        val artificial = LatLng(centerY - offset, centerRouteBounds.center?.longitude!!)
        return centerRouteBounds.including(artificial)!!
    }

    override fun onApplyWindowInsets(view: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        val oldInsets = this.currentInsets
        this.currentInsets = insets
        if (insets != oldInsets) {
            updateMapFocus(false)
            updateMapPadding()
        }
        return insets
    }

    companion object {
        private const val DEFAULT_MAP_CAMERA_DURATION = 500
    }

}