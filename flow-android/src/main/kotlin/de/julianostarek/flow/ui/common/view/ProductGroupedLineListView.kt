package de.julianostarek.flow.ui.common.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import de.julianostarek.flow.ui.component.linechip.LineChipSpan2
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.graphics.setBoundsScaledWidth
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.LineSet
import de.julianostarek.flow.util.iconResId
import kotlin.math.roundToInt

class ProductGroupedLineListView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :
    AppCompatTextView(context, attrs, defStyleAttr) {
    private val stringBuilder = SpannableStringBuilder()

    var lines: LineSet? = null
        set(value) {
            field = value
            if (value != null) {
                val stringBuilder = SpannableStringBuilder()
                value.forEach {
                    stringBuilder.append(
                        ".",
                        LineChipSpan2(context, it),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                val productStyle = context.styles.resolveProductStyle(
                    value.first().product
                )
                val drawable = ContextCompat.getDrawable(context, productStyle.iconResId(context))!!
                drawable.setBoundsScaledWidth(24F.dp(this).roundToInt())
                setCompoundDrawablesRelative(drawable, null, null, null)
                setText(stringBuilder, BufferType.SPANNABLE)
            } else {
                setText(null, BufferType.SPANNABLE)
            }
        }

    init {
        gravity = Gravity.CENTER_VERTICAL
        compoundDrawablePadding = 4F.dp(this).roundToInt()
        maxLines = 1
        isSingleLine = true
        TextViewCompat.setTextAppearance(this, context.captionAppearanceResId())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (text != null && lines != null) {
            val avail =
                measuredWidth - compoundPaddingLeft.toFloat() - compoundPaddingRight.toFloat()
            val ellipsisHeuristic = "   " + "+00 more"
            val ellipsized =
                TextUtils.ellipsize(
                    text,
                    paint,
                    avail - paint.measureText(ellipsisHeuristic),
                    TextUtils.TruncateAt.END
                )
            if (ellipsized != text) {
                val moreCount = lines!!.size - ellipsized.length + ellipsisHeuristic.length
                try {
                    stringBuilder.append(ellipsized)
                    stringBuilder.replace(
                        ellipsized.length - 1, ellipsized.length, ellipsisHeuristic
                            .replace("00", moreCount.toString())
                    )
                    setText(stringBuilder, BufferType.SPANNABLE)
                    stringBuilder.clear()
                } catch (e: Exception) {
                    println(text)
                    println(avail)
                    println(ellipsized)
                }
            }
        }
    }

}