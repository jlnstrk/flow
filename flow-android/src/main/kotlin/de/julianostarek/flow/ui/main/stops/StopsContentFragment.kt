package de.julianostarek.flow.ui.main.stops

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.backdrop.ContentLayerFragment
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.main.stops.journeydetail.JourneyDetailSceneFragment
import de.julianostarek.flow.ui.main.stops.nearby.NearbySceneFragment
import de.julianostarek.flow.ui.main.stops.stationboard.StationBoardSceneFragment
import kotlin.reflect.KClass

class StopsContentFragment : ContentLayerFragment<StopsContentFragment.Scene>() {
    override val initialScene: Scene = Scene.NEARBY
    private val viewModel: StopsViewModel by activityViewModels()

    override fun onMoveToScene(fromScene: Scene, toScene: Scene) {
        if (fromScene >= Scene.JOURNEY_DETAILS && toScene < Scene.JOURNEY_DETAILS) {
            viewModel.journeyDetails.value = null
        }
        if (fromScene >= Scene.STATION_BOARD && toScene < Scene.STATION_BOARD) {
            viewModel.stationBoard.value = null
            viewModel.location.value = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.nearbyConfigurationChanged.observe(viewLifecycleOwner) { changed ->
            if (changed && scene == Scene.NEARBY) {
                mainFragment?.requestSnackBar(
                    R.string.notice_filters_changed,
                    Snackbar.LENGTH_INDEFINITE
                )?.setAction(R.string.action_refresh_results) { viewModel.refreshNearby() }
                    ?.show()
            }
        }
        viewModel.stationBoardConfigurationChanged.observe(viewLifecycleOwner) { changed ->
            if (changed && scene == Scene.STATION_BOARD) {
                mainFragment?.requestSnackBar(
                    R.string.notice_filters_changed,
                    Snackbar.LENGTH_INDEFINITE
                )
                    ?.setAction(R.string.action_refresh_results) {
                        viewModel.refreshStationBoard()
                    }
                    ?.show()
            }
        }
    }

    enum class Scene(
        override val mode: Mode,
        override val type: KClass<out ContentLayerSceneFragment>
    ) : ContentLayerFragment.Scene {
        NEARBY(Mode.ANCHORED, NearbySceneFragment::class),
        STATION_BOARD(Mode.EXPANDED, StationBoardSceneFragment::class),
        JOURNEY_DETAILS(Mode.DRAGGABLE, JourneyDetailSceneFragment::class)
    }

}