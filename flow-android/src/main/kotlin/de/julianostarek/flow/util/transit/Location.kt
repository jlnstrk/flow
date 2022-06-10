package de.julianostarek.flow.util.transit

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import de.julianostarek.flow.R
import de.julianostarek.flow.util.text.appendProducts
import de.jlnstrk.transit.common.model.Location

fun Location?.applyTo(textView: TextView, prefixRes: Int) {
    val text = if (this is Location.Point) {
        textView.resources.getString(R.string.action_my_location)
    } else this?.name
    val spanBuilder = SpannableStringBuilder()
        .append(
            textView.resources.getString(prefixRes),
            ForegroundColorSpan(textView.currentHintTextColor),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        .append(" ")
        .append(text)
    if (this is Location.Station) {
        spanBuilder.appendProducts(textView.context, products, textView)
    }
    val currentDrawables = textView.compoundDrawables
    if (this is Location.Point && currentDrawables[2] == null) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            currentDrawables[0],
            currentDrawables[1],
            ResourcesCompat.getDrawable(textView.resources, R.drawable.ic_my_location_24dp, null),
            currentDrawables[3]
        )
    } else if (this !is Location.Point && currentDrawables[2] != null) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            currentDrawables[0],
            currentDrawables[1],
            null,
            currentDrawables[3]
        )
    }
    textView.setText(spanBuilder, TextView.BufferType.SPANNABLE)
}