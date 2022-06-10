package de.julianostarek.flow.ui.main.trips.detail

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import de.julianostarek.flow.ui.main.trips.detail.viewholder.IndividualLegViewHolder
import de.julianostarek.flow.ui.main.trips.detail.viewholder.PublicLegViewHolder
import de.julianostarek.flow.ui.main.trips.detail.viewholder.TransferViewHolder
import de.julianostarek.flow.ui.main.trips.detail.viewholder.TripLocationViewHolder
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt

class TripDetailItemDecoration : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        val viewHolder = parent.getChildViewHolder(view)
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = 16F.dp(parent).roundToInt()
        }
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            outRect.bottom = 16F.dp(parent).roundToInt()
        }
        outRect.right = 24F.dp(parent).roundToInt()
        when (viewHolder) {
            is TripLocationViewHolder -> {
                outRect.left = 20F.dp(parent).roundToInt()
            }
            is TransferViewHolder,
            is IndividualLegViewHolder -> {
                outRect.left = 28F.dp(parent).roundToInt()
            }
            is PublicLegViewHolder -> {
                outRect.left = 24F.dp(parent).roundToInt()
            }
        }
    }

}