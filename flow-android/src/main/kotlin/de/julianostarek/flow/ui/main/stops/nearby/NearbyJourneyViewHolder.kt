package de.julianostarek.flow.ui.main.stops.nearby

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import de.julianostarek.flow.databinding.ItemJourneyLocationBinding
import de.julianostarek.flow.ui.common.adapter.base.BaseChangeSignal
import de.julianostarek.flow.ui.common.diff.JourneyDiffItemCallback
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.view.setDisplayStop
import de.julianostarek.flow.util.type.subtitle2AppearanceResId
import de.jlnstrk.transit.common.model.Journey

class NearbyJourneyViewHolder(parent: ViewGroup) :
    BindingViewHolder<Journey, ItemJourneyLocationBinding>(
        parent,
        ItemJourneyLocationBinding::inflate
    ), View.OnClickListener {

    fun interface Observer {
        fun onJourneyClicked(viewHolder: NearbyJourneyViewHolder)
    }

    init {
        itemView.setOnClickListener(this)
        TextViewCompat.setTextAppearance(
            viewBinding.lineDirection.directionView,
            itemView.context.subtitle2AppearanceResId()
        )
        viewBinding.lineDirection.directionView.maxLines = 2
        viewBinding.lineDirection.directionView.ellipsize = TextUtils.TruncateAt.END
    }

    override fun bindTo(data: Journey) {
        invalidateLineAndDirection(data)
        invalidateCountdown(data)
    }

    override fun bindTo(data: Journey, payloads: List<Any>) {
        super.bindTo(data, payloads)
        if (payloads.contains(JourneyDiffItemCallback.ChangeSignal.TYPE)) {
            invalidateLineAndDirection(data)
        }
        if (payloads.contains(JourneyDiffItemCallback.ChangeSignal.TYPE)
            || payloads.contains(JourneyDiffItemCallback.ChangeSignal.STATUS)
            || payloads.contains(JourneyDiffItemCallback.ChangeSignal.REALTIME)
            || payloads.contains(BaseChangeSignal.TIME_TICK)
        ) {
            invalidateCountdown(data)
        }
    }

    private fun invalidateLineAndDirection(data: Journey) {
        viewBinding.lineDirection.setFromJourney(data, true)
    }

    private fun invalidateCountdown(data: Journey) {
        viewBinding.countdown.setDisplayStop(data.stop)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.lineDirection.setFromJourney(null)
        viewBinding.countdown.clearDisplay()
        itemView.alpha = 1.0F
    }

    override fun onClick(view: View) {
        adapterAsOptional<Observer>()?.onJourneyClicked(this)
    }

}