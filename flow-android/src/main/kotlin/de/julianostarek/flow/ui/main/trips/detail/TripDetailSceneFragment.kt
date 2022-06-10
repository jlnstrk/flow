package de.julianostarek.flow.ui.main.trips.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.databinding.FragmentSceneTripsDetailBinding
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.main.trips.TripsViewModel
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator


class TripDetailSceneFragment : ContentLayerSceneFragment(),
    OnApplyWindowInsetsListener {
    private lateinit var viewBinding: FragmentSceneTripsDetailBinding
    /*override val dragHandle: View
        get() = viewBinding.dragHandle*/
    override val nestedScrollingChild: RecyclerView
        get() = viewBinding.recyclerView
    private val viewModel: TripsViewModel by activityViewModels()
    private val sectionsAdapter = TripDetailAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(context)
        itemDecoration = TripDetailItemDecoration()
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneTripsDetailBinding.inflate(inflater, container!!, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (enterTransition is SceneFragmentEnterUpTransition) {
            postponeEnterTransition()
        }
        viewBinding.recyclerView.setHasFixedSize(true)
        viewBinding.recyclerView.addItemDecoration(itemDecoration)
        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.adapter = sectionsAdapter

        view.post {
            viewModel.selectedTrip.observe(viewLifecycleOwner) {
                viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
                sectionsAdapter.updateDataSet(it)
                viewBinding.previewBar.setTrip(it)
            }
        }
    }

    /*override fun onMapViewCreated(
        mapView: BackdropMapView
    ) {
        viewModel.selectedTrip.observe(viewLifecycleOwner) { trip ->
            if (trip != null) {
                mapView.applyConnection(trip)
            }
        }
    }*/

    /*override fun onBottomSheetStateChanged(state: Int) {
        tabLayout.elevation = dip(if (state == BottomSheetBehavior.STATE_COLLAPSED) 8F else 16F)
    }*/

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        return insets
    }

    companion object {
        private const val LIST_SPAN_COUNT = 1
    }
}