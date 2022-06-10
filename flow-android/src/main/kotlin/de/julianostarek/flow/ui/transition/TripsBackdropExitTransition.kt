package de.julianostarek.flow.ui.transition

import android.content.Context
import androidx.transition.Transition
import de.julianostarek.flow.R

class TripsBackdropExitTransition(context: Context) : BackdropExitTransition(context) {

    override fun addFadeTargets(context: Context, transition: Transition) {
        transition.addTarget(context.getString(R.string.tn_trips_location))
        transition.addTarget(context.getString(R.string.tn_trips_wait_time))
        transition.addTarget(context.getString(R.string.tn_trips_time))
        transition.addTarget(context.getString(R.string.tn_trips_time_mode))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_1))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_2))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_3))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_4))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_5))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_6))
    }

}