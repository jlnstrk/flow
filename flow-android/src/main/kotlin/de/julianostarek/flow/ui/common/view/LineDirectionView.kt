package de.julianostarek.flow.ui.common.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import de.julianostarek.flow.ui.component.linechip.LineChipSpan2
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.graphics.setBoundsScaledWidth
import de.julianostarek.flow.util.transit.iconRes
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.julianostarek.flow.util.type.subtitle1AppearanceResId
import kotlin.math.roundToInt
import de.jlnstrk.transit.common.model.Journey
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Location

class LineDirectionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val lineView: TextView = AppCompatTextView(context, attrs, defStyleAttr)
    val directionView: TextView = AppCompatTextView(context, attrs, defStyleAttr)

    init {
        orientation = HORIZONTAL

        TextViewCompat.setTextAppearance(lineView, context.captionAppearanceResId())

        directionView.layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
            weight = 1.0F
            marginStart = 8F.dp(this@LineDirectionView).roundToInt()
        }
        TextViewCompat.setTextAppearance(directionView, context.subtitle1AppearanceResId())
        lineView.compoundDrawablePadding = 4F.dp(this).roundToInt()
        lineView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        lineView.gravity = Gravity.CENTER_VERTICAL
        addView(lineView)
        addView(directionView)
    }

    fun setFromJourney(journey: Journey?, showProduct: Boolean = true) {
        setFromLineDirection(journey?.line, journey?.directionTo, showProduct)
    }

    fun setFromLineDirection(line: Line?, direction: Location?, showProduct: Boolean = true) {
        if (line == null) {
            lineView.setText(null, TextView.BufferType.SPANNABLE)
            lineView.setCompoundDrawablesRelative(null, null, null, null)
            directionView.text = null
            return
        }
        if (showProduct) {
            val drawable = ContextCompat.getDrawable(context, line.iconRes(context))!!
            drawable.setBoundsScaledWidth(24F.dp(this).roundToInt())
            lineView.setCompoundDrawablesRelative(drawable, null, null, null)
        }

        val text = SpannableString(".")
        text.setSpan(LineChipSpan2(context, line), 0,  text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        lineView.setText(text, TextView.BufferType.SPANNABLE)

        directionView.text = direction?.name
    }

}