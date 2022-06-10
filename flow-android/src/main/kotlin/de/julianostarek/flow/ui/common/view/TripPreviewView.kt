package de.julianostarek.flow.ui.common.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.Gravity
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import de.julianostarek.flow.ui.common.span.ChevronSpan
import de.julianostarek.flow.ui.common.span.IndividualSpan
import de.julianostarek.flow.ui.common.span.ProductSpan
import de.julianostarek.flow.ui.component.linechip.LineChipDrawable
import de.julianostarek.flow.ui.component.linechip.LineChipSpan2
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.Leg
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Trip

class TripPreviewView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {
    private val textView = AppCompatTextView(context)

    init {
        overScrollMode = OVER_SCROLL_NEVER
        isHorizontalFadingEdgeEnabled = true

        val childParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        childParams.gravity = Gravity.CENTER_VERTICAL
        addView(textView, childParams)

        TextViewCompat.setTextAppearance(textView, context.captionAppearanceResId())
    }

    fun setTrip(trip: Trip?) {
        if (trip?.legs == null) {
            textView.setText(null, TextView.BufferType.SPANNABLE)
            return
        }
        val textBuilder = SpannableStringBuilder()
        trip.legs
            .filterNot { it is Leg.Transfer }
            .forEachIndexed { index, leg ->
                if (index > 0) {
                    textBuilder
                        .append(' ')
                        .append(
                            ".",
                            ChevronSpan(context),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        .append(' ')
                }
                when (leg) {
                    is Leg.Individual -> {
                        textBuilder.append(
                            ".",
                            IndividualSpan(context, leg),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    is Leg.Public -> {
                        textBuilder.append(
                            ".",
                            ProductSpan(context, leg.journey.line.product),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        textBuilder.append(' ')

                        val all = mutableListOf(leg.journey.line)
                        leg.alternatives?.forEach {
                            all.add(it.line)
                        }
                        val reduced = all.groupBy(Line::name)
                            .map { it.value.first() }
                            .sortedBy(Line::name)
                        reduced.forEachIndexed { index, product ->
                            textBuilder.append(
                                ".",
                                LineChipSpan2(
                                    context, product, when {
                                        index == 0 && reduced.size == 1 -> LineChipDrawable.Mode.ONLY
                                        index == 0 -> LineChipDrawable.Mode.START
                                        index == reduced.lastIndex -> LineChipDrawable.Mode.END
                                        else -> LineChipDrawable.Mode.MID
                                    }
                                ),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                    else -> {}
                }
            }
        textView.setText(textBuilder, TextView.BufferType.SPANNABLE)
    }

}