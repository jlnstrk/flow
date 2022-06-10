package de.julianostarek.flow.ui.main.trips.recents

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemRouteBinding
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.ui.common.diff.RouteDiffItemCallback
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.formatName
import de.julianostarek.flow.util.type.captionAppearanceResId

class RecentRouteViewHolder(parent: ViewGroup) :
    BindingViewHolder<RouteEntity, ItemRouteBinding>(parent, ItemRouteBinding::inflate),
    View.OnClickListener {
    private val captionOne = TextAppearanceSpan(
        itemView.context,
        itemView.context.captionAppearanceResId()
    )
    private val captionTwo = TextAppearanceSpan(
        itemView.context,
        itemView.context.captionAppearanceResId()
    )
    private val captionThree = TextAppearanceSpan(
        itemView.context,
        itemView.context.captionAppearanceResId()
    )

    interface Observer {

        fun onRouteClicked(viewHolder: RecentRouteViewHolder)

        fun onRouteFavoriteClicked(viewHolder: RecentRouteViewHolder)

    }

    init {
        itemView.setOnClickListener(this)
        viewBinding.favorite.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            viewBinding.root -> adapterAsOptional<Observer>()?.onRouteClicked(this)
            viewBinding.favorite -> adapterAsOptional<Observer>()?.onRouteFavoriteClicked(this)
        }
    }

    override fun bindTo(data: RouteEntity) {
        invalidateLocations(data)
        invalidateFavorite(data)
    }

    override fun bindTo(data: RouteEntity, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(RouteDiffItemCallback.Signal.LOCATIONS)) {
            invalidateLocations(data)
        }
        if (payloads.contains(RouteDiffItemCallback.Signal.FAVORITE)) {
            invalidateFavorite(data)
        }
    }

    private fun invalidateLocations(data: RouteEntity) {
        val textBuilder = SpannableStringBuilder()
        textBuilder.append(
            data.origin.formatName(
                itemView.context,
                captionOne,
                target = viewBinding.text,
                productsNewline = true
            )
        )
        textBuilder.append('\n')
        if (data.via.isNotEmpty()) {
            textBuilder.append(
                "\n+${data.via.size} intermediate destination(s)\n\n",
                captionTwo,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            textBuilder.append("\n", captionTwo, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        textBuilder.append(
            data.destination.formatName(
                itemView.context,
                captionThree,
                target = viewBinding.text,
                productsNewline = true
            )
        )
        viewBinding.text.setText(textBuilder, TextView.BufferType.SPANNABLE)
    }

    private fun invalidateFavorite(data: RouteEntity) {
        val drawableRes = if (data.isFavorite) {
            R.drawable.ic_favorite_24dp
        } else R.drawable.ic_favorite_border_24dp
        viewBinding.favorite.setImageResource(drawableRes)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.text.text = null
        viewBinding.favorite.setImageDrawable(null)
    }

}