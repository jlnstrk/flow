package de.julianostarek.flow.ui.transition

import androidx.transition.ChangeBounds

class BackdropSharedElementTransition : ChangeBounds() {

    init {
        setInterpolator { input ->
            var t = input
            t -= 1.0F
            t * t * t * t * t + 1.0F
        }
    }

}