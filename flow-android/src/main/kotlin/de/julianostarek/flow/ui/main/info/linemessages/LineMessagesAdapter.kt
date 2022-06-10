package de.julianostarek.flow.ui.main.info.linemessages

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.jlnstrk.transit.common.model.Message

class LineMessagesAdapter(
    private val listener: Listener
) : BaseListAdapter<Message, LineMessageViewHolder>(ITEM_CALLBACK),
    LineMessageViewHolder.Observer {

    fun interface Listener {
        fun onMessageClicked(message: Message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineMessageViewHolder {
        return LineMessageViewHolder(parent)
    }

    override fun onViewAttachedToWindow(holder: LineMessageViewHolder) {
        holder.attachObserver(this)
    }

    override fun onViewDetachedFromWindow(holder: LineMessageViewHolder) {
        holder.detachObserver()
    }

    override fun onMessageClicked(message: Message) = listener.onMessageClicked(message)

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<Message>() {

            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return false // oldItem.created == newItem.created && oldItem.body?.head == newItem.body?.head
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return false
            }

        }
    }

}