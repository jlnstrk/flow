package de.julianostarek.flow.ui.transition

import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionSet
import de.julianostarek.flow.R

abstract class BackdropEnterTransition(context: Context, direction: Int) : TransitionSet() {

    init {
        ordering = ORDERING_SEQUENTIAL
        addTransition(
            TransitionSet()
                .setOrdering(ORDERING_TOGETHER)
                .addTransition(
                    Fade(Fade.MODE_IN)
                        .setDuration(150)
                        .setStartDelay(100)
                        .setInterpolator(LinearInterpolator())
                        .also {
                            addFadeTargets(context, it)
                        }
                        .also {
                            addSlideFadeTargets(context, it)
                        }
                )
                .addTransition(
                    Slide(direction)
                        .setDuration(150)
                        .setInterpolator(DecelerateInterpolator())
                        .also {
                            addSlideFadeTargets(context, it)
                        }
                )

        )
        addTransition(
            Fade(Fade.MODE_IN)
                .setInterpolator(AccelerateInterpolator())
                .addTarget(context.getString(R.string.tn_section_settings))
        )
    }

    abstract fun addSlideFadeTargets(context: Context, transition: Transition)

    abstract fun addFadeTargets(context: Context, transition: Transition)

}