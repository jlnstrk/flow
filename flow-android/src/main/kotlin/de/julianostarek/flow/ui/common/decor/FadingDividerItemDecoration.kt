package com.google.android.material.divider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.R
import com.google.android.material.internal.ThemeEnforcement
import com.google.android.material.resources.MaterialResources
import de.julianostarek.flow.util.graphics.intAlpha
import de.julianostarek.flow.util.recyclerview.SuppressDecor

/**
 * MaterialDividerItemDecoration is a [RecyclerView.ItemDecoration], similar to a [ ], that can be used as a divider between items of
 * a [LinearLayoutManager]. It supports both [.HORIZONTAL] and [.VERTICAL]
 * orientations.
 *
 * <pre>
 * dividerItemDecoration = new MaterialDividerItemDecoration(recyclerView.getContext(),
 * layoutManager.getOrientation());
 * recyclerView.addItemDecoration(dividerItemDecoration);
</pre> *
 */
class FadingDividerItemDecoration(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int, orientation: Int
) : ItemDecoration() {
    private var dividerDrawable: Drawable

    constructor(context: Context, dividerInsetStart: Int, dividerInsetEnd: Int) : this(context, VERTICAL) {
        this.dividerInsetStart = dividerInsetStart
        this.dividerInsetEnd = dividerInsetEnd
    }

    /**
     * The thickness set on the divider.
     *
     * @see .setDividerThickness
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerThickness
     */
    /**
     * Sets the thickness of the divider.
     *
     * @param thickness The thickness value to be set.
     * @see .getDividerThickness
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerThickness
     */
    @get:Px
    var dividerThickness: Int

    @ColorInt
    private var color: Int
    private var orientation = 0
    /**
     * The divider's start inset.
     *
     * @see .setDividerInsetStart
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerInsetStart
     */
    /**
     * Sets the start inset of the divider.
     *
     * @param insetStart The start inset to be set.
     * @see .getDividerInsetStart
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerInsetStart
     */
    @get:Px
    var dividerInsetStart: Int
    /**
     * The divider's end inset.
     *
     * @see .setDividerInsetEnd
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerInsetEnd
     */
    /**
     * Sets the end inset of the divider.
     *
     * @param insetEnd The end inset to be set.
     * @see .getDividerInsetEnd
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerInsetEnd
     */
    @get:Px
    var dividerInsetEnd: Int
    private val tempRect = Rect()

    constructor(context: Context, orientation: Int) : this(context, null, orientation)
    constructor(
        context: Context, attrs: AttributeSet?, orientation: Int
    ) : this(context, attrs, R.attr.materialDividerStyle, orientation)

    /**
     * Sets the orientation for this divider. This should be called if [ ] changes orientation.
     *
     *
     * A [.HORIZONTAL] orientation will draw a vertical divider, and a [.VERTICAL]
     * orientation a horizontal divider.
     *
     * @param orientation The orientation of the [RecyclerView] this divider is associated with:
     * [.HORIZONTAL] or [.VERTICAL]
     */
    fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL && orientation != VERTICAL)) { "Invalid orientation: $orientation. It should be either HORIZONTAL or VERTICAL" }
        this.orientation = orientation
    }

    fun getOrientation(): Int {
        return orientation
    }

    /**
     * Sets the thickness of the divider.
     *
     * @param thicknessId The id of the thickness dimension resource to be set.
     * @see .getDividerThickness
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerThickness
     */
    fun setDividerThicknessResource(context: Context, @DimenRes thicknessId: Int) {
        dividerThickness = context.resources.getDimensionPixelSize(thicknessId)
    }

    /**
     * Sets the color of the divider.
     *
     * @param colorId The id of the color resource to be set.
     * @see .getDividerColor
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerColor
     */
    fun setDividerColorResource(context: Context, @ColorRes colorId: Int) {
        dividerColor = ContextCompat.getColor(context, colorId)
    }
    /**
     * The divider color.
     *
     * @see .setDividerColor
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerColor
     */
    /**
     * Sets the color of the divider.
     *
     * @param color The color to be set.
     * @see .getDividerColor
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerColor
     */
    @get:ColorInt
    var dividerColor: Int
        get() = color
        set(color) {
            this.color = color
            dividerDrawable = DrawableCompat.wrap(dividerDrawable)
            DrawableCompat.setTint(dividerDrawable, color)
        }

    /**
     * Sets the start inset of the divider.
     *
     * @param insetStartId The id of the inset dimension resource to be set.
     * @see .getDividerInsetStart
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerInsetStart
     */
    fun setDividerInsetStartResource(context: Context, @DimenRes insetStartId: Int) {
        dividerInsetStart = context.resources.getDimensionPixelOffset(insetStartId)
    }

    /**
     * Sets the end inset of the divider.
     *
     * @param insetEndId The id of the inset dimension resource to be set.
     * @see .getDividerInsetEnd
     * @attr ref com.google.android.material.R.styleable#MaterialDivider_dividerInsetEnd
     */
    fun setDividerInsetEndResource(context: Context, @DimenRes insetEndId: Int) {
        dividerInsetEnd = context.resources.getDimensionPixelOffset(insetEndId)
    }

    override fun onDraw(
        canvas: Canvas, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (parent.layoutManager == null) {
            return
        }
        if (orientation == VERTICAL) {
            drawForVerticalOrientation(canvas, parent)
        } else {
            drawForHorizontalOrientation(canvas, parent)
        }
    }

    /**
     * Draws a divider for the vertical orientation of the recycler view. The divider itself will be
     * horizontal.
     */
    private fun drawForVerticalOrientation(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        var left: Int
        var right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right, parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val isRtl = ViewCompat.getLayoutDirection(parent) == ViewCompat.LAYOUT_DIRECTION_RTL
        left += if (isRtl) dividerInsetEnd else dividerInsetStart
        right -= if (isRtl) dividerInsetStart else dividerInsetEnd
        for (child in parent.children) {
            if (child is SuppressDecor)
                continue
            parent.getDecoratedBoundsWithMargins(child, tempRect)
            // Take into consideration any translationY added to the view.
            val bottom = tempRect.bottom + Math.round(child.translationY)
            val top = bottom - dividerDrawable.intrinsicHeight - dividerThickness
            dividerDrawable.alpha = child.alpha.intAlpha()
            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(canvas)
        }
        canvas.restore()
    }

    /**
     * Draws a divider for the horizontal orientation of the recycler view. The divider itself will be
     * vertical.
     */
    private fun drawForHorizontalOrientation(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        var top: Int
        var bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top, parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        top += dividerInsetStart
        bottom -= dividerInsetEnd
        for (child in parent.children) {
            if (child is SuppressDecor)
                continue
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, tempRect)
            // Take into consideration any translationY added to the view.
            val right = tempRect.right + Math.round(child.translationX)
            val left = right - dividerDrawable.intrinsicWidth - dividerThickness
            dividerDrawable.alpha = child.alpha.intAlpha()
            dividerDrawable.setBounds(left, top, right, bottom)
            dividerDrawable.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect[0, 0, 0] = 0
        if (orientation == VERTICAL) {
            outRect.bottom = dividerDrawable.intrinsicHeight + dividerThickness
        } else {
            outRect.right = dividerDrawable.intrinsicWidth + dividerThickness
        }
    }

    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
        private val DEF_STYLE_RES = R.style.Widget_MaterialComponents_MaterialDivider
    }

    init {
        val attributes = ThemeEnforcement.obtainStyledAttributes(
            context,
            attrs,
            R.styleable.MaterialDivider,
            defStyleAttr,
            DEF_STYLE_RES
        )
        color = MaterialResources.getColorStateList(
            context, attributes, R.styleable.MaterialDivider_dividerColor
        )!!.defaultColor
        dividerThickness = attributes.getDimensionPixelSize(
            R.styleable.MaterialDivider_dividerThickness,
            context.resources.getDimensionPixelSize(R.dimen.material_divider_thickness)
        )
        dividerInsetStart =
            attributes.getDimensionPixelOffset(R.styleable.MaterialDivider_dividerInsetStart, 0)
        dividerInsetEnd =
            attributes.getDimensionPixelOffset(R.styleable.MaterialDivider_dividerInsetEnd, 0)
        attributes.recycle()
        dividerDrawable = ShapeDrawable()
        dividerColor = color
        setOrientation(orientation)
    }
}