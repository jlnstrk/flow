package de.julianostarek.flow.ui.main.stops

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentBackdropStopsBinding
import de.julianostarek.flow.databinding.IncludeProductsBarBinding
import de.julianostarek.flow.ui.common.backdrop.BackdropFragment
import de.julianostarek.flow.ui.common.timeselector.TimeSelectorDialog
import de.julianostarek.flow.ui.locationsearch.LocationSearchActivity
import de.julianostarek.flow.ui.transition.StopsBackdropEnterTransition
import de.julianostarek.flow.ui.transition.StopsBackdropExitTransition
import de.julianostarek.flow.util.datetime.DATE_TIME_FORMAT_UI
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.text.setTextWithPrefix
import de.julianostarek.flow.util.transit.applyTo
import de.jlnstrk.transit.common.model.Location
import kotlin.math.roundToInt
import kotlinx.datetime.LocalDateTime

class StopsBackdropFragment : BackdropFragment(),
    View.OnClickListener {
    override val maxNumSharedElements: Int = 3
    override val headerRes: Int = R.string.header_station_board
    override val menuRes: Int = R.menu.menu_backdrop_stops
    override val anchorPosition: Int = 0

    private lateinit var viewBinding: FragmentBackdropStopsBinding
    private lateinit var productsBarBinding: IncludeProductsBarBinding
    private val viewModel: StopsViewModel by activityViewModels()

    override val linearLayout: LinearLayout
        get() = viewBinding.root

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentBackdropStopsBinding.inflate(inflater, container, false)
        productsBarBinding = IncludeProductsBarBinding.bind(viewBinding.root)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.locationContainer.setOnClickListener(this)
        viewBinding.locationDismiss.setOnClickListener(this)
        viewBinding.modeContainer.setOnClickListener(this)
        viewBinding.timeContainer.setOnClickListener(this)
        viewBinding.durationIncreaseButton.setOnClickListener(this)
        viewBinding.durationDecreaseButton.setOnClickListener(this)

        viewBinding.locationText.doOnPreDraw {
            startPostponedEnterTransition()
        }

        productsBarBinding.productsBar.subscribe { selection ->
            viewModel.productFilter.value = selection
        }

        viewModel.location.observe(viewLifecycleOwner) {
            if (it != null) {
                it.applyTo(viewBinding.locationText, R.string.input_prefix_at_location)
                viewBinding.locationDismiss.visibility = View.VISIBLE
            } else {
                viewBinding.locationText.text = null
                viewBinding.locationDismiss.visibility = View.GONE
            }
            animateLayoutChange()
        }
        viewModel.showArrivals.observe(viewLifecycleOwner) {
            viewBinding.modeText.setTextWithPrefix(
                R.string.input_prefix_show, when (it) {
                    true -> R.string.input_arrivals
                    false -> R.string.input_departures
                }
            )
        }
        viewModel.time.observe(viewLifecycleOwner) {
            viewBinding.timeText.setTextWithPrefix(
                R.string.input_prefix_from_time, text = when (it) {
                    null -> getString(R.string.input_now)
                    else -> DATE_TIME_FORMAT_UI.formatDateTime(it)
                }
            )
        }

        viewModel.productFilter.isDefaultFilter.observe(viewLifecycleOwner) {
            if (findContentLayer()?.isShifted == false) {
                onContentLayerShiftChanged(false)
            }
        }
        viewModel.intervalDuration.observe(viewLifecycleOwner) {
            val minutes = it.inWholeMinutes
            val formattedDuration = when {
                minutes <= 60 -> "$it-minute Intervals"
                else -> "${minutes / 60}-hour Intervals"
            }
            viewBinding.durationText.text = formattedDuration
        }
        viewModel.profileConfig.observe(viewLifecycleOwner) { profileConfig ->
            productsBarBinding.productsBar.submitConfiguration(profileConfig.constant)
        }
        viewModel.productFilter.observe(viewLifecycleOwner) { filter ->
            productsBarBinding.productsBar.submitSelection(filter)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_STATION && resultCode == Activity.RESULT_OK) {
            val stationId = data!!.getLongExtra(
                LocationSearchActivity.INTENT_EXTRA_LOCATION_ID,
                LocationSearchActivity.INTENT_EXTRA_LOCATION_ID_CURRENT_POSITION
            )
            viewModel.selectNearbyLocation(stationId)
            (findContentLayer() as? StopsContentFragment)
                ?.setScene(StopsContentFragment.Scene.STATION_BOARD)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            viewBinding.locationDismiss.id -> {
                viewModel.location.value = null
            }
            viewBinding.locationContainer.id -> {
                val intent = Intent(activity, LocationSearchActivity::class.java)
                intent.putExtra(
                    LocationSearchActivity.INTENT_EXTRA_LOCATION_TYPES,
                    viewModel.supportedLocationTypes.map(Location.Type::name)
                        .toTypedArray()
                )
                startActivityForResult(intent, REQUEST_CODE_STATION)
            }
            viewBinding.modeContainer.id -> {
                viewModel.showArrivals.value =
                    !viewModel.showArrivals.value!!
            }
            viewBinding.timeContainer.id -> {
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
            viewBinding.durationDecreaseButton.id -> {
                viewModel.shiftIntervalDuration(true)
            }
            viewBinding.durationIncreaseButton.id -> {
                viewModel.shiftIntervalDuration(false)
            }
        }
    }

    override fun applyEnterTransition(context: Context, direction: Int) {
        this.enterTransition = StopsBackdropEnterTransition(context, direction)
    }

    override fun applyExitTransition(context: Context) {
        this.exitTransition = StopsBackdropExitTransition(context)
    }

    override fun onCollectSharedElements(): List<Pair<View, String>> {
        val fieldOne = resources.getString(R.string.tn_backdrop_field_1)
        val fieldTwo = resources.getString(R.string.tn_backdrop_field_2)
        val fieldThree = resources.getString(R.string.tn_backdrop_field_3)
        return listOf(
            viewBinding.locationContainer to fieldOne,
            viewBinding.modeContainer to fieldTwo,
            viewBinding.timeContainer to fieldThree
        )
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_tune -> {
                val contentLayer = findContentLayer()
                contentLayer?.setShifted(!contentLayer.isShifted)
                return true
            }
            R.id.action_refresh -> {
                viewModel.refreshStationBoard()
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
                R.drawable.ic_filter_list_24dp
            } else R.drawable.ic_state_modified_24dp
        }
        menuItem?.setIcon(iconRes)
    }

    override fun getConcealedBackdropHeight(): Int {
        return viewBinding.timeContainer.bottom + 16F.dp(this)
            .roundToInt() // dip(48 + 8 + 48 + 8 + 48 + 16)
    }

    companion object {
        private const val REQUEST_CODE_STATION = 1
    }

}