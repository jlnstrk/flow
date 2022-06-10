package de.julianostarek.flow.ui.common.decor

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt

class HorizontalLinearSpacingItemDecoration(
    context: Context,
    private val spacing: Int = 8F.dp(context).roundToInt(),
    private val includeEdges: Boolean = true
) : PositionItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State, position: Int
    ) {
        if (position == 0 && includeEdges) {
            outRect.left = spacing * 2
        }
        if (includeEdges && position == parent.adapter!!.itemCount - 1) {
            outRect.right = spacing * 2
        } else if (!includeEdges) {
            outRect.right = spacing
        }
    }
}