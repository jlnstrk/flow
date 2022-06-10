package de.julianostarek.flow.ui.main.info.linemessages

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import de.julianostarek.flow.databinding.ItemMessageBinding
import de.julianostarek.flow.ui.common.adapter.ProductGroupedLineListAdapter
import de.julianostarek.flow.ui.common.decor.VerticalGridSpacingItemDecoration
import de.julianostarek.flow.ui.common.viewholder.base.ObservableViewHolder
import de.julianostarek.flow.util.graphics.dp
import de.julianostarek.flow.util.res.colorCancelled
import de.julianostarek.flow.util.res.colorEarly
import de.julianostarek.flow.util.type.captionAppearanceResId
import de.jlnstrk.transit.common.model.Message
import kotlinx.datetime.Clock
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import kotlin.math.roundToInt

class LineMessageViewHolder(parent: ViewGroup) :
    ObservableViewHolder<Message, ItemMessageBinding, LineMessageViewHolder.Observer>(
        parent, ItemMessageBinding::inflate
    ), View.OnClickListener {
    private val lineGroupsAdapter: ProductGroupedLineListAdapter = ProductGroupedLineListAdapter()
    private val captionSpan =
        TextAppearanceSpan(itemView.context, itemView.context.captionAppearanceResId())

    fun interface Observer {
        fun onMessageClicked(message: Message)
    }

    init {
        itemView.setOnClickListener(this)
        viewBinding.products.layoutManager =
            LinearLayoutManager(viewBinding.products.context, LinearLayoutManager.VERTICAL, false)
        viewBinding.products.addItemDecoration(
            VerticalGridSpacingItemDecoration(
                viewBinding.products.context,
                horizontalEdge = false,
                verticalEdge = false,
                spacing = 8F.dp(this).roundToInt()
            )
        )
        viewBinding.products.adapter = lineGroupsAdapter
    }

    override fun bindTo(data: Message) {
        super.bindTo(data)
        lineGroupsAdapter.setData(data.affectedLines)
        val headBuilder = SpannableStringBuilder()

        val now = Clock.System.now()
        val hasBegun = data.validFrom!! < now
        val hasEnded = data.validUntil!! < now
        val isInEffect = hasBegun && !hasEnded

        val prefixBuilder = SpannableStringBuilder()
        prefixBuilder.append("", captionSpan, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        if (!hasBegun) {
            prefixBuilder.append(
                "Upcoming",
                ForegroundColorSpan(itemView.context.resources.colorEarly),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (isInEffect) {
            prefixBuilder.append(
                "In Effect",
                ForegroundColorSpan(itemView.context.resources.colorCancelled),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            prefixBuilder.append("Passed")
        }
        prefixBuilder.append(" • ")
        prefixBuilder.append(
            "${
                when (data.priority) {
                    Message.Priority.LOW -> "Low"
                    Message.Priority.MEDIUM -> "Normal"
                    Message.Priority.HIGH -> "High"
                    else -> null
                }
            } priority"
        )
        headBuilder.append(prefixBuilder, captionSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        headBuilder.append('\n')
        headBuilder.append()

        if (data.isHtmlHead) {
            val spanned =
                HtmlCompat.fromHtml(data.head, HtmlCompat.FROM_HTML_MODE_LEGACY)
            headBuilder.append(spanned)
        } else {
            headBuilder.append(data.head)
        }
        viewBinding.head.setText(headBuilder, TextView.BufferType.SPANNABLE)
    }

    override fun unbind() {
        super.unbind()
        lineGroupsAdapter.setData(null)
        viewBinding.head.text = null
    }

    override fun onClick(view: View) {
        observer?.onMessageClicked(data!!)
    }

    companion object {
        private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral('.')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('.')
            .appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
            .toFormatter()
        private val DATE_TIME_FORMAT: DateTimeFormatter = DateTimeFormatterBuilder()
            .append(DATE_FORMAT)
            .appendLiteral(", ")
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .toFormatter()
    }

}