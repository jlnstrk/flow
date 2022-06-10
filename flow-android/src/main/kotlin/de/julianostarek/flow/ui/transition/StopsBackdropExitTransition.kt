package de.julianostarek.flow.ui.transition

import android.content.Context
import androidx.transition.Transition
import de.julianostarek.flow.R

class StopsBackdropExitTransition(context: Context) : BackdropExitTransition(context) {

    override fun addFadeTargets(context: Context, transition: Transition) {
        transition.addTarget(context.getString(R.string.tn_stops_location))
        transition.addTarget(context.getString(R.string.tn_stops_mode))
        transition.addTarget(context.getString(R.string.tn_stops_time))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_1))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_2))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_3))
    }

}