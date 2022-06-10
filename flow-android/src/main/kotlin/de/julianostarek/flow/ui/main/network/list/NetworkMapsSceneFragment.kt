package de.julianostarek.flow.ui.main.network.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.FadingDividerItemDecoration
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentSceneNetworkMapsBinding
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.main.network.NetworkViewModel
import de.jlnstrk.transit.common.response.base.ServiceResult
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class NetworkMapsSceneFragment : ContentLayerSceneFragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var viewBinding: FragmentSceneNetworkMapsBinding
    private val viewModel: NetworkViewModel by activityViewModels()
    override val nestedScrollingChild: RecyclerView get() = viewBinding.recyclerView

    private val mapsSection = NetworkMapsAdapter()

    private lateinit var mapsItemDecoration: RecyclerView.ItemDecoration
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())

        mapsItemDecoration = FadingDividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        ).apply {
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.keyline_fg_plus112dp)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.keyline_fg)
        }
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneNetworkMapsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.toolbar.setOnMenuItemClickListener(this)
        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.adapter = mapsSection
        viewBinding.recyclerView.addItemDecoration(mapsItemDecoration)
        viewModel.networkMaps.observe(viewLifecycleOwner) { response ->
            mapsSection.submitList((response as? ServiceResult.Success?)?.result?.maps)
        }
    }

    override fun onContentLayerShiftChanged(isShifted: Boolean) {
        val menuItem = viewBinding.toolbar.menu.findItem(R.id.action_filter)
        val iconRes: Int
        if (isShifted) {
            iconRes = R.drawable.ic_expand_less_24dp
        } else {
            iconRes = if (viewModel.productFilter.isDefaultFilter.value == true) {
                R.drawable.ic_filter_list_24dp
            } else R.drawable.ic_state_modified_24dp
        }
        menuItem.setIcon(iconRes)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter -> {
                contentLayer?.setShifted(contentLayer?.isShifted == false)
                return false
            }
            R.id.action_refresh -> {
                // (viewModel.profile as MutableLiveData).setAgain()
                return true
            }
            else -> return false
        }

    }

}