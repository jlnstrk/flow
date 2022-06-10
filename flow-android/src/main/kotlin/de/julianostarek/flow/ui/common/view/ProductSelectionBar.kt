package de.julianostarek.flow.ui.common.view

import android.animation.*
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEach
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.ProductSet
import de.julianostarek.flow.R
import de.julianostarek.flow.profile.FlowProfile
import de.julianostarek.flow.ui.common.view.base.TintableFrameLayout
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.graphics.findBalancedIconScale
import de.julianostarek.flow.util.iconRawOrRegularResId
import kotlin.math.roundToInt

class ProductSelectionBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var profile: FlowProfile? = null
    private var callback: Callback? = null

    fun subscribe(callback: Callback) {
        this.callback = callback
    }

    fun interface Callback {

        fun onSelectionChanged(filter: Set<ProductClass>)

    }

    init {
        clipChildren = false
        showDividers = SHOW_DIVIDER_MIDDLE
        dividerDrawable = ContextCompat.getDrawable(context, R.drawable.product_flex_divider)
    }

    fun submitConfiguration(profile: FlowProfile) {
        if (this.profile != profile) {
            removeAllViews()
            this.profile = profile
            this.profile?.profile?.filterConfig
                ?.forEach { entry ->
                    val entryView = EntryView(context, entry = entry)
                    addView(entryView)
                }
        }
    }

    fun submitSelection(filter: Set<ProductClass>?) {
        outer@ for (child in children) {
            for (product in (child as EntryView).entry.products) {
                if (filter?.contains(product) != true) {
                    child.isActivated = false
                    continue@outer
                }
            }
            child.isActivated = true
        }
    }

    private fun compileAndDispatch() {
        val filter = ProductSet()
        forEach {
            if (it.isActivated) {
                val products = (it as EntryView).entry.products
                filter += products
            }
        }
        callback?.onSelectionChanged(filter)
    }

    inner class EntryView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
        val entry: Profile.FilterEntry
    ) : TintableFrameLayout(context, attrs, defStyleAttr), OnClickListener {

        init {
            setOnClickListener(this)
            setBackgroundResource(R.drawable.bg_leg_chip)
            layoutParams = LinearLayout.LayoutParams(
                0,
                32F.dp(this).roundToInt()
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
                .also { it.weight = 1F }
            val productStyle = profile!!.styles.resolveProductStyle(entry.styleOf)
            val backgroundColorActivated = productStyle.productColor
            addView(AppCompatImageView(context).also {
                // it.scaleType = ImageView.ScaleType.MATRIX
                val drawable = ContextCompat.getDrawable(
                    context,
                    productStyle.iconRawOrRegularResId(context)
                )!!
                val scale = drawable.findBalancedIconScale(context)
                it.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,//56F.dp(this).roundToInt(),
                    scale.height()//ViewGroup.LayoutParams.WRAP_CONTENT//,20F.dp(this).roundToInt()
                ).also { it.gravity = Gravity.CENTER }
                /*it.setPadding(
                    8F.dp(this).roundToInt(), 0,
                    8F.dp(this).roundToInt(), 0
                )*/
                it.setImageDrawable(drawable)
            })
            stateListAnimator = AnimatorInflater.loadStateListAnimator(
                context,
                R.animator.state_list_product_chip
            )
            backgroundTintList = ColorStateList.valueOf(backgroundColorActivated)
            stateListAnimator.addState(intArrayOf(android.R.attr.state_activated),
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofObject(
                            this@EntryView, "backgroundTint",
                            ArgbEvaluator(), backgroundColorActivated
                        ),
                        ObjectAnimator.ofObject(
                            this@EntryView, "alpha",
                            FloatEvaluator(), 1.0F
                        ),
                        ObjectAnimator.ofObject(
                            this@EntryView, "translationZ",
                            FloatEvaluator(), 2F.dp(this@EntryView)
                        )
                    )
                })
        }

        override fun onClick(view: View) {
            view.isActivated = !view.isActivated
            compileAndDispatch()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(48F.dp(this).roundToInt(), MeasureSpec.EXACTLY)
        )
    }

}