package de.julianostarek.flow.ui.main.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.doOnPreDraw
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.FragmentBackdropNetworkBinding
import de.julianostarek.flow.ui.common.backdrop.BackdropFragment
import de.julianostarek.flow.util.graphics.dp

class NetworkBackdropFragment : BackdropFragment() {
    override val maxNumSharedElements: Int = 1
    override val headerRes: Int = R.string.header_network_maps
    override val menuRes: Int = R.menu.menu_backdrop_network

    private lateinit var viewBinding: FragmentBackdropNetworkBinding

    override val anchorPosition: Int = 3

    override val linearLayout: LinearLayout
        get() = requireView() as LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentBackdropNetworkBinding.inflate(inflater, container!!, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return false
    }

    override fun getConcealedBackdropHeight(): Int {
        return viewBinding.modeContainer.bottom + 16F.dp(this).toInt()
    }

    override fun onCollectSharedElements(): List<Pair<View, String>> {
        return emptyList()
    }

}