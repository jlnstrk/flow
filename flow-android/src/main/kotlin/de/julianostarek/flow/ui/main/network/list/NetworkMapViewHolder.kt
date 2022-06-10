package de.julianostarek.flow.ui.main.network.list

import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import de.julianostarek.flow.databinding.ItemNetworkMapBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.prependProducts
import de.jlnstrk.transit.common.model.NetworkMap

class NetworkMapViewHolder(parent: ViewGroup) :
    BindingViewHolder<NetworkMap, ItemNetworkMapBinding>(
        parent,
        ItemNetworkMapBinding::inflate
    ), View.OnClickListener {

    override fun bindTo(data: NetworkMap) {
        super.bindTo(data)
        viewBinding.title.setText(
            SpannableStringBuilder()
                .prependProducts(viewBinding.title.context, data.products, viewBinding.title)
                .append(data.title), TextView.BufferType.SPANNABLE
        )
        viewBinding.author.text = data.author
        Glide.with(viewBinding.thumbnail)
            .asBitmap()
            .load(data.thumbnailUrl)
            .centerCrop()
            .transition(
                BitmapTransitionOptions
                    .withCrossFade()
            )
            .into(viewBinding.thumbnail)
        if (data.fileUrl != null) {
            viewBinding.root.setOnClickListener(this)
        } else {
            viewBinding.root//.forEach { it.alpha = 0.5F }
        }
    }

    override fun unbind() {
        super.unbind()
        viewBinding.root.setOnClickListener(null)
        viewBinding.root//.forEach { it.alpha = 1.0F }
        viewBinding.title.text = null
        viewBinding.author.text = null
        Glide.with(viewBinding.thumbnail)
            .clear(viewBinding.thumbnail)
    }

    override fun onClick(view: View) {
        when (view) {
            viewBinding.root -> {
                view.context.startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(data?.fileUrl))
                )
            }
        }
    }

}