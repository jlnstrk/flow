package de.julianostarek.flow.ui.transition

import android.content.Context
import androidx.transition.Transition
import de.julianostarek.flow.R

class InfoBackdropExitTransition(context: Context) : BackdropExitTransition(context) {

    override fun addFadeTargets(context: Context, transition: Transition) {
        transition.addTarget(context.getString(R.string.tn_info_mode))
        transition.addTarget(context.getString(R.string.tn_info_city))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_1))
        transition.addTarget(context.getString(R.string.tn_backdrop_field_2))
    }

}