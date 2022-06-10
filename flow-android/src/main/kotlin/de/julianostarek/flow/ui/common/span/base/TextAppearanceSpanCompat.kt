package de.julianostarek.flow.ui.common.span.base

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.TextPaint
import android.text.style.TextAppearanceSpan
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat

class TextAppearanceSpanCompat(context: Context, appearance: Int) : TextAppearanceSpan(context, appearance) {
    private var _typeface: Typeface? = null

    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val typedArray = context.obtainStyledAttributes(appearance,
                    androidx.appcompat.R.styleable.TextAppearance)
            val typedValue = TypedValue()
            typedArray.getValue(androidx.appcompat.R.styleable.TextAppearance_fontFamily, typedValue)
            this._typeface = ResourcesCompat.getFont(context, typedValue.resourceId)
            typedArray.recycle()
        }
    }

    override fun updateMeasureState(ds: TextPaint) {
        super.updateMeasureState(ds)
        if (_typeface != null) {
            ds.typeface = _typeface
        }
    }

}