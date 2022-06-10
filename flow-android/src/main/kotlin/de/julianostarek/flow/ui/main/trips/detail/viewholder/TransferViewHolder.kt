package de.julianostarek.flow.ui.main.trips.detail.viewholder

import android.view.ViewGroup
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemTransferBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import kotlin.time.Duration

class TransferViewHolder(parent: ViewGroup) :
    BindingViewHolder<Duration, ItemTransferBinding>(parent, ItemTransferBinding::inflate) {

    override fun bindTo(data: Duration) {
        super.bindTo(data)
        val hours = data.inWholeHours
        val quantityString: String = if (hours <= 0) {
            viewBinding.text.resources.getString(
                R.string.leg_transfer_duration_minutes,
                data.inWholeMinutes
            )
        } else {
            viewBinding.text.resources.getQuantityString(
                R.plurals.leg_transfer_duration_hours,
                hours.toInt(), data.inWholeHours, data.inWholeMinutes % 60L
            )
        }
        viewBinding.text.text = quantityString
    }

    override fun unbind() {
        super.unbind()
        viewBinding.text.text = null
    }

}