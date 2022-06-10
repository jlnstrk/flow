package de.julianostarek.flow.ui.main.info.linegroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.FadingDividerItemDecoration
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentSceneInfoLinesBinding
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.main.info.InfoContentFragment
import de.julianostarek.flow.ui.main.info.InfoViewModel
import de.julianostarek.flow.ui.transition.scene.SceneFragmentEnterUpTransition
import de.julianostarek.flow.util.graphics.dp
import de.jlnstrk.transit.common.model.Line
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class LinesSceneFragment : ContentLayerSceneFragment(), LineGroupsAdapter.Listener, Toolbar.OnMenuItemClickListener {
    private val viewModel: InfoViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentSceneInfoLinesBinding
    override val nestedScrollingChild: RecyclerView get() = viewBinding.recyclerView

    private val adapter = LineGroupsAdapter(this)
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var itemDecoration: RecyclerView.ItemDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())
        itemDecoration = FadingDividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        ).apply {
            dividerInsetStart = 116F.dp(this@LinesSceneFragment).toInt()
            dividerInsetEnd = 24F.dp(this@LinesSceneFragment).toInt()
        }
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneInfoLinesBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (enterTransition is SceneFragmentEnterUpTransition) {
            postponeEnterTransition()
        }
        viewBinding.toolbar.setOnMenuItemClickListener(this)
        viewBinding.recyclerView.doOnPreDraw { startPostponedEnterTransition() }
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.adapter = adapter
        viewBinding.recyclerView.addItemDecoration(itemDecoration)
        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        viewModel.groupedMessages.observe(viewLifecycleOwner) { groupedMessages ->
            adapter.submitList(groupedMessages.toList())
        }
    }

    override fun onLineClicked(line: Line) {
        viewModel.selectedLine.value = line
        (contentLayer as InfoContentFragment).setScene(InfoContentFragment.Scene.MESSAGES)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter -> {
                contentLayer?.setShifted(contentLayer?.isShifted == false)
                return false
            }
            else -> return false
        }
    }

    override fun onContentLayerShiftChanged(isShifted: Boolean) {
        val menuItem = viewBinding.toolbar.menu.findItem(R.id.action_filter)
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

}