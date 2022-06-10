package de.julianostarek.flow.ui.transition

import android.content.Context
import androidx.transition.Transition
import de.julianostarek.flow.R

class InfoBackdropEnterTransition(context: Context, direction: Int) :
    BackdropEnterTransition(context, direction) {

    override fun addSlideFadeTargets(context: Context, transition: Transition) {
        transition.addTarget(context.getString(R.string.tn_info_mode))
        transition.addTarget(context.getString(R.string.tn_info_city))
    }

    override fun addFadeTargets(context: Context, transition: Transition) {
        transition.addTarget(context.getString(R.string.tn_backdrop_field_1))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_2))
    }

}