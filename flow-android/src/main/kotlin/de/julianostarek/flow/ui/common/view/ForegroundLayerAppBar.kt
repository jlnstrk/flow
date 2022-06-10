package de.julianostarek.flow.ui.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import de.julianostarek.flow.R

class ForegroundLayerAppBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppBarLayout(context, attrs, defStyleAttr) {

    init {
        (background as MaterialShapeDrawable).apply {
            shapeAppearanceModel = ShapeAppearanceModel.builder(
                context,
                R.style.ShapeAppearance_Sequence_LargeComponent,
                com.google.android.material.R.style.ShapeAppearanceOverlay_MaterialComponents_BottomSheet
            )
                .build()
        }
        outlineProvider = ViewOutlineProvider.BACKGROUND
    }

}