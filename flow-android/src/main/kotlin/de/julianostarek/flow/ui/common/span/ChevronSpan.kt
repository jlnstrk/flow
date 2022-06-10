package de.julianostarek.flow.ui.common.span

import android.content.Context
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.span.base.ImageSpanCompat

class ChevronSpan(context: Context) :
    ImageSpanCompat(
        context,
        R.drawable.ic_chevron_right_18dp,
        ALIGN_CENTER,
        tintAttr = com.google.android.material.R.attr.colorOnSurface
    )

