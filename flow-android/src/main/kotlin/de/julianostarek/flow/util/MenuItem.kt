package de.julianostarek.flow.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MenuItem
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import de.julianostarek.flow.R

fun MenuItem.animateRealtimeSignal(context: Context) {
    if (icon !is AnimatedVectorDrawableCompat) {
        icon = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_ic_realtime_signal)
        (icon as AnimatedVectorDrawableCompat).registerAnimationCallback(object :
            Animatable2Compat.AnimationCallback() {
            override fun onAnimationStart(drawable: Drawable?) {
                // this@animateRealtimeSignal.isVisible = true
            }

            override fun onAnimationEnd(drawable: Drawable?) {
                this@animateRealtimeSignal.isVisible = false
            }
        })
    }
    isVisible = true
    (icon as AnimatedVectorDrawableCompat).start()
}