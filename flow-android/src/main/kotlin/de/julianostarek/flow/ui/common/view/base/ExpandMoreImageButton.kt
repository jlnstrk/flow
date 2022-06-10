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
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import de.julianostarek.flow.R

class ExpandMoreImageButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {
    var isExpanded = false
        private set
    private var collapseAnim: AnimatedVectorDrawableCompat? = null
    private var expandAnim: AnimatedVectorDrawableCompat? = null

    init {
        isClickable = true
        collapseAnim = AnimatedVectorDrawableCompat.create(context,
                R.drawable.anim_ic_arrow_collapse)
        expandAnim = AnimatedVectorDrawableCompat.create(context,
                R.drawable.anim_ic_arrow_expand)
        setImageDrawable(expandAnim)
    }

    fun reset() {
        isExpanded = false
        setImageDrawable(expandAnim)
    }

    fun toggle(): Boolean {
        val drawable = if (isExpanded) collapseAnim else expandAnim
        setImageDrawable(drawable)
        drawable?.start()
        isExpanded = !isExpanded
        return isExpanded
    }

}