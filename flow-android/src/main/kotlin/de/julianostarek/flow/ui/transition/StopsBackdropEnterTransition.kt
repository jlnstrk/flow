package de.julianostarek.flow.ui.transition

import android.content.Context
import androidx.transition.Transition
import de.julianostarek.flow.R

class StopsBackdropEnterTransition(context: Context, direction: Int) :
    BackdropEnterTransition(context, direction) {

    override fun addSlideFadeTargets(context: Context, transition: Transition) {
        transition.addTarget(context.getString(R.string.tn_stops_location))
        transition.addTarget(context.getString(R.string.tn_stops_mode))
        transition.addTarget(context.getString(R.string.tn_stops_time))
    }

    override fun addFadeTargets(context: Context, transition: Transition) {
        transition.addTarget(context.getString(R.string.tn_backdrop_field_1))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_2))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_3))
    }

}