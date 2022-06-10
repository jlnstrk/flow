package de.julianostarek.flow.ui.common.viewholder

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import de.jlnstrk.transit.common.model.ProductClass
import de.julianostarek.flow.databinding.ItemHeaderBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.iconResId
import kotlin.math.roundToInt

open class HeaderViewHolder<T>(parent: ViewGroup) :
    BindingViewHolder<T, ItemHeaderBinding>(parent, ItemHeaderBinding::inflate) {

    override fun unbind() {
        super.unbind()
        viewBinding.itemText.text = null
    }

    class StringRes(parent: ViewGroup) : HeaderViewHolder<Int>(parent) {

        override fun bindTo(data: Int) {
            viewBinding.itemText.setText(data)
        }

    }

    class Text(parent: ViewGroup) : HeaderViewHolder<String>(parent) {

        override fun bindTo(data: String) {
            viewBinding.itemText.text = data
        }

    }

    class ProductIcon(parent: ViewGroup) : HeaderViewHolder<ProductClass>(parent) {

        init {
            /* 40dp min chip size + 8dp - 24dp icon size */
            val padding = 24F.dp(this).roundToInt()
            viewBinding.itemText.compoundDrawablePadding = padding
        }

        override fun bindTo(data: ProductClass) {
            super.bindTo(data)
            viewBinding.itemText.text = (data as? Enum<*>)?.name?.replace('_', ' ') ?: "?"
            val productStyle = itemView.context.styles.resolveProductStyle(data)
            val drawable = ContextCompat.getDrawable(
                itemView.context,
                productStyle.iconResId(itemView.context)
            )
            viewBinding.itemText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawable, null, null, null
            )
        }

        override fun unbind() {
            super.unbind()
            viewBinding.itemText.setCompoundDrawablesRelative(
                null, null, null, null
            )
        }

    }

}