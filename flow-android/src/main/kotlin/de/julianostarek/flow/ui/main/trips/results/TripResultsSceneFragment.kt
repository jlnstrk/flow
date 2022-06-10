package de.julianostarek.flow.ui.main.trips.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentSceneTripsResultsBinding
import de.julianostarek.flow.ui.main.trips.TripsViewModel
import de.julianostarek.flow.ui.main.trips.TripsContentFragment
import de.julianostarek.flow.ui.main.trips.results.simple.LoadTriggerAdapter
import de.julianostarek.flow.ui.main.trips.results.simple.SimpleTripAdapter
import de.julianostarek.flow.ui.main.trips.results.timeline.TimelineAdapter
import de.julianostarek.flow.ui.main.trips.results.timeline.TimelineLayoutManager
import de.julianostarek.flow.ui.main.trips.results.timeline.TimelineSnapHelper
import de.julianostarek.flow.ui.main.trips.results.timeline.TimelineItemDecoration
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.adapter.LoadStateAdapter
import de.julianostarek.flow.ui.common.decor.VerticalGridSpacingItemDecoration
import de.julianostarek.flow.ui.common.view.LoadStateIndicator
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import de.julianostarek.flow.util.text.messageRes
import de.jlnstrk.transit.common.model.Trip
import de.jlnstrk.transit.common.response.TripSearchData
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.TripSearchResult
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class TripResultsSceneFragment : ContentLayerSceneFragment(),
    Toolbar.OnMenuItemClickListener,
    SimpleTripAdapter.Listener,
    TimelineAdapter.Listener {
    private val viewModel: TripsViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentSceneTripsResultsBinding
    override val nestedScrollingChild: RecyclerView
        get() = viewBinding.recyclerView

    private val loadStateSection = LoadStateAdapter()

    /* Timeline view */
    private val timelineAdapter = TimelineAdapter(this)
    private lateinit var timelineLayoutManager: TimelineLayoutManager
    private lateinit var timelineItemDecoration: TimelineItemDecoration
    private val timelineSnapHelper = TimelineSnapHelper()

    /* Simple view */
    private val simplePrecedingSection =
        LoadTriggerAdapter(R.string.load_preceding) { viewModel.postScrollBack() }
    private val simpleSucceedingSection =
        LoadTriggerAdapter(R.string.load_succeeding) { viewModel.postScrollForward() }
    private val simpleSection = SimpleTripAdapter(this)
    private val simpleAdapter = ConcatAdapter(
        simplePrecedingSection,
        simpleSection,
        simpleSucceedingSection
    )

    private val tripsAdapter = ConcatAdapter(loadStateSection)

    private lateinit var simpleLayoutManager: LinearLayoutManager
    private lateinit var simpleItemDecoration: RecyclerView.ItemDecoration

    private val timelineObserver = Observer<TripSearchResult?> { response ->
        submitTimeline((response as? ServiceResult.Success)?.result)
    }
    private val simpleObserver = Observer<TripSearchResult?> { response ->
        submitSimple((response as? ServiceResult.Success)?.result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        simpleLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        simpleItemDecoration =
            VerticalGridSpacingItemDecoration(requireContext(), missingEdge = true)
        timelineLayoutManager = TimelineLayoutManager(requireContext()) { timelineAdapter.specs!! }
        timelineItemDecoration = TimelineItemDecoration(requireContext())
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneTripsResultsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (enterTransition is SceneFragmentEnterUpTransition) {
            postponeEnterTransition()
        }
        viewBinding.recyclerView.setHasFixedSize(true)
        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        viewBinding.recyclerView.adapter = tripsAdapter
        timelineSnapHelper.attachToRecyclerView(nestedScrollingChild)
        viewBinding.toolbar.setOnMenuItemClickListener(this)

        viewModel.timeTick.observe(viewLifecycleOwner) {
            simpleSection.onTimeTick()
            timelineAdapter.onTimeTick()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadStateSection.loadState = LoadStateIndicator.State.Loading(
                    captionRes = R.string.state_searching_trips
                )
            }
        }
        viewModel.isLoadingMore.observe(viewLifecycleOwner) {
            if (it) {
                viewBinding.appBarLayout.isLifted = true
                viewBinding.loadingMore.show()
            } else {
                viewBinding.loadingMore.hide()
                if (viewBinding.recyclerView.computeVerticalScrollOffset() == 0) {
                    view.postDelayed({
                        viewBinding.appBarLayout.isLifted = false
                    }, 400)
                }
            }
        }
        viewModel.destination.observe(viewLifecycleOwner) {
            viewBinding.toolbar.title = if (it?.name != null) {
                getString(R.string.header_to, it.name)
            } else {
                getString(R.string.header_to_my_location)
            }
        }
        viewModel.trips.observe(viewLifecycleOwner) { response ->
            viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
            when (response) {
                is ServiceResult.Success -> {
                    loadStateSection.loadState = LoadStateIndicator.State.Hidden
                }
                is ServiceResult.NoResult -> {
                    loadStateSection.loadState = LoadStateIndicator.State.Error(
                        R.drawable.ic_state_no_result_40dp, R.string.state_no_trips
                    )
                }
                is ServiceResult.Failure -> {
                    loadStateSection.loadState = LoadStateIndicator.State.Error(
                        R.drawable.ic_state_error_40dp, response.error.messageRes
                    )
                }
                null -> when (viewModel.resultsViewMode.value) {
                    TripsViewModel.ResultsViewMode.TRIPS_TIMELINE -> submitTimeline(null)
                    TripsViewModel.ResultsViewMode.TRIPS -> submitSimple(null)
                    else -> {}
                }
            }

        }
        viewModel.resultsViewMode.observe(viewLifecycleOwner) { viewMode ->
            viewBinding.appBarLayout.isLiftOnScroll =
                viewMode == TripsViewModel.ResultsViewMode.TRIPS
            viewBinding.recyclerView.isNestedScrollingEnabled =
                viewMode == TripsViewModel.ResultsViewMode.TRIPS
            if (viewMode == TripsViewModel.ResultsViewMode.TRIPS_TIMELINE) {
                viewBinding.appBarLayout.isLifted = true
            }

            val newMenuIconRes: Int
            val newItemDecoration: RecyclerView.ItemDecoration
            val newLayoutManager: RecyclerView.LayoutManager
            val newAdapter: RecyclerView.Adapter<*>
            when (viewMode!!) {
                TripsViewModel.ResultsViewMode.TRIPS_TIMELINE -> {
                    newMenuIconRes = R.drawable.ic_view_rows_tall_24dp
                    newItemDecoration = timelineItemDecoration
                    newLayoutManager = timelineLayoutManager
                    newAdapter = timelineAdapter

                    submitSimple(null)
                    viewModel.trips.removeObserver(simpleObserver)
                    viewModel.trips.observe(viewLifecycleOwner, timelineObserver)
                }
                TripsViewModel.ResultsViewMode.TRIPS -> {
                    newMenuIconRes = R.drawable.ic_view_columns_24dp
                    newItemDecoration = simpleItemDecoration
                    newLayoutManager = simpleLayoutManager
                    newAdapter = simpleAdapter

                    submitTimeline(null)
                    viewModel.trips.removeObserver(timelineObserver)
                    viewModel.trips.observe(viewLifecycleOwner, simpleObserver)
                }
            }
            viewBinding.toolbar.menu.findItem(R.id.action_change_view)
                .setIcon(newMenuIconRes)

            if (viewBinding.recyclerView.layoutManager !== newLayoutManager) {
                viewBinding.recyclerView.layoutManager = newLayoutManager
            }

            while (tripsAdapter.adapters.size > 1) {
                tripsAdapter.removeAdapter(tripsAdapter.adapters.last())
            }
            tripsAdapter.addAdapter(newAdapter)

            while (viewBinding.recyclerView.itemDecorationCount > 0) {
                viewBinding.recyclerView.removeItemDecorationAt(0)
            }
            viewBinding.recyclerView.addItemDecoration(newItemDecoration)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timelineSnapHelper.attachToRecyclerView(null)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_change_view -> {
                viewModel.toggleResultsViewMode()
                return true
            }
            R.id.action_refresh -> return viewModel.searchTrips()
            else -> return false
        }
    }

    override fun onTripClicked(trip: Trip) {
        viewModel.selectedTrip.value = trip
        (contentLayer as? TripsContentFragment)
            ?.setScene(TripsContentFragment.Scene.TRIP_DETAIL)
    }

    private fun submitTimeline(result: TripSearchData?) {
        val earliestTrip = result?.trips
            ?.minByOrNull { it.departure.departureScheduled }
        val sortedTrips = result?.trips
            ?.sortedBy { it.departure.departureScheduled }
        timelineItemDecoration.earliestTrip = earliestTrip
        timelineAdapter.submitList(sortedTrips)
    }

    private fun submitSimple(result: TripSearchData?) {
        val scrollContext = result?.scrollContext
        simplePrecedingSection.isTriggerVisible = scrollContext?.canScrollBackward ?: false
        simpleSucceedingSection.isTriggerVisible = scrollContext?.canScrollForward ?: false
        simpleSection.submitList(result?.trips)
    }

    private fun clearTimeline() {
        timelineAdapter.submitList(null)
        timelineItemDecoration.earliestTrip = null
    }

    private fun clearSimple() {
        simplePrecedingSection.isTriggerVisible = false
        simpleSucceedingSection.isTriggerVisible = false
        simpleSection.submitList(null)
    }

}