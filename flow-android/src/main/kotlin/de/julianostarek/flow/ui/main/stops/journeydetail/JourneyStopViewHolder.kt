package de.julianostarek.flow.ui.main.stops.journeydetail

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.databinding.ItemStopJourneyBinding
import de.julianostarek.flow.ui.common.time.TimeDisplay
import de.julianostarek.flow.ui.common.time.util.buildTimeDisplay
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.julianostarek.flow.util.text.appendLineBreak
import de.julianostarek.flow.util.text.formatPlatforms
import de.julianostarek.flow.util.text.appendProducts
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.Location
import de.jlnstrk.transit.common.model.stop.BaseArrival
import de.jlnstrk.transit.common.model.stop.BaseDeparture
import de.jlnstrk.transit.common.model.stop.Stop

class JourneyStopViewHolder(parent: ViewGroup) :
    BindingViewHolder<Stop, ItemStopJourneyBinding>(parent, ItemStopJourneyBinding::inflate) {
    private val captionSpan: TextAppearanceSpan = TextAppearanceSpan(
        itemView.context,
        itemView.context.captionAppearanceResId()
    )
    private val placeSpan: TextAppearanceSpan = TextAppearanceSpan(
        itemView.context,
        itemView.context.captionAppearanceResId()
    )

    override fun bindTo(data: Stop) {
        populateText(data)
        populateTimes(data)
    }

    private fun populateText(data: Stop) {
        val textBuilder = SpannableStringBuilder()
        if (data.location.place != null) {
            textBuilder.append(data.location.place)
            textBuilder.append('\n')
            textBuilder.setSpan(
                placeSpan,
                0,
                textBuilder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textBuilder.append(data.location.name)
        if (data.location is Location.Station) {
            textBuilder.appendProducts(
                viewBinding.location.context,
                (data.location as Location.Station).products,
                viewBinding.location
            )
        }
        if ((data as? BaseArrival)?.arrivalScheduledPlatform != null
            || (data as? BaseDeparture)?.departureScheduledPlatform != null
        ) {
            val platformsFormatted = data.formatPlatforms(itemView.context)
            textBuilder
                .appendLineBreak()
                .append(platformsFormatted, captionSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        viewBinding.location.setText(textBuilder, TextView.BufferType.SPANNABLE)
    }

    private fun populateTimes(data: Stop) {
        viewBinding.times.setText(
            data.buildTimeDisplay(
                TimeDisplay.Style.ABSOLUTE_RELATIVE,
                itemView.context,
                false
            ), TextView.BufferType.SPANNABLE
        )
    }

    override fun unbind() {
        super.unbind()
        viewBinding.location.text = null
        viewBinding.times.text = null
    }

}