package de.julianostarek.flow.ui.common.profileselector

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.color.MaterialColors
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemNetworkProfileBinding
import de.julianostarek.flow.profile.FlowProfile
import de.julianostarek.flow.ui.common.span.ProductIconSpan
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.iconResId
import de.julianostarek.flow.util.view.recyclerView

class NetworkProfilePager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    darkContext: Context = ContextThemeWrapper(
        context, R.style.ThemeOverlay_MaterialComponents_Dark
    ).also {
        val config = Configuration(context.resources.configuration)
        config.uiMode = config.uiMode and Configuration.UI_MODE_NIGHT_YES.inv()
        config.uiMode = config.uiMode or Configuration.UI_MODE_NIGHT_NO
        it.applyOverrideConfiguration(config)
    }
) : FrameLayout(darkContext, attrs, defStyleAttr) {
    private val viewPager: ViewPager2 = ViewPager2(darkContext, attrs, defStyleAttr)
    private val adapter: Adapter = Adapter()

    var currentSelection: FlowProfile
        get() = VALUES[viewPager.currentItem]
        set(value) {
            viewPager.setCurrentItem(value.ordinal, true)
        }

    init {
        addView(viewPager, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        viewPager.registerOnPageChangeCallback(ColorCrossFade())
        viewPager.adapter = adapter
    }

    private inner class ColorCrossFade : ViewPager2.OnPageChangeCallback() {
        private val evaluator: ArgbEvaluator = ArgbEvaluator()

        override fun onPageScrollStateChanged(state: Int) {
            // ignore
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val current =
                (viewPager.recyclerView.findViewHolderForAdapterPosition(position) as Page)
                    .color
            val next = if (positionOffset > 0.0F) {
                (viewPager.recyclerView.findViewHolderForAdapterPosition(position + 1) as Page)
                    .color
            } else current
            val evaluated = evaluator.evaluate(positionOffset, current, next)
            setBackgroundColor(evaluated as Int)
        }

        override fun onPageSelected(position: Int) {
            // ignore
        }

    }

    private inner class Adapter : RecyclerView.Adapter<Page>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Page {
            return Page(parent)
        }

        override fun onBindViewHolder(holder: Page, position: Int) {
            holder.bindTo(VALUES[position])
        }

        override fun onViewRecycled(holder: Page) {
            holder.unbind()
        }

        override fun getItemCount(): Int {
            return VALUES.size
        }

    }

    private inner class Page(parent: ViewGroup) :
        BindingViewHolder<FlowProfile, ItemNetworkProfileBinding>(
            parent,
            ItemNetworkProfileBinding::inflate
        ) {
        var color: Int = Color.TRANSPARENT

        override fun bindTo(data: FlowProfile) {
            color = MaterialColors.getColor(
                ContextThemeWrapper(itemView.context, data.themeRes),
                R.attr.colorPrimary,
                Color.TRANSPARENT
            )
            viewBinding.name.text = data.name.replace('_', ' ')
            val productsBuilder = SpannableStringBuilder()
            data.profile.brandingConfig.forEachIndexed { index, product ->
                if (index > 0) {
                    productsBuilder.append("  ")
                }
                val style = data.styles.resolveProductStyle(product)

                productsBuilder.append(
                    ".",
                    ProductIconSpan(itemView.context, style.iconResId(context)),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            viewBinding.products.setText(productsBuilder, TextView.BufferType.SPANNABLE)
        }

        override fun unbind() {
            super.unbind()
            color = Color.TRANSPARENT
            viewBinding.name.text = null
            viewBinding.products.text = null
            viewBinding.providedBy.text = null
        }

    }

    private companion object {
        val VALUES = FlowProfile.values()
    }

}