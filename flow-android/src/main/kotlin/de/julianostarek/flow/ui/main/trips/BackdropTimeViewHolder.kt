package de.julianostarek.flow.ui.main.trips

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import android.widget.TextView
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemBackdropTimeBinding
import de.julianostarek.flow.util.datetime.DATE_TIME_FORMAT_UI
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import kotlinx.datetime.LocalDateTime

class BackdropTimeViewHolder(parent: ViewGroup) :
    BindingViewHolder<LocalDateTime?, ItemBackdropTimeBinding>(
        parent,
        ItemBackdropTimeBinding::inflate
    ) {

    override fun bindTo(data: LocalDateTime?) {
        invalidateTime(data)
        invalidateMode(false)
    }

    fun invalidateTime(data: LocalDateTime?) {
        val hintSpan = ForegroundColorSpan(viewBinding.time.currentHintTextColor)
        val res = viewBinding.mode.resources
        val spannedString = SpannableStringBuilder()
            .append(
                res.getString(R.string.input_prefix_at_time),
                hintSpan,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            .append(" ")
            .append(
                if (data == null) res.getString(R.string.input_now)
                else DATE_TIME_FORMAT_UI.formatDateTime(data)
            )
        viewBinding.time.setText(spannedString, TextView.BufferType.SPANNABLE)
    }

    fun invalidateMode(mode: Boolean?) {
        val modeRes = if (mode == true) R.string.mode_arrival_short else R.string.mode_departure_short
        viewBinding.mode.setText(modeRes)
    }

    override fun unbind() {
        super.unbind()
        viewBinding.time.text = null
    }

}