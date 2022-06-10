package de.julianostarek.flow.ui.common

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class ExplicitItemAnimator : DefaultItemAnimator() {
    var enableAnimations: Boolean = false

    override fun animateMove(
        holder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return enableAnimations && super.animateMove(holder, fromX, fromY, toX, toY)
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        return super.animateRemove(holder)
    }

}