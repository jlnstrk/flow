/*
 * Copyright 2017 Julian Ostarek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy parse the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.julianostarek.flow.ui.common.view.base

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatButton
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import de.julianostarek.flow.R
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt

class ExpandMoreButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr), View.OnClickListener {
    var isExpanded = false
        private set
    private var collapseAnim: AnimatedVectorDrawableCompat? = null
    private var expandAnim: AnimatedVectorDrawableCompat? = null

    @IndicatorGravity
    private var indicatorGravity: Int = INDICATOR_GRAVITY_END
    var onExpandChangeListener: OnExpandChangeListener? = null

    interface OnExpandChangeListener {
        fun onExpandChanged(isExpanded: Boolean)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                null, null, null,
                null
            )
        }
    }

    init {
        setTextColor(ColorStateList.valueOf(currentTextColor))
        gravity = Gravity.CENTER_VERTICAL
        compoundDrawablePadding = 8F.dp(this).roundToInt()
        collapseAnim = AnimatedVectorDrawableCompat.create(
            context,
            R.drawable.anim_ic_arrow_collapse
        )
        expandAnim = AnimatedVectorDrawableCompat.create(
            context,
            R.drawable.anim_ic_arrow_expand
        )

        setBackgroundResource(R.drawable.bg_text_view_expand_more)
        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.ExpandMoreTextView,
                0, 0
            )
            this.indicatorGravity = typedArray.getInt(
                R.styleable.ExpandMoreTextView_indicatorGravity, INDICATOR_GRAVITY_END
            )
        }
        setOnClickListener(this)

        isClickable = true
        isFocusable = true
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (indicatorGravity == INDICATOR_GRAVITY_START)
                expandAnim else null, null, if (indicatorGravity == INDICATOR_GRAVITY_END)
                expandAnim else null, null
        )
    }

    fun reset() {
        isEnabled = true
        isExpanded = false
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (indicatorGravity == INDICATOR_GRAVITY_START)
                expandAnim else null, null, if (indicatorGravity == INDICATOR_GRAVITY_END)
                expandAnim else null, null
        )
    }

    fun toggle() {
        val drawable = if (isExpanded) collapseAnim else expandAnim
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (indicatorGravity == INDICATOR_GRAVITY_START)
                drawable else null, null, if (indicatorGravity == INDICATOR_GRAVITY_END)
                drawable else null, null
        )
        drawable?.start()
        isExpanded = !isExpanded
        onExpandChangeListener?.onExpandChanged(isExpanded)
    }

    override fun onClick(view: View?) = toggle()

    companion object {
        @IntDef()
        annotation class IndicatorGravity

        const val INDICATOR_GRAVITY_START = 0
        const val INDICATOR_GRAVITY_END = 1
    }

}