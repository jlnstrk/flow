package de.julianostarek.flow.ui.common.span

import android.content.Context
import android.graphics.drawable.Drawable
import de.jlnstrk.transit.common.model.ProductClass
import de.julianostarek.flow.ui.common.span.base.ImageSpanCompat
import de.julianostarek.flow.util.context.styles
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.graphics.setBoundsScaledWidth
import de.julianostarek.flow.util.iconResId
import kotlin.math.roundToInt

class ProductSpan(context: Context, product: ProductClass) : ImageSpanCompat(
    context,
    context.styles.resolveProductStyle(product).iconResId(context),
    verticalAlignment = ALIGN_CENTER
) {
    private val height: Int = 24F.dp(context).roundToInt()

    override fun getDrawable(): Drawable {
        val drawable = super.getDrawable()
        drawable.setBoundsScaledWidth(height)
        return drawable
    }

}