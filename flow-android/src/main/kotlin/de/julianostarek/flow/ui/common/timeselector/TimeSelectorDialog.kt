package de.julianostarek.flow.ui.common.timeselector

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.jlnstrk.transit.util.DateFormat
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.DialogTimePickerBinding
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import kotlinx.datetime.*


class TimeSelectorDialog : DialogFragment(), View.OnClickListener {
    private lateinit var viewBinding: DialogTimePickerBinding

    private lateinit var callback: Callback
    private var preselection: LocalDateTime? = null

    interface Callback {

        fun onTimeSelected(time: LocalDateTime)

        fun onNowSelected()

    }

    fun withCallback(callback: Callback): TimeSelectorDialog {
        this.callback = callback
        return this
    }

    fun preselection(preselection: LocalDateTime?): TimeSelectorDialog {
        this.preselection = preselection ?: Clock.System.now().toSystemLocal()
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val wrappedContext = ContextThemeWrapper(
            requireContext(),
            R.style.ThemeOverlay_Sequence_Dialog
        )
        viewBinding = DialogTimePickerBinding.inflate(layoutInflater.cloneInContext(wrappedContext))
        val alertDialog = MaterialAlertDialogBuilder(wrappedContext)
            .setView(viewBinding.root)
            .create()

        viewBinding.earlier.setOnClickListener(this)
        viewBinding.later.setOnClickListener(this)
        viewBinding.now.setOnClickListener(this)
        viewBinding.confirm.setOnClickListener(this)

        viewBinding.timePicker.setIs24HourView(true)
        preselection?.let {
            viewBinding.timePicker.currentHour = it.hour
            viewBinding.timePicker.currentMinute = it.minute
        }
        viewBinding.datePager.offscreenPageLimit = 0
        val adapter = DatePagerAdapter(preselection?.date!!)
        viewBinding.datePager.adapter = adapter
        viewBinding.datePager.currentItem = adapter.count / 2

        return alertDialog
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.earlier -> if (viewBinding.datePager.currentItem > 0) {
                viewBinding.datePager.setCurrentItem(viewBinding.datePager.currentItem - 1, true)
            }
            R.id.later -> if (viewBinding.datePager.currentItem
                < viewBinding.datePager.adapter!!.count - 1
            ) {
                viewBinding.datePager.setCurrentItem(viewBinding.datePager.currentItem + 1, true)
            }
            R.id.now -> {
                callback.onNowSelected()
                dismiss()
            }
            R.id.confirm -> {
                val date = (viewBinding.datePager.adapter as DatePagerAdapter)
                    .getTimeForPosition(viewBinding.datePager.currentItem)
                val time = LocalTime(
                    viewBinding.timePicker.currentHour,
                    viewBinding.timePicker.currentMinute
                )
                callback.onTimeSelected(LocalDateTime(date, time))
                dismiss()
            }
        }
    }

    inner class DatePagerAdapter(private val preselection: LocalDate) : PagerAdapter() {


        fun getTimeForPosition(position: Int): LocalDate {
            return preselection + DatePeriod(days = position - (count / 2))
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val textView = LayoutInflater.from(container.context)
                .inflate(R.layout.dialog_item_digits, container, false) as TextView
            textView.text = DATE_FORMAT.formatInstant(getTimeForPosition(position))
            container.addView(textView)
            return textView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return 365
        }
    }

    companion object {
        private val DATE_FORMAT = DateFormat("EEEE, dd. MMM yyyy")
    }
}