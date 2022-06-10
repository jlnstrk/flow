package de.julianostarek.flow.ui.locationsearch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.ViewOutlineProvider
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.tabs.TabLayout
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ActivityLocationSearchBinding
import de.julianostarek.flow.ui.common.activity.SequenceActivity
import de.julianostarek.flow.ui.common.adapter.HeaderAdapter
import de.julianostarek.flow.ui.common.adapter.LoadStateAdapter
import de.julianostarek.flow.ui.common.view.LoadStateIndicator
import de.julianostarek.flow.ui.main.stops.nearby.NearbyLocationsAdapter
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.viewmodel.LocationSourceViewModel
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.LocationSearchResult
import de.jlnstrk.transit.common.service.NearbyLocationsResult
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class LocationSearchActivity : SequenceActivity(),
    TextWatcher,
    LocationSearchAdapter.Listener,
    OfflineLocationsAdapter.Listener,
    NearbyLocationsAdapter.Listener,
    Toolbar.OnMenuItemClickListener,
    TabLayout.OnTabSelectedListener {
    private lateinit var viewBinding: ActivityLocationSearchBinding

    private lateinit var layoutManager: LinearLayoutManager

    private val concatAdapter = ConcatAdapter()

    private val resultsHeaderAdapter = HeaderAdapter(R.string.header_results)
    private val resultsLoadStateAdapter = LoadStateAdapter()
    private val resultsAdapter = LocationSearchAdapter(this)

    private val nearbyHeaderAdapter = HeaderAdapter(R.string.header_nearby)
    private val nearbyLoadStateAdapter = LoadStateAdapter()
    private val nearbyAdapter = NearbyLocationsAdapter(this)

    private val offlineHeader = HeaderAdapter(R.string.header_recently_used)
    private val offlineLoadStateAdapter = LoadStateAdapter()
    private val offlineAdapter = OfflineLocationsAdapter(this)

    private val locationSource: LocationSourceViewModel by viewModels()
    private val viewModel: LocationSearchViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val queryCallbacks = Runnable {
        viewModel.postQuery(viewBinding.editText.text.toString())
    }

    private val nearbyObserver = Observer<NearbyLocationsResult> { _ ->
        fun update() {
            val nearbyLocations =
                (viewModel.nearbyResults.value as? ServiceResult.Success)?.result?.locations?.take(
                    3
                )
            nearbyHeaderAdapter.isVisible = !nearbyLocations.isNullOrEmpty()
            nearbyAdapter.submitList(nearbyLocations)
            nearbyLoadStateAdapter.loadState = LoadStateIndicator.State.Hidden
        }

        viewBinding.recyclerView.itemAnimator!!.isRunning(::update)
    }
    private val offlineObserver = Observer<PagingData<Location>> { _ ->
        fun update() {
            offlineAdapter.submitData(lifecycle, viewModel.offlineResults.value!!)
        }

        viewBinding.recyclerView.itemAnimator!!.isRunning(::update)
    }
    private val resultsObserver = Observer<LocationSearchResult> { response ->
        println("results: ${response is ServiceResult.Success}")
        val list = (response as? ServiceResult.Success)?.result?.locations
        if (response is ServiceResult.Failure) {
            resultsLoadStateAdapter.loadState = LoadStateIndicator.State.Error(
                R.drawable.ic_state_no_result_40dp,
                R.string.state_no_device_location
            )
        } else {
            resultsLoadStateAdapter.loadState = LoadStateIndicator.State.Hidden
        }
        resultsAdapter.submitList(list)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.ThemeOverlay_Sequence_BackdropActivity)
        viewBinding = ActivityLocationSearchBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val shape =
            MaterialShapeDrawable.createWithElevationOverlay(viewBinding.appBarLayout.context)
        shape.shapeAppearanceModel = ShapeAppearanceModel.builder(
            viewBinding.appBarLayout.context,
            R.style.ShapeAppearance_Sequence_LargeComponent,
            0
        )
            .setAllCorners(CornerFamily.ROUNDED, 4F.dp(this))
            .build()
        viewBinding.appBarLayout.background = shape
        viewBinding.appBarLayout.outlineProvider = ViewOutlineProvider.BACKGROUND
        viewBinding.appBarLayout.clipToOutline = true
        viewBinding.toolbar.inflateMenu(R.menu.menu_location_search)
        viewBinding.toolbar.setOnMenuItemClickListener(this)
        viewBinding.tabLayout.addOnTabSelectedListener(this)
        viewBinding.editText.addTextChangedListener(this)
        val types = intent.getStringArrayExtra(INTENT_EXTRA_LOCATION_TYPES)
            ?.map(Location.Type::valueOf)
            ?.toSet()

        if (types?.contains(Location.Type.STATION) == false) {
            disableTab(0)
        }
        if (types?.contains(Location.Type.ADDRESS) == false) {
            disableTab(1)
        }
        if (types?.contains(Location.Type.POI) == false) {
            disableTab(2)
        }
        if (types?.contains(Location.Type.POINT) == true) {
            viewBinding.toolbar.menu.findItem(R.id.action_use_my_location)
                .isVisible = true
        }

        val selectedTab = viewBinding.tabLayout.getTabAt(viewBinding.tabLayout.selectedTabPosition)
        onTabSelected(selectedTab)

        layoutManager = LinearLayoutManager(this)

        viewBinding.toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.adapter = concatAdapter
        concatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0 && positionStart == layoutManager.findFirstCompletelyVisibleItemPosition()) {
                    layoutManager.scrollToPosition(0);
                }
            }
        })

        nearbyLoadStateAdapter.mode = LoadStateAdapter.Mode.NESTED
        resultsLoadStateAdapter.mode = LoadStateAdapter.Mode.FULL_PAGE

        setShowResults(false)
        viewModel.locationPersisted.observe(this) {
            if (it != null) {
                val resultIntent = Intent()
                    .putExtra(INTENT_EXTRA_LOCATION_ID, it)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        viewModel.isLoadingResults.observe(this) { isLoading ->
            println("loading results: $isLoading")
            if (isLoading) {
                resultsLoadStateAdapter.loadState = LoadStateIndicator.State.Loading(
                    captionRes = R.string.state_searching_locations
                )
            }
        }
        viewModel.isLoadingNearby.observe(this) { isLoading ->
            println("loading nearby: $isLoading")
            if (isLoading) {
                nearbyLoadStateAdapter.loadState = LoadStateIndicator.State.Loading(
                    captionRes = R.string.state_loading_locations_nearby
                )
            }
        }

        offlineAdapter.addLoadStateListener { loadStates ->
            println("offline load state: ${loadStates.source.refresh} ${loadStates.source.append}")
            when (loadStates.source.refresh) {
                is LoadState.Loading -> {
                    offlineLoadStateAdapter.loadState = LoadStateIndicator.State.Hidden
                }
                is LoadState.Error -> {
                    offlineLoadStateAdapter.mode = LoadStateAdapter.Mode.FULL_PAGE
                    offlineLoadStateAdapter.loadState = LoadStateIndicator.State.Error(
                        captionRes = R.string.error_offline_locations
                    )
                }
                is LoadState.NotLoading -> when (val sourceAppend = loadStates.source.append) {
                    is LoadState.NotLoading -> {
                        if (sourceAppend.endOfPaginationReached && offlineAdapter.itemCount == 0) {
                            offlineLoadStateAdapter.mode = LoadStateAdapter.Mode.FULL_PAGE
                            offlineLoadStateAdapter.loadState = LoadStateIndicator.State.Error(
                                iconRes = R.drawable.ic_state_help_40dp,
                                captionRes = R.string.state_no_offline_locations
                            )
                        } else {
                            offlineLoadStateAdapter.loadState = LoadStateIndicator.State.Hidden
                        }
                    }
                    is LoadState.Loading -> {
                        offlineLoadStateAdapter.mode = LoadStateAdapter.Mode.NESTED
                        offlineLoadStateAdapter.loadState =
                            LoadStateIndicator.State.Loading(
                                captionRes = R.string.state_loading_offline_locations
                            )
                    }
                    else -> offlineLoadStateAdapter.loadState = LoadStateIndicator.State.Hidden
                }
            }
        }

        viewModel.setLocationSource(locationSource.location)
        viewModel.deviceLocation.observe(this) {
            nearbyAdapter.onReferenceLocationChanged(it)
            offlineAdapter.onReferenceLocationChanged(it)
            resultsAdapter.onReferenceLocationChanged(it)
        }
    }

    private fun disableTab(position: Int) {
        val nextTab =
            viewBinding.tabLayout.getTabAt((position + 1) % viewBinding.tabLayout.tabCount)
        viewBinding.tabLayout.selectTab(nextTab)
        viewBinding.tabLayout.getTabAt(position)?.view?.apply {
            isClickable = false
            alpha = 0.25F
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        handler.removeCallbacks(queryCallbacks)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isEmpty()) {
            if (concatAdapter.adapters.first() !== nearbyHeaderAdapter) {
                setShowResults(false)
            }
        } else if (concatAdapter.adapters.first() !== resultsHeaderAdapter) {
            setShowResults(true)
        }
        if (s.length < MIN_QUERY_LENGTH) {
            queryCallbacks.run()
        } else {
            viewModel.isLoadingResults.pushLoading()
            handler.postDelayed(
                queryCallbacks,
                if (count < before) 2 * INPUT_DELAY_MS else INPUT_DELAY_MS
            )
        }
    }

    private fun setShowResults(showResults: Boolean) {
        if (showResults) {
            concatAdapter.removeAdapter(nearbyHeaderAdapter)
            concatAdapter.removeAdapter(nearbyLoadStateAdapter)
            concatAdapter.removeAdapter(nearbyAdapter)
            concatAdapter.removeAdapter(offlineHeader)
            concatAdapter.removeAdapter(offlineAdapter)
            concatAdapter.removeAdapter(offlineLoadStateAdapter)
            concatAdapter.addAdapter(resultsHeaderAdapter)
            concatAdapter.addAdapter(resultsLoadStateAdapter)
            concatAdapter.addAdapter(resultsAdapter)

            viewModel.searchResults.observe(this, resultsObserver)
            viewModel.nearbyResults.removeObserver(nearbyObserver)
            viewModel.offlineResults.removeObserver(offlineObserver)
        } else {
            concatAdapter.removeAdapter(resultsHeaderAdapter)
            concatAdapter.removeAdapter(resultsLoadStateAdapter)
            concatAdapter.removeAdapter(resultsAdapter)
            concatAdapter.addAdapter(nearbyHeaderAdapter)
            concatAdapter.addAdapter(nearbyLoadStateAdapter)
            concatAdapter.addAdapter(nearbyAdapter)
            concatAdapter.addAdapter(offlineHeader)
            concatAdapter.addAdapter(offlineAdapter)
            concatAdapter.addAdapter(offlineLoadStateAdapter)

            viewModel.searchResults.removeObserver(resultsObserver)
            viewModel.nearbyResults.observe(this, nearbyObserver)
            viewModel.offlineResults.observe(this, offlineObserver)
        }
    }

    override fun afterTextChanged(s: Editable?) {
        // do nothing
    }

    override fun onLocationClicked(location: Location) {
        viewModel.persistLocation(location)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_use_my_location -> {
                val resultIntent = Intent()
                    .putExtra(
                        INTENT_EXTRA_LOCATION_ID,
                        INTENT_EXTRA_LOCATION_ID_CURRENT_POSITION
                    )
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
                return true
            }
            else -> return false
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        // do nothing
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        viewModel.setLocationType(
            when (tab?.position) {
                0 -> Location.Type.STATION
                1 -> Location.Type.ADDRESS
                2 -> Location.Type.POI
                else -> throw IllegalStateException()
            }
        )
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        // do nothing
    }

    companion object {
        private const val SHARED_VIEW_TYPE_HEADER = 0
        private const val SHARED_VIEW_TYPE_LOCATION = 1

        private const val MIN_QUERY_LENGTH = 3
        private const val INPUT_DELAY_MS = 500L
        const val INTENT_EXTRA_LOCATION_ID = "location_id"
        const val INTENT_EXTRA_LOCATION_TYPES = "location_types"
        const val INTENT_EXTRA_LOCATION_ID_CURRENT_POSITION = -1L

        @JvmStatic
        fun makeIntent(context: Context, allowedTypes: Set<Location.Type>): Intent {
            val intent = Intent(context, LocationSearchActivity::class.java)
            val typesArray = allowedTypes
                .map(Location.Type::name)
                .toTypedArray()
            intent.putExtra(INTENT_EXTRA_LOCATION_TYPES, typesArray)
            return intent
        }

    }

}