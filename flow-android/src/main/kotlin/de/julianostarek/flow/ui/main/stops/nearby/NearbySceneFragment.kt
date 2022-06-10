package de.julianostarek.flow.ui.main.stops.nearby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.FadingDividerItemDecoration
import com.google.android.material.divider.MaterialDividerItemDecoration
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentSceneStopsNearbyStationsBinding
import de.julianostarek.flow.ui.common.adapter.LoadStateAdapter
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.view.LoadStateIndicator
import de.julianostarek.flow.ui.main.stops.StopsContentFragment
import de.julianostarek.flow.ui.main.stops.StopsViewModel
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.NearbyLocationsService
import de.jlnstrk.transit.common.service.StationBoardService
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class NearbySceneFragment : ContentLayerSceneFragment(),
    Toolbar.OnMenuItemClickListener,
    NearbyLocationsAdapter.Listener,
    NearbyDeparturesAdapter.Listener {
    private lateinit var viewBinding: FragmentSceneStopsNearbyStationsBinding
    override val nestedScrollingChild: RecyclerView get() = viewBinding.recyclerView

    private val nearbyLocationsLoadState = LoadStateAdapter()
    private val nearbyLocationsSection = NearbyLocationsAdapter(this)
    private val nearbyDeparturesLoadState = LoadStateAdapter()
    private val nearbyDeparturesSection = NearbyDeparturesAdapter(this)
    private val nearbyAdapter = ConcatAdapter()

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    private val viewModel: StopsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        itemDecoration = FadingDividerItemDecoration(
            requireContext(),
            MaterialDividerItemDecoration.VERTICAL
        ).apply {
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.keyline_fg_plus56dp)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.keyline_fg)
        }
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneStopsNearbyStationsBinding.inflate(
            inflater, container,
            false
        )
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (enterTransition is SceneFragmentEnterUpTransition) {
            postponeEnterTransition()
        }
        viewBinding.recyclerView.setHasFixedSize(true)
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.addItemDecoration(itemDecoration)
        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        viewBinding.recyclerView.adapter = nearbyAdapter
        viewBinding.toolbar.setOnMenuItemClickListener(this)

        viewModel.nearbyLocationsLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                nearbyLocationsLoadState.loadState = LoadStateIndicator.State.Loading(
                    captionRes = R.string.state_loading_locations_nearby
                )
            }
        }
        viewModel.nearbyDeparturesLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                nearbyDeparturesLoadState.loadState = LoadStateIndicator.State.Loading(
                    captionRes = R.string.state_loading_locations_nearby
                )
            }
        }
        viewModel.nearbyLocationsRefreshing.observe(viewLifecycleOwner) { refreshing ->
            if (refreshing) {
                viewBinding.refreshing.show()
            } else {
                viewBinding.refreshing.hide()
            }
        }
        viewModel.nearbyDeparturesRefreshing.observe(viewLifecycleOwner) { refreshing ->
            if (refreshing) {
                viewBinding.refreshing.show()
            } else {
                viewBinding.refreshing.hide()
            }
        }
        viewModel.nearbyLocations.observe(viewLifecycleOwner) { response ->
            viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
            when (response) {
                is ServiceResult.Success -> {
                    nearbyLocationsLoadState.loadState = LoadStateIndicator.State.Hidden
                    nearbyLocationsSection.submitList(response.result.locations)
                }
                else -> when {
                    (response is ServiceResult.Failure && response.error == NearbyLocationsService.Error.INVALID_LOCATION)
                            || response is ServiceResult.NoResult -> {
                        nearbyLocationsLoadState.loadState = LoadStateIndicator.State.Error(
                            R.drawable.ic_state_no_result_40dp,
                            R.string.error_too_far_locations
                        )
                        nearbyLocationsSection.submitList(null)
                    }
                    else -> {
                        nearbyLocationsLoadState.loadState = LoadStateIndicator.State.Error(
                            R.drawable.ic_state_error_40dp, R.string.error_unknown
                        )
                    }
                }
            }
        }
        viewModel.nearbyDepartures.observe(viewLifecycleOwner) { response ->
            viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
            when (response) {
                is ServiceResult.Success -> {
                    nearbyDeparturesLoadState.loadState = LoadStateIndicator.State.Hidden
                    nearbyDeparturesSection.submitDepartures(response.result.journeys)
                }
                else -> when {
                    (response is ServiceResult.Failure && response.error == StationBoardService.Error.INVALID_LOCATION)
                            || response is ServiceResult.NoResult -> {
                        nearbyDeparturesLoadState.loadState = LoadStateIndicator.State.Error(
                            R.drawable.ic_state_no_result_40dp,
                            R.string.error_too_far_departures
                        )
                        nearbyDeparturesSection.submitList(null)
                    }
                    else -> {
                        nearbyDeparturesLoadState.loadState = LoadStateIndicator.State.Error(
                            R.drawable.ic_state_error_40dp, R.string.error_unknown
                        )
                    }
                }
            }
        }
        viewModel.deviceLocation.observe(viewLifecycleOwner) { deviceLocation ->
            if (deviceLocation != null) {
                nearbyLocationsSection.onReferenceLocationChanged(deviceLocation)
                nearbyDeparturesSection.onReferenceLocationChanged(deviceLocation)
            }
        }
        viewModel.nearbyViewMode.observe(viewLifecycleOwner) { viewMode ->
            while (nearbyAdapter.adapters.isNotEmpty()) {
                nearbyAdapter.removeAdapter(nearbyAdapter.adapters.last())
            }
            when (viewMode) {
                StopsViewModel.NearbyViewMode.DEPARTURES, null -> {
                    nearbyAdapter.addAdapter(nearbyDeparturesLoadState)
                    nearbyAdapter.addAdapter(nearbyDeparturesSection)
                }
                StopsViewModel.NearbyViewMode.LOCATIONS -> {
                    nearbyAdapter.addAdapter(nearbyLocationsLoadState)
                    nearbyAdapter.addAdapter(nearbyLocationsSection)
                }
            }
            viewBinding.toolbar.menu.findItem(R.id.action_change_view)
                .setIcon(
                    when (viewMode) {
                        StopsViewModel.NearbyViewMode.DEPARTURES, null -> R.drawable.ic_view_rows_short_24dp
                        StopsViewModel.NearbyViewMode.LOCATIONS -> R.drawable.ic_view_rows_tall_24dp
                    }
                )
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_tune -> {
                contentLayer?.setShifted(contentLayer?.isShifted == false)
                return false
            }
            R.id.action_change_view -> {
                viewModel.toggleNearbyViewMode()
                return true
            }
            R.id.action_refresh -> {
                viewModel.refreshNearby()
                return true
            }
            else -> return false
        }
    }

    override fun onContentLayerShiftChanged(isShifted: Boolean) {
        val menuItem = viewBinding.toolbar.menu.findItem(R.id.action_tune)
        val iconRes: Int
        if (isShifted) {
            iconRes = R.drawable.ic_expand_less_24dp
        } else {
            iconRes = if (viewModel.productFilter.isDefaultFilter()) {
                R.drawable.ic_filter_list_24dp
            } else R.drawable.ic_state_modified_24dp
        }
        menuItem.setIcon(iconRes)
    }

    override fun onLocationClicked(location: Location) {
        viewModel.selectNearbyLocation(location)
        (contentLayer as? StopsContentFragment)
            ?.setScene(StopsContentFragment.Scene.STATION_BOARD)
    }

    override fun onJourneyClicked(journey: Journey) {
        (contentLayer as? StopsContentFragment)
            ?.setScene(StopsContentFragment.Scene.JOURNEY_DETAILS)
        viewModel.loadJourneyDetails(journey)
    }

}