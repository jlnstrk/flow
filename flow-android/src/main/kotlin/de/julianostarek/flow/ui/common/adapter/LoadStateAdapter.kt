package de.julianostarek.flow.ui.common.adapter

import android.animation.Animator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.view.LoadStateIndicator
import de.julianostarek.flow.util.graphics.dp
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder
import kotlin.math.abs
import kotlin.math.roundToInt

class LoadStateAdapter : RecyclerView.Adapter<LoadStateAdapter.ViewHolder>() {
    var loadState: LoadStateIndicator.State = LoadStateIndicator.State.Hidden
        set(value) {
            if (field === LoadStateIndicator.State.Hidden
                && value !== LoadStateIndicator.State.Hidden
            ) {
                notifyItemInserted(0)
            } else if (field !== LoadStateIndicator.State.Hidden
                && value === LoadStateIndicator.State.Hidden
            ) {
                notifyItemRemoved(0)
            } else if (value !== LoadStateIndicator.State.Hidden) {
                notifyItemChanged(0)
            }
            field = value
        }
    var mode: Mode = Mode.FULL_PAGE
        set(value) {
            field = value
            if (itemCount == 1) {
                notifyItemChanged(0)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lsi_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setMode(mode)
        holder.indicator.moveToState(loadState)
    }

    override fun getItemCount(): Int {
        return if (loadState === LoadStateIndicator.State.Hidden) 0 else 1
    }

    enum class Mode {
        FULL_PAGE,
        NESTED
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnimateViewHolder {
        val indicator: LoadStateIndicator get() = itemView as LoadStateIndicator

        fun setMode(mode: Mode) {
            when (mode) {
                Mode.FULL_PAGE -> {
                    itemView.minimumHeight = 0
                    itemView.layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 96F.dp(itemView).roundToInt()
                    }
                }
                Mode.NESTED -> {
                    itemView.minimumHeight = 128F.dp(this).roundToInt()
                    itemView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            }
        }

        override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
            holder.itemView.alpha = 0F
        }

        override fun animateAddImpl(
            holder: RecyclerView.ViewHolder,
            listener: Animator.AnimatorListener
        ) {
            holder.itemView.animate().apply {
                alpha(1F)
                duration = DELEGATE.addDuration
                interpolator = interpolator
                setListener(listener)
                startDelay = getAddDelay(holder)
            }.start()
        }

        override fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder) {

        }

        override fun animateRemoveImpl(
            holder: RecyclerView.ViewHolder,
            listener: Animator.AnimatorListener
        ) {
            holder.itemView.animate().apply {
                alpha(0F)
                duration = DELEGATE.removeDuration
                interpolator = interpolator
                setListener(listener)
                startDelay = getRemoveDelay(holder)
            }.start()
        }

        private fun getAddDelay(holder: RecyclerView.ViewHolder): Long {
            return abs(holder.adapterPosition * DELEGATE.addDuration / 4)
        }

        private fun getRemoveDelay(holder: RecyclerView.ViewHolder): Long {
            return abs(holder.oldPosition * DELEGATE.removeDuration / 4)
        }

        companion object {
            private val DELEGATE = FadeInAnimator()

        }

    }

}