package de.julianostarek.flow.ui.common.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import de.julianostarek.flow.ui.component.linechip.LineChipSpan2
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.graphics.setBoundsScaledWidth
import de.julianostarek.flow.util.transit.iconRes
import de.julianostarek.flow.util.type.captionAppearanceResId
import kotlin.math.roundToInt
import de.jlnstrk.transit.common.model.Line

class LineGroupView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        TextViewCompat.setTextAppearance(this, context.captionAppearanceResId())
        gravity = Gravity.CENTER_VERTICAL
        compoundDrawablePadding = 4F.dp(this).roundToInt()
    }

    fun setLines(lines: List<Line>?) {
        if (lines.isNullOrEmpty()) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
            setText(null, BufferType.SPANNABLE)
            return
        }
        try {
            val drawable = ContextCompat.getDrawable(context, lines.first().iconRes(context))!!
            drawable.setBoundsScaledWidth(24F.dp(this).roundToInt())
            setCompoundDrawablesRelative(drawable, null, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val textBuilder = SpannableStringBuilder()
        lines.forEach {
            textBuilder.append(".", LineChipSpan2(context, it), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        setText(textBuilder, BufferType.SPANNABLE)
    }


}