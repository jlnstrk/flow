package de.julianostarek.flow.ui.main.trips

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentBackdropTripsBinding
import de.julianostarek.flow.databinding.IncludeProductsBarBinding
import de.julianostarek.flow.ui.common.backdrop.BackdropFragment
import de.julianostarek.flow.ui.common.timeselector.TimeSelectorDialog
import de.julianostarek.flow.ui.common.ExplicitItemAnimator
import de.julianostarek.flow.ui.common.decor.VerticalGridSpacingItemDecoration
import de.julianostarek.flow.ui.locationsearch.LocationSearchActivity
import de.julianostarek.flow.ui.transition.TripsBackdropEnterTransition
import de.julianostarek.flow.ui.transition.TripsBackdropExitTransition
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt
import de.jlnstrk.transit.common.model.Via
import kotlinx.datetime.LocalDateTime

class TripsBackdropFragment : BackdropFragment(),
    View.OnClickListener,
    TripsBackdropAdapter.Callback {
    override val maxNumSharedElements: Int = 6
    override val headerRes: Int = R.string.header_connections
    override val menuRes: Int = R.menu.menu_backdrop_trip
    override val anchorPosition: Int = 1

    private val touchHelperCallback: ItemTouchHelper.Callback = TouchHelperCallback()
    private val adapter: TripsBackdropAdapter = TripsBackdropAdapter()
        .withCallback(this)
    private val touchHelper: ItemTouchHelper = ItemTouchHelper(touchHelperCallback)
    private val itemAnimator: ExplicitItemAnimator = ExplicitItemAnimator()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    private lateinit var viewBinding: FragmentBackdropTripsBinding
    private lateinit var productsBarBinding: IncludeProductsBarBinding
    private val viewModel: TripsViewModel by activityViewModels()

    override val linearLayout: LinearLayout get() = viewBinding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(activity)
        itemDecoration = VerticalGridSpacingItemDecoration(
            requireContext(),
            horizontalEdge = false, verticalEdge = false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentBackdropTripsBinding.inflate(inflater, container, false)
        productsBarBinding = IncludeProductsBarBinding.bind(viewBinding.root)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.addViaButton.setOnClickListener(this)
        viewBinding.swapButton.setOnClickListener(this)

        viewBinding.recyclerView.itemAnimator = itemAnimator
        viewBinding.recyclerView.addItemDecoration(itemDecoration)
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.adapter = adapter
        touchHelper.attachToRecyclerView(viewBinding.recyclerView)

        productsBarBinding.productsBar.subscribe {
            viewModel.productFilter.value = it
        }

        (viewBinding.recyclerView).doOnPreDraw {
            startPostponedEnterTransition()
        }

        viewModel.origin.observe(viewLifecycleOwner) {
            adapter.updateOrigin(it)
            animateLayoutChange()
        }
        viewModel.via.observe(viewLifecycleOwner) {
            adapter.updateVias(it)
            animateLayoutChange()
        }
        viewModel.destination.observe(viewLifecycleOwner) {
            adapter.updateDestination(it)
            animateLayoutChange()
        }
        viewModel.time.observe(viewLifecycleOwner) {
            if (viewBinding.recyclerView.childCount > 0) {
                val changeBounds = ChangeBounds()
                TransitionManager.beginDelayedTransition(
                    view as ViewGroup,
                    changeBounds
                )
            }
            adapter.updateTime(it)
        }
        viewModel.isArrivalTime.observe(viewLifecycleOwner) {
            adapter.updateTimeMode(it)
        }

        viewModel.productFilter.isDefaultFilter.observe(viewLifecycleOwner) {
            if (findContentLayer()?.isShifted == false) {
                onContentLayerShiftChanged(false)
            }
        }
        viewModel.profileConfig.observe(viewLifecycleOwner) { profileConfig ->
            productsBarBinding.productsBar.submitConfiguration(profileConfig.constant)
        }
        viewModel.productFilter.observe(viewLifecycleOwner) {
            productsBarBinding.productsBar.submitSelection(it)
        }
    }

    override fun getTransitionForLayoutChange(): Transition {
        val transition = ChangeBounds()
        if (itemAnimator.enableAnimations) {
            transition.excludeChildren(viewBinding.recyclerView, true)
        }
        return transition
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ORIGIN -> if (resultCode == Activity.RESULT_OK) {
                val locationId = data!!.getLongExtra(
                    LocationSearchActivity.INTENT_EXTRA_LOCATION_ID,
                    LocationSearchActivity.INTENT_EXTRA_LOCATION_ID_CURRENT_POSITION
                )
                viewModel.setOrigin(locationId)
            }
            REQUEST_CODE_VIA -> if (resultCode == Activity.RESULT_OK) {
                val locationId = data!!.getLongExtra(
                    LocationSearchActivity.INTENT_EXTRA_LOCATION_ID,
                    LocationSearchActivity.INTENT_EXTRA_LOCATION_ID_CURRENT_POSITION
                )
                viewModel.addVia(locationId)
            }
            REQUEST_CODE_DESTINATION -> if (resultCode == Activity.RESULT_OK) {
                val locationId = data!!.getLongExtra(
                    LocationSearchActivity.INTENT_EXTRA_LOCATION_ID,
                    LocationSearchActivity.INTENT_EXTRA_LOCATION_ID_CURRENT_POSITION
                )
                viewModel.setDestination(locationId)
            }
        }
    }

    override fun onOriginSelected() {
        val allowedTypes = viewModel.allowedOriginTypes
        val intent = LocationSearchActivity.makeIntent(requireContext(), allowedTypes)
        startActivityForResult(intent, REQUEST_CODE_ORIGIN)
    }

    override fun onDestinationSelected() {
        val allowedTypes = viewModel.allowedDestinationTypes
        val intent = LocationSearchActivity.makeIntent(requireContext(), allowedTypes)
        startActivityForResult(intent, REQUEST_CODE_DESTINATION)
    }

    override fun onWaitTimeSelected(via: Via, index: Int) {
        viewModel.toggleViaWaitTime(index)
    }

    override fun onTimeSelected() {
        TimeSelectorDialog()
            .withCallback(object : TimeSelectorDialog.Callback {
                override fun onNowSelected() {
                    viewModel.time.value = null
                }

                override fun onTimeSelected(time: LocalDateTime) {
                    viewModel.time.value = time
                }
            })
            .preselection(viewModel.time.value)
            .show(childFragmentManager, null)
    }

    override fun onTimeModeSelected() {
        val liveData = viewModel.isArrivalTime
        liveData.value = !liveData.value!!
    }

    override fun onClick(view: View) {
        when (view.id) {
            viewBinding.addViaButton.id -> {
                val currentCount = viewModel.via.value?.size ?: 0
                if (currentCount < viewModel.allowedViaCount) {
                    val allowedTypes = viewModel.allowedViaTypes
                    val intent = LocationSearchActivity.makeIntent(requireContext(), allowedTypes)
                    startActivityForResult(intent, REQUEST_CODE_VIA)
                }
            }
            viewBinding.swapButton.id -> {
                viewModel.reverseRoute()
            }
        }
    }

    override fun applyEnterTransition(context: Context, direction: Int) {
        this.enterTransition = TripsBackdropEnterTransition(context, direction)
    }

    override fun applyExitTransition(context: Context) {
        this.exitTransition = TripsBackdropExitTransition(context)
    }

    override fun onCollectSharedElements(): List<Pair<View, String>> {
        return viewBinding.recyclerView.children
            .mapIndexed { index, view ->
                Pair(
                    view,
                    getString(R.string.tn_backdrop_field_arg, index + 1)
                )
            }
            .toList()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_tune -> {
                val contentLayer = findContentLayer()
                contentLayer?.setShifted(!contentLayer.isShifted)
                return true
            }
            R.id.action_refresh -> {
                val setShifted = !viewModel.searchTrips()
                view?.post {
                    findContentLayer()?.setShifted(setShifted)
                }
                return true
            }
            else -> return false
        }
    }

    override fun onContentLayerShiftChanged(isShifted: Boolean) {
        val menuItem = mainFragment?.contextualToolbar?.menu?.findItem(R.id.action_tune)
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

    override fun getConcealedBackdropHeight(): Int {
        return viewBinding.recyclerView.bottom + 8F.dp(this).roundToInt()
    }

    private inner class TouchHelperCallback : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val itemCount = recyclerView.adapter!!.itemCount
            return when (viewHolder.adapterPosition) {
                0,
                itemCount - 2,
                itemCount - 1 -> 0
                else -> ItemTouchHelper.Callback.makeMovementFlags(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.END
                )
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            viewModel.removeVia(viewHolder.adapterPosition - 1)
        }

        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val itemCount = recyclerView.adapter!!.itemCount
            return when (target.adapterPosition) {
                0,
                itemCount - 2,
                itemCount - 1 -> false
                else -> {
                    viewModel.swapVia(
                        viewHolder.adapterPosition - 1,
                        target.adapterPosition - 1
                    )
                    true
                }
            }
        }

        override fun isLongPressDragEnabled(): Boolean = true

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_DRAG -> {
                    viewHolder?.itemView?.isActivated = true
                    itemAnimator.enableAnimations = true
                }
            }
        }

        override fun clearView(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) {
            super.clearView(recyclerView, viewHolder)
            itemAnimator.enableAnimations = false
            if (viewHolder.itemView.isActivated) {
                viewHolder.itemView.isActivated = false
            }
        }

    }

    companion object {
        private const val REQUEST_CODE_ORIGIN = 8
        private const val REQUEST_CODE_DESTINATION = 9
        private const val REQUEST_CODE_VIA = 10
    }

}