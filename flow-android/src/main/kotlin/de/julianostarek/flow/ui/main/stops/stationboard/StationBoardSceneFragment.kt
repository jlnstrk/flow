package de.julianostarek.flow.ui.main.stops.stationboard

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
import androidx.transition.TransitionManager
import com.google.android.material.divider.FadingDividerItemDecoration
import com.google.android.material.tabs.TabLayout
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentSceneStopsStationBoardBinding
import de.julianostarek.flow.profile.FlowProduct
import de.julianostarek.flow.ui.common.adapter.LoadStateAdapter
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.view.LoadStateIndicator
import de.julianostarek.flow.ui.main.stops.StopsContentFragment
import de.julianostarek.flow.ui.main.stops.StopsViewModel
import de.julianostarek.flow.ui.main.stops.stationboard.chronologic.ChronologicJourneysAdapter
import de.julianostarek.flow.ui.main.stops.stationboard.merged.MergedJourneysAdapter
import de.julianostarek.flow.ui.main.stops.stationboard.mergedgrouped.GroupedJourneysAdapter
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import de.julianostarek.flow.util.animateRealtimeSignal
import de.julianostarek.flow.util.transit.iconRes
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.response.base.ServiceResult
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class StationBoardSceneFragment : ContentLayerSceneFragment(),
    ChronologicJourneysAdapter.Listener,
    MergedJourneysAdapter.Listener,
    GroupedJourneysAdapter.Listener,
    Toolbar.OnMenuItemClickListener,
    TabLayout.OnTabSelectedListener {
    private val viewModel: StopsViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentSceneStopsStationBoardBinding
    override val nestedScrollingChild: RecyclerView
        get() = viewBinding.recyclerView

    private lateinit var layoutManager: LinearLayoutManager

    private val loadStateSection = LoadStateAdapter()
    private val chronologicSection = ChronologicJourneysAdapter(this)
    private val mergedSection = MergedJourneysAdapter(this)
    private val groupedSection = GroupedJourneysAdapter(this)
    private val stationBoardAdapter = ConcatAdapter(loadStateSection)

    private val visibleSection: RecyclerView.Adapter<*>?
        get() = stationBoardAdapter.adapters.getOrNull(1)

    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())
        val attrs = requireContext().theme.obtainStyledAttributes(intArrayOf(R.attr.keyline))
        itemDecoration = FadingDividerItemDecoration(
            requireContext(),
            dividerInsetStart = attrs.getDimensionPixelSize(0, 0),
            dividerInsetEnd = attrs.getDimensionPixelSize(0, 0)
        )
        attrs.recycle()
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneStopsStationBoardBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    private fun submitResults(results: List<Journey>?) {
        chronologicSection.submitList(results)
        mergedSection.submitJourneys(results)
        groupedSection.submitList(results)
        viewBinding.tabLayout.removeAllTabs()
        results?.groupBy { it.line.product }
            ?.keys?.forEach { productClass ->
                viewBinding.tabLayout.addTab(
                    viewBinding.tabLayout.newTab()
                        .setIcon(productClass.iconRes(requireContext()))
                        .setText(
                            when {
                                productClass is FlowProduct
                                        && productClass.label != null -> productClass.label
                                else -> "?"
                            }
                        )
                        .setTag(productClass)
                )
            }
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
        viewBinding.recyclerView.adapter = stationBoardAdapter
        viewBinding.toolbar.setOnMenuItemClickListener(this)
        viewBinding.tabLayout.addOnTabSelectedListener(this)

        viewModel.timeTick.observe(viewLifecycleOwner) {
            chronologicSection.onTimeTick()
            mergedSection.onTimeTick()
            groupedSection.onTimeTick()
        }

        viewModel.stationBoard.observe(viewLifecycleOwner) { response ->
            submitResults((response as? ServiceResult.Success)?.result?.journeys)
            when (response) {
                is ServiceResult.Success -> {
                    val type =
                        getString(if (response.result.isArrivalBoard) R.string.input_arrivals else R.string.input_departures)
                    viewBinding.toolbar.title = "$type • ${viewModel.location.value?.name}"
                    loadStateSection.loadState = LoadStateIndicator.State.Hidden
                }
                is ServiceResult.Failure,
                is ServiceResult.NoResult -> {
                    loadStateSection.loadState = LoadStateIndicator.State.Error(
                        R.drawable.ic_state_error_40dp, when (response) {
                            is ServiceResult.NoResult -> R.string.state_no_station_board
                            is ServiceResult.Failure -> R.string.error_unknown
                            else -> throw IllegalStateException()
                        }
                    )
                }
                else -> {}
            }
            viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
        }
        viewModel.stationBoardViewMode.observe(viewLifecycleOwner) { viewMode ->
            val newAdapter = when (viewMode) {
                StopsViewModel.StationBoardViewMode.CHRONOLOGIC -> chronologicSection
                StopsViewModel.StationBoardViewMode.MERGED -> mergedSection
                StopsViewModel.StationBoardViewMode.GROUPED -> groupedSection
            }

            val oldAdapter = visibleSection
            if (oldAdapter != null && oldAdapter !== newAdapter) {
                TransitionManager.beginDelayedTransition(viewBinding.tabLayout)
                if (newAdapter === groupedSection && oldAdapter !== groupedSection) {
                    viewBinding.tabLayout.visibility = View.VISIBLE
                } else if (newAdapter !== groupedSection && oldAdapter === groupedSection) {
                    viewBinding.tabLayout.visibility = View.GONE
                }
                stationBoardAdapter.removeAdapter(oldAdapter)
            }
            stationBoardAdapter.addAdapter(newAdapter)
        }
        viewModel.stationBoardLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadStateSection.loadState = LoadStateIndicator.State.Loading(
                    captionRes = R.string.state_loading_station_board
                )
            }
        }
        viewModel.stationBoardRefreshing.observe(viewLifecycleOwner) {
            val item = viewBinding.toolbar.menu.findItem(R.id.action_realtime)
            item.animateRealtimeSignal(requireContext())
        }
    }

    override fun onJourneyClicked(journey: Journey) {
        (contentLayer as? StopsContentFragment)?.setScene(StopsContentFragment.Scene.JOURNEY_DETAILS)
        viewModel.loadJourneyDetails(journey)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_change_view -> {
                viewModel.toggleStationBoardViewMode()
                return true
            }
            else -> return false
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (visibleSection === groupedSection) {
            groupedSection.showProduct(tab?.tag as ProductClass)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // do nothing
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        // do nothing
    }

    companion object {
        private const val LIST_SPAN_COUNT = 1
    }

}