package de.julianostarek.flow.util.text

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView

fun TextView.setTextWithPrefix(prefixRes: Int, textRes: Int = -1, text: String? = null) {
    val prefix = resources.getString(prefixRes)
    val effectiveText = text ?: resources.getString(textRes)

    val hintSpan = ForegroundColorSpan(currentHintTextColor)
    val spannedString = SpannableStringBuilder()
        .append(prefix, hintSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        .append(' ')
        .append(effectiveText)
    setText(spannedString, TextView.BufferType.SPANNABLE)
}