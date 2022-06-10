package de.julianostarek.flow.ui.common.view.base

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.Keep

open class TintableFrameLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var backgroundTint: Int
        @Keep
        get() = backgroundTintList!!.defaultColor
        @Keep
        set(value) {
            backgroundTintList = ColorStateList.valueOf(value)
        }

}