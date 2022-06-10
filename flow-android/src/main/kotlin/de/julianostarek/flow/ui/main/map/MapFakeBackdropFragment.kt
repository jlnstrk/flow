package de.julianostarek.flow.ui.main.map

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
import de.julianostarek.flow.databinding.FragmentBackdropInfoBinding
import de.julianostarek.flow.databinding.IncludeProductsBarBinding
import de.julianostarek.flow.ui.common.backdrop.BackdropFragment
import de.julianostarek.flow.ui.main.info.InfoViewModel
import de.julianostarek.flow.ui.settings.SettingsActivity
import de.julianostarek.flow.ui.transition.InfoBackdropEnterTransition
import de.julianostarek.flow.ui.transition.InfoBackdropExitTransition
import de.julianostarek.flow.util.text.setTextWithPrefix
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt

class MapFakeBackdropFragment : BackdropFragment(), View.OnClickListener {
    override val maxNumSharedElements: Int = 2
    override val headerRes: Int = R.string.header_connections
    override val menuRes: Int = R.menu.menu_backdrop_trip
    override val anchorPosition: Int = 3

    private val viewModel: InfoViewModel by activityViewModels()
    private lateinit var viewBinding: FragmentBackdropInfoBinding
    private lateinit var productsBarBinding: IncludeProductsBarBinding

    override val linearLayout: LinearLayout
        get() = viewBinding.root

    override fun applyEnterTransition(context: Context, direction: Int) {
        this.enterTransition = InfoBackdropEnterTransition(context, direction)
    }

    override fun applyExitTransition(context: Context) {
        this.exitTransition = InfoBackdropExitTransition(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentBackdropInfoBinding.inflate(inflater, container, false)
        productsBarBinding = IncludeProductsBarBinding.bind(viewBinding.root)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.modeContainer.setOnClickListener(this)
        viewBinding.settings.setOnClickListener(this)

        view.doOnPreDraw {
            startPostponedEnterTransition()
        }

        productsBarBinding.productsBar.subscribe {
            viewModel.productFilter.value = it
        }

        viewModel.city.observe(viewLifecycleOwner) {
            viewBinding.cityText.setTextWithPrefix(
                R.string.input_prefix_city, text = when (it) {
                    null -> getString(R.string.input_all)
                    else -> it
                }
            )
        }
        viewModel.profileConfig.observe(viewLifecycleOwner) { profileConfig ->
            productsBarBinding.productsBar.submitConfiguration(profileConfig.constant)
        }
        viewModel.productFilter.observe(viewLifecycleOwner) {
            productsBarBinding.productsBar.submitSelection(it)
        }
    }

    override fun onCollectSharedElements(): List<Pair<View, String>> {
        val modeName = resources.getString(R.string.tn_backdrop_field_1)
        val cityName = resources.getString(R.string.tn_backdrop_field_2)
        return listOf(
            viewBinding.modeContainer to modeName,
            viewBinding.cityContainer to cityName
        )
    }

    override fun getConcealedBackdropHeight(): Int {
        return viewBinding.cityContainer.bottom + 16F.dp(this).roundToInt()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            viewBinding.settings.id -> {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }
        }
    }

}