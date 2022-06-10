package de.julianostarek.flow.ui.common.mapbackdrop.transition

import android.content.Context
import android.view.Gravity
import android.view.animation.DecelerateInterpolator
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionSet
import de.julianostarek.flow.R

class MapBackdropSceneEnterTransition(context: Context) : TransitionSet() {

    init {
        addTransition(
            Fade()
                .excludeTarget(R.id.footer, true)
                .excludeTarget(R.id.bottom_sheet, true)
                .excludeTarget(R.id.back_button, true)
                .excludeTarget(R.id.info_button, true)
        )
        addTransition(
            Slide(Gravity.BOTTOM)
                .addTarget(R.id.footer)
        )

        interpolator = DecelerateInterpolator()
    }

}