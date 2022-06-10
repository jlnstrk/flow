package de.julianostarek.flow.ui.common.decor

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.julianostarek.flow.util.graphics.dp
import kotlin.math.roundToInt

open class VerticalGridSpacingItemDecoration(
    context: Context,
    private val spanCount: Int = 1,
    private val spacing: Int = 8F.dp(context).roundToInt(),
    private val horizontalEdge: Boolean = true,
    private val verticalEdge: Boolean = true,
    private val missingEdge: Boolean = true,
    private val isTopBased: Boolean = false,
    var startAtPosition: Int = 0
) : PositionItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State, position: Int
    ) {
        if (position < startAtPosition) return
        val decorationPosition = (position.takeIf { it != RecyclerView.NO_POSITION }
            ?: (parent.getChildViewHolder(view).oldPosition)) - startAtPosition
        val column = decorationPosition % spanCount
        if (horizontalEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
        }
        if (isTopBased || (verticalEdge && missingEdge && (!isTopBased && decorationPosition < spanCount))) {
            outRect.top = spacing
        }
        if (!isTopBased || (verticalEdge && missingEdge && (position == parent.adapter!!.itemCount - 1))) {
            outRect.bottom = spacing
        }
    }
}