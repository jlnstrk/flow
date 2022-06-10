package de.julianostarek.flow.ui.common.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.shape.MaterialShapeDrawable
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemMarkerWindowBinding
import de.julianostarek.flow.ui.common.map.display.JourneyDisplay
import de.julianostarek.flow.ui.common.map.display.MapDisplay
import de.julianostarek.flow.ui.common.map.display.TripDisplay
import de.julianostarek.flow.util.text.appendProducts
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.text.appendLineBreak
import de.julianostarek.flow.util.text.formatPlatforms
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.model.stop.Stop
import de.jlnstrk.transit.common.response.JourneyDetailsData
import kotlin.math.roundToInt


abstract class SequenceMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet, defStyleAttr: Int = 0
) : MapView(context, attrs, defStyleAttr),
    OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.InfoWindowAdapter {

    private var currentDisplay: MapDisplay? = null
    internal var markersVisible: Boolean = false
        private set

    private val stopDrawable: BitmapDrawable by lazy {
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.transitpoint_measle,
            null
        ) as BitmapDrawable
    }
    private val changeStopBitmapDescriptor: BitmapDescriptor by lazy {
        val size = 20F.dp(this).roundToInt()
        val scaled = Bitmap.createScaledBitmap(stopDrawable.bitmap, size, size, false)
        BitmapDescriptorFactory.fromBitmap(scaled)
    }
    private val intermediateStopBitmapDescriptor: BitmapDescriptor by lazy {
        val size = 14F.dp(this).roundToInt()
        val scaled = Bitmap.createScaledBitmap(stopDrawable.bitmap, size, size, false)
        BitmapDescriptorFactory.fromBitmap(scaled)
    }

    fun newChangeStop(): MarkerOptions = MarkerOptions()
        .icon(changeStopBitmapDescriptor)
        .anchor(0.5F, 0.5F)

    fun newIntermediateStop(): MarkerOptions = MarkerOptions()
        .icon(intermediateStopBitmapDescriptor)
        .anchor(0.5F, 0.5F)

    fun newPolyline(): PolylineOptions = PolylineOptions()
        .width(8F.dp(this))
        .startCap(RoundCap())
        .endCap(RoundCap())
        .jointType(JointType.ROUND)

    interface Callback {

        fun onMapReady(mapView: SequenceMapView)

    }

    init {
        getMapAsync(this)
    }

    @CallSuper
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.isBuildingsEnabled = true
        googleMap.isIndoorEnabled = false
        googleMap.isTrafficEnabled = false
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isIndoorLevelPickerEnabled = false
        googleMap.setOnCameraMoveListener(this)
        googleMap.setInfoWindowAdapter(this)
    }

    @CallSuper
    override fun onCameraMove() = getMapAsync { googleMap ->
        val shouldBeVisible = googleMap!!.cameraPosition.zoom >= MINOR_MARKER_VISIBILITY_THRESHOLD
        if (markersVisible != shouldBeVisible) {
            markersVisible = shouldBeVisible
            for (marker in currentDisplay?.zoomedMarkers.orEmpty()) {
                marker.isVisible = shouldBeVisible
            }
        }
    }

    override fun getInfoWindow(marker: Marker): View {
        val stop = marker.tag as Stop
        val windowBinding =
            ItemMarkerWindowBinding.inflate(LayoutInflater.from(context), null, false)
        if (stop.location is Location.Station) {
            val stringBuilder = SpannableStringBuilder(stop.location.name)
                .appendProducts(
                    context,
                    (stop.location as Location.Station).products,
                    windowBinding.name
                )
            if (stop is Stop.Arrival && stop.arrivalScheduledPlatform != null
                || stop is Stop.Departure && stop.departureScheduledPlatform != null
            ) {
                stringBuilder
                    .appendLineBreak()
                    .append(stop.formatPlatforms(context))
            }
            windowBinding.name.setText(
                stringBuilder, TextView.BufferType.SPANNABLE
            )
        } else {
            windowBinding.name.text = stop.location.name
        }

        val shape = MaterialShapeDrawable.createWithElevationOverlay(context, 8F.dp(this))
        windowBinding.root.background = shape
        return windowBinding.root
    }

    fun installJourney(journey: JourneyDetailsData?) {
        if (journey != null) {
            install(JourneyDisplay(journey))
        } else if (currentDisplay is JourneyDisplay) {
            currentDisplay?.uninstall()
            currentDisplay = null
        }
    }

    fun installTrip(trip: Trip?) {
        if (trip != null) {
            install(TripDisplay(trip))
        } else if (currentDisplay is TripDisplay) {
            currentDisplay?.uninstall()
            currentDisplay = null
        }
    }

    private fun install(display: MapDisplay) {
        currentDisplay?.uninstall()
        currentDisplay = null
        currentDisplay = display
        getMapAsync { map ->
            if (currentDisplay === display) {
                val bounds = currentDisplay!!.install(this, map)
                onUpdateMapBounds(bounds, false)
            }
        }
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    abstract fun onUpdateMapBounds(bounds: LatLngBounds, shouldAnimate: Boolean)

    companion object {
        private const val MINOR_MARKER_VISIBILITY_THRESHOLD = 13.5F
    }

}