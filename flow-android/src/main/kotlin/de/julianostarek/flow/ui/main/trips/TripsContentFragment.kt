package de.julianostarek.flow.ui.main.trips

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.backdrop.ContentLayerFragment
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.main.trips.detail.TripDetailSceneFragment
import de.julianostarek.flow.ui.main.trips.recents.RecentRoutesSceneFragment
import de.julianostarek.flow.ui.main.trips.results.TripResultsSceneFragment
import kotlin.reflect.KClass

class TripsContentFragment : ContentLayerFragment<TripsContentFragment.Scene>() {
    override val initialScene: Scene = Scene.RECENT_ROUTES
    private val viewModel: TripsViewModel by activityViewModels()

    override fun onMoveToScene(
        fromScene: Scene,
        toScene: Scene
    ) {
        if (fromScene >= Scene.TRIP_DETAIL && toScene < Scene.TRIP_DETAIL) {
            viewModel.selectedTrip.value = null
        }
        if (fromScene >= Scene.TRIP_RESULTS && toScene < Scene.TRIP_RESULTS) {
            viewModel.trips.value = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.connectionResultsConfigurationChange.observe(viewLifecycleOwner) {
            if (scene.mode == Mode.EXPANDED) {
                mainFragment?.requestSnackBar(
                    R.string.notice_filters_changed,
                    Snackbar.LENGTH_INDEFINITE
                )
                    ?.apply {
                        setAction(R.string.action_refresh_results) {
                            viewModel.searchTrips()
                            dismiss()
                        }
                    }
                    ?.show()
            }
        }
        viewModel.originEqualsDestination.observe(viewLifecycleOwner) {
            mainFragment?.requestSnackBar(
                R.string.error_origin_destination_identical,
                Snackbar.LENGTH_INDEFINITE
            )
                ?.apply {
                    setAction(R.string.action_dismiss) {
                        dismiss()
                    }
                }
                ?.show()
        }
    }

    enum class Scene(
        override val mode: Mode,
        override val type: KClass<out ContentLayerSceneFragment>
    ) : ContentLayerFragment.Scene {
        RECENT_ROUTES(Mode.ANCHORED, RecentRoutesSceneFragment::class),
        TRIP_RESULTS(Mode.EXPANDED, TripResultsSceneFragment::class),
        TRIP_DETAIL(Mode.DRAGGABLE, TripDetailSceneFragment::class)
    }

    companion object {
        internal const val VIEW_TYPE_ROUTE = 1
        internal const val VIEW_TYPE_LOAD_TRIGGER = 2
        internal const val VIEW_TYPE_TRIP = 3
        internal const val VIEW_TYPE_CONNECTION = 4
        internal const val VIEW_TYPE_HEADER = 5
    }

}