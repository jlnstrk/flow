package de.julianostarek.flow.ui.main.info.linemessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentSceneInfoMessagesBinding
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.decor.VerticalGridSpacingItemDecoration
import de.julianostarek.flow.ui.main.info.InfoContentFragment
import de.julianostarek.flow.ui.main.info.InfoViewModel
import de.jlnstrk.transit.common.model.Message
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class MessagesSceneFragment : ContentLayerSceneFragment(),
    Toolbar.OnMenuItemClickListener,
    LineMessagesAdapter.Listener {
    private lateinit var viewBinding: FragmentSceneInfoMessagesBinding
    private val viewModel: InfoViewModel by activityViewModels()
    override val nestedScrollingChild: RecyclerView get() = viewBinding.recyclerView

    private val messagesSection = LineMessagesAdapter(this)

    private lateinit var messagesItemDecoration: RecyclerView.ItemDecoration
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutManager = LinearLayoutManager(requireContext())

        messagesItemDecoration = VerticalGridSpacingItemDecoration(
            requireContext(),
            missingEdge = false
        )
    }

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneInfoMessagesBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.toolbar.setOnMenuItemClickListener(this)
        viewBinding.recyclerView.itemAnimator = FadeInUpAnimator()
        viewBinding.recyclerView.layoutManager = layoutManager
        viewBinding.recyclerView.adapter = messagesSection
        viewBinding.recyclerView.addItemDecoration(messagesItemDecoration)
        viewModel.selectedLine.observe(viewLifecycleOwner) { line ->
            viewBinding.toolbar.title = if (line != null) {
                getString(R.string.header_messages_affecting, line.label)
            } else null
        }
        viewModel.selectedLineMessages.observe(viewLifecycleOwner, messagesSection::submitList)
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

    override fun onMessageClicked(message: Message) {
        viewModel.selectedMessage.value = message
        (contentLayer as InfoContentFragment).setScene(InfoContentFragment.Scene.MESSAGE_DETAIL)
    }

}