package de.julianostarek.flow.ui.common.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.progressindicator.LinearProgressIndicator
import de.julianostarek.flow.R
import de.julianostarek.flow.util.recyclerview.SuppressDecor

class LoadStateIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), SuppressDecor {
    var state: State = State.Hidden
        private set
    private val progressIndicator: LinearProgressIndicator
    private val caption: TextView

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER_VERTICAL
        clipChildren = false
        setWillNotDraw(true)
        LayoutInflater.from(context).inflate(R.layout.lsi_layout, this)
        progressIndicator = findViewById(R.id.lsi_progress)
        caption = findViewById(R.id.lsi_caption)

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.LoadStateIndicator,
            defStyleAttr,
            0
        )
        if (typedArray.hasValue(R.styleable.LoadStateIndicator_captionColor)) {
            val textColor = typedArray.getColor(
                R.styleable.LoadStateIndicator_captionColor,
                caption.currentTextColor
            )
            caption.setTextColor(textColor)
            TextViewCompat.setCompoundDrawableTintList(caption, ColorStateList.valueOf(textColor))
        }
        if (typedArray.hasValue(R.styleable.LoadStateIndicator_indeterminateColor)) {
            val tintList = ColorStateList.valueOf(
                typedArray.getColor(
                    R.styleable.LoadStateIndicator_indeterminateColor,
                    progressIndicator.indeterminateTintList?.defaultColor ?: Color.BLACK
                )
            )
            progressIndicator.indeterminateTintList = tintList
        }
        typedArray.recycle()

        progressIndicator.isIndeterminate = true
        caption.visibility = GONE
    }

    fun moveToLoadingState(
        iconRes: Int = 0,
        captionRes: Int = 0,
        loading: State.Loading = State.Loading(iconRes, captionRes)
    ) {
        if (state !is State.Loading) {
            TransitionManager.beginDelayedTransition(this, newTransition())
            state = loading
        }
        caption.setText(loading.captionRes)
        caption.setCompoundDrawablesRelativeWithIntrinsicBounds(loading.iconRes, 0, 0, 0)
        progressIndicator.show()
        caption.visibility = VISIBLE
    }

    fun moveToErrorState(
        iconRes: Int = 0,
        captionRes: Int = 0,
        error: State.Error = State.Error(iconRes, captionRes)
    ) {
        if (state !is State.Error) {
            TransitionManager.beginDelayedTransition(this, newTransition())
            state = error
        }
        caption.setText(error.captionRes)
        caption.setCompoundDrawablesRelativeWithIntrinsicBounds(error.iconRes, 0, 0, 0)
        progressIndicator.hide()
        caption.visibility = VISIBLE
    }

    fun moveToHiddenState() {
        if (state !== State.Hidden) {
            TransitionManager.beginDelayedTransition(this, newTransition())
            state = State.Hidden
        }
        progressIndicator.hide()
        caption.visibility = GONE
    }

    fun moveToState(state: State) = when (state) {
        State.Hidden -> moveToHiddenState()
        is State.Loading -> moveToLoadingState(loading = state)
        is State.Error -> moveToErrorState(error = state)
    }

    sealed class State {

        object Hidden : State()

        data class Loading(
            val iconRes: Int = 0,
            val captionRes: Int = 0
        ) : State()

        data class Error(
            val iconRes: Int = R.drawable.ic_state_error_40dp,
            val captionRes: Int = 0
        ) : State()

    }

    private fun newTransition(): Transition = AutoTransition().apply {
        excludeTarget(progressIndicator, true)
    }

}