package de.julianostarek.flow.ui.main.trips.recents

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
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentSceneTripsRecentsBinding
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.ui.common.adapter.LoadStateAdapter
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.decor.VerticalGridSpacingItemDecoration
import de.julianostarek.flow.ui.common.view.LoadStateIndicator
import de.julianostarek.flow.ui.main.trips.TripsViewModel
import de.julianostarek.flow.ui.main.trips.TripsContentFragment
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator


class RecentRoutesSceneFragment : ContentLayerSceneFragment(),
    Toolbar.OnMenuItemClickListener,
    RecentRoutesAdapter.Listener {
    private val viewModel: TripsViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentSceneTripsRecentsBinding
    override val nestedScrollingChild: RecyclerView get() = viewBinding.recyclerView

    private val loadStateSection = LoadStateAdapter()
    private val routesSection = RecentRoutesAdapter(this)
    private val concatAdapter = ConcatAdapter(loadStateSection, routesSection)
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        itemDecoration = VerticalGridSpacingItemDecoration(
            requireContext(), missingEdge = false
        )
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneTripsRecentsBinding.inflate(inflater, container, false)
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
        viewBinding.recyclerView.adapter = concatAdapter
        viewBinding.toolbar.setOnMenuItemClickListener(this)

        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
            routesSection.submitList(routes)
            if (routes.isEmpty()) {
                loadStateSection.loadState = LoadStateIndicator.State.Error(
                    iconRes = R.drawable.ic_state_no_result_40dp,
                    captionRes = R.string.state_no_routes
                )
            } else {
                loadStateSection.loadState = LoadStateIndicator.State.Hidden
            }
        }
        viewModel.productFilter.isDefaultFilter.observe(viewLifecycleOwner) {
            if (contentLayer?.isShifted == false) {
                onContentLayerShiftChanged(false)
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_tune -> {
                contentLayer?.setShifted(contentLayer?.isShifted == false)
                return false
            }
            R.id.action_submit -> {
                if (viewModel.searchTrips()) {
                    (contentLayer as? TripsContentFragment)
                        ?.setScene(TripsContentFragment.Scene.TRIP_RESULTS)
                }
                return true
            }
            else -> return false
        }
    }

    override fun onContentLayerShiftChanged(isShifted: Boolean) {
        val menuItem = viewBinding.toolbar.menu?.findItem(R.id.action_tune)
        val iconRes: Int
        if (isShifted) {
            iconRes = R.drawable.ic_expand_less_24dp
        } else {
            iconRes = if (viewModel.productFilter.isDefaultFilter.value == true) {
                R.drawable.ic_tune_24dp
            } else R.drawable.ic_state_modified_24dp
        }
        menuItem?.setIcon(iconRes)
    }


    override fun onRouteClicked(route: RouteEntity) {
        viewModel.setRoute(route)
    }

    override fun onRouteFavoriteClicked(route: RouteEntity) {
        viewModel.toggleRouteFavorite(route)
    }

}