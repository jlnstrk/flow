package de.julianostarek.flow.ui.main.stops.journeydetail

import android.content.Context
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
import de.julianostarek.flow.databinding.FragmentSceneStopsJourneyDetailBinding
import de.julianostarek.flow.ui.common.adapter.LoadStateAdapter
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.view.LoadStateIndicator
import de.julianostarek.flow.ui.common.view.LoadStateIndicator.State.Error
import de.julianostarek.flow.ui.common.view.LoadStateIndicator.State.Hidden
import de.julianostarek.flow.ui.main.stops.StopsViewModel
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import de.julianostarek.flow.util.transit.defaultColor
import de.jlnstrk.transit.common.response.base.ServiceResult
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class JourneyDetailSceneFragment : ContentLayerSceneFragment(),
    Toolbar.OnMenuItemClickListener,
    JourneyDetailAdapter.Listener {
    private lateinit var viewBinding: FragmentSceneStopsJourneyDetailBinding

    //override val dragHandle: View
    //    get() = viewBinding.dragHandle
    override val nestedScrollingChild: RecyclerView
        get() = viewBinding.recyclerView
    private val viewModel: StopsViewModel by activityViewModels()

    private val indicatorAdapter = LoadStateAdapter()
    private val stopsAdapter = JourneyDetailAdapter(this)
    private val concatAdapter = ConcatAdapter(indicatorAdapter, stopsAdapter)
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var sidelineDecoration: JourneyDetailItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())

        val attrs = requireContext().obtainStyledAttributes(intArrayOf(R.attr.keyline))
        sidelineDecoration = JourneyDetailItemDecoration(
            requireContext(),
            attrs.getDimensionPixelSize(0, 0)
        )
        attrs.recycle()
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneStopsJourneyDetailBinding.inflate(inflater, container!!, false)
        return viewBinding.root
    }

    override fun applyTransitionForContext(context: Context, exit: Boolean, up: Boolean) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (enterTransition is SceneFragmentEnterUpTransition) {
            postponeEnterTransition()
        }
        viewBinding.recyclerView.setHasFixedSize(true)
        viewBinding.recyclerView.addItemDecoration(sidelineDecoration)
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.adapter = concatAdapter
        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        view.post {
            viewModel.journeyDetails.observe(viewLifecycleOwner) {
                viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
                if (it is ServiceResult.Success) {
                    indicatorAdapter.loadState = Hidden
                    stopsAdapter.submitList(it.result.journey.stops)
                    val productColor = it.result.journey.line.defaultColor(requireContext())
                    sidelineDecoration.lineColor = productColor
                    viewBinding.lineDirection.setFromLineDirection(
                        it.result.journey.line,
                        it.result.journey.directionTo
                    )
                    /*view.post {
                        startBottomSheetSettling()
                    }
                    genericViewBinding.footerText.setText(
                        it.result.stops!!.first().formatContext(
                            requireContext(),
                            it.result.line.product
                        ), TextView.BufferType.SPANNABLE
                    )*/
                } else {
                    stopsAdapter.submitList(null)
                    if (it != null) {
                        indicatorAdapter.loadState = Error(
                            captionRes = R.string.state_no_journey_detail
                        )
                    }
                }

            }
            viewModel.journeyDetailsLoading.observe(viewLifecycleOwner) { isLoading ->
                println("observer")
                if (isLoading) {
                    indicatorAdapter.loadState = LoadStateIndicator.State.Loading(
                        captionRes = R.string.state_loading_journey_detail
                    )
                }
            }
        }

    }


    /*override fun onMapViewCreated(mapView: BackdropMapView) {
        viewModel.journeyDetails.observe(viewLifecycleOwner) {
            if (it is ServiceResponse.Success) {
                mapView.applyJourney(it.result)
            }
        }
    }*/

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            else -> return false
        }
    }

    companion object {
        private const val LIST_SPAN_COUNT = 1
    }

}