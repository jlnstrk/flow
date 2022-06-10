package de.julianostarek.flow.ui.main.info.messagedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.view.ScrollingView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import de.julianostarek.flow.databinding.FragmentSceneInfoMessageDetailBinding
import de.julianostarek.flow.ui.common.adapter.ProductGroupedLineListAdapter
import de.julianostarek.flow.ui.common.backdrop.ContentLayerSceneFragment
import de.julianostarek.flow.ui.common.decor.VerticalGridSpacingItemDecoration
import de.julianostarek.flow.ui.main.info.InfoViewModel
import de.julianostarek.flow.util.graphics.dp
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import kotlin.math.roundToInt

class MessageDetailSceneFragment : ContentLayerSceneFragment() {
    private val viewModel: InfoViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentSceneInfoMessageDetailBinding
    override val nestedScrollingChild: ScrollingView get() = viewBinding.nestedScrollView

    private val affectedLinesAdapter = ProductGroupedLineListAdapter()

    override fun onCreateSceneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSceneInfoMessageDetailBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding.affectedLines.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.affectedLines.adapter = affectedLinesAdapter
        viewBinding.affectedLines.itemAnimator = FadeInUpAnimator()
        viewBinding.affectedLines.addItemDecoration(
            VerticalGridSpacingItemDecoration(
                requireContext(),
                horizontalEdge = false,
                verticalEdge = false,
                spacing = 8F.dp(this).roundToInt()
            )
        )
        viewModel.selectedMessage.observe(viewLifecycleOwner) { message ->
            viewBinding.head.text = message?.head
            viewBinding.subhead.text = message?.lead
            viewBinding.body.setText(message?.body?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }, TextView.BufferType.SPANNABLE)
            affectedLinesAdapter.setData(message?.affectedLines)
        }
    }

}