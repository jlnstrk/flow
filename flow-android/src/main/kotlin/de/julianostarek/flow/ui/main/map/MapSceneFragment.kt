package de.julianostarek.flow.ui.main.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.jlnstrk.transit.common.model.LineGeometry
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.response.base.ServiceResult

class MapSceneFragment : ContentLayerSceneFragment(), GoogleMap.OnCameraIdleListener {
    private val viewModel: MapControlViewModel by activityViewModels()
    override val nestedScrollingChild: RecyclerView
        get() = throw UnsupportedOperationException()

    private inline fun requireMapView(): MapView = requireView() as MapView

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MapView(inflater.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onCameraIdle() {
        if (viewModel.geometries.value is ServiceResult.Success) {
            requireMapView().getMapAsync { googleMap ->
                redrawPolylines(
                    googleMap,
                    (viewModel.geometries.value as ServiceResult.Success).result.geometries[TransportMode.SUBWAY]!!
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireMapView().onCreate(savedInstanceState)
        requireMapView().getMapAsync { googleMap ->
            googleMap.setOnCameraIdleListener(this)
        }
        viewModel.geometries.observe(viewLifecycleOwner) {
            if (it is ServiceResult.Success) {
                requireMapView().getMapAsync { googleMap ->
                    redrawPolylines(googleMap, it.result.geometries[TransportMode.SUBWAY]!!)
                }
            }
        }
    }

    private fun redrawPolylines(googleMap: GoogleMap, geometries: List<LineGeometry>) {
        googleMap.clear()

        /*val sample = LatLng(0.0, 0.0)

        val anchor = googleMap.projection.toScreenLocation(sample)
        val anchorPlusDp = googleMap.projection.fromScreenLocation(Point(anchor.x, anchor.y + googleMap.cameraPosition.zoom.dp(this).roundToInt()))
        val offset = sqrt((anchorPlusDp.longitude - sample.longitude).pow(2) +
                (anchorPlusDp.latitude - sample.latitude).pow(2))

        geometries.forEach { (lines, polyline) ->
            val center = Polyline()
            polyline.coordinates.forEachIndexed { index, coordinates ->
                if (index == 0) {
                    center.startPath(coordinates.longitude, coordinates.latitude)
                } else {
                    center.lineTo(coordinates.longitude, coordinates.latitude)
                }
            }

            googleMap.addPolyline(PolylineOptions()
                .addAll(polyline.coordinates.map(Coord::asMaps)))
            lines.forEachIndexed { index, line ->
                val individualOffset = if (lines.size % 2 == 0) {
                    (index - (lines.size / 2)) * offset + 0.5 * offset
                } else {
                    (index - (lines.size / 2)) * offset
                }

                println("off $offset, individual $individualOffset")

                val resulting: Geometry = if (individualOffset == 0.0) center else
                    offsetOperator.execute(
                    center, null, individualOffset, OperatorOffset.JoinType.Round,
                    0.0, offset, null
                )
                val points = arrayOfNulls<com.esri.core.geometry.Point>((resulting as Polyline).pointCount)
                (resulting as Polyline).queryCoordinates(points)
                if (!resulting.isEmpty) {
                    val innerLine = PolylineOptions()
                        .addAll(points.map { LatLng(it!!.y, it.x) })
                        .color(Random().nextInt() or 0xFF000000.toInt())
                        .width(1F.dp(this))
                    googleMap.addPolyline(innerLine)
                }
            }
        }*/
        /*val basePolyline = PolylineOptions()
            .width(3F.dp(this))
        googleMap.addPolyline(PolylineOptions()
                    .width(basePolyline.width * 2)
                    .addAll(basePolyline.points)
                    .color(Color.WHITE))
        googleMap.addPolyline(it)*/
    }

    override fun onStart() {
        super.onStart()
        requireMapView().onStart()
    }

    override fun onResume() {
        super.onResume()
        requireMapView().onResume()
    }

    override fun onPause() {
        super.onPause()
        requireMapView().onPause()
    }

    override fun onStop() {
        super.onStop()
        requireMapView().onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireMapView().onDestroy()
    }


}