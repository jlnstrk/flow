package de.julianostarek.flow.ui.common.view.base

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class EffectivelyWrappingLinearLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.UNSPECIFIED)
    }

}