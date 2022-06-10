package de.julianostarek.flow.ui.main.stops.stationboard.merged

import androidx.recyclerview.widget.DiffUtil
import de.julianostarek.flow.ui.common.diff.JourneyDiffItemCallback
import de.julianostarek.flow.ui.common.diff.LineDiffItemCallback
import de.julianostarek.flow.ui.common.diff.location.DirectionDiffItemCallback

object MergedJourneyDiffItemCallback : DiffUtil.ItemCallback<MergedJourney>() {

    override fun areItemsTheSame(
        oldItem: MergedJourney,
        newItem: MergedJourney
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MergedJourney,
        newItem: MergedJourney
    ): Boolean {
        return oldItem.journeys.size == newItem.journeys.size
                && LineDiffItemCallback.areContentsTheSame(oldItem.line, newItem.line)
                && DirectionDiffItemCallback.areContentsTheSame(
            oldItem.direction,
            newItem.direction
        )
                && oldItem.journeys.mapIndexed { index, journey ->
            journey to newItem.journeys[index]
        }.all { (first, second) ->
            JourneyDiffItemCallback.areContentsTheSame(first, second)
        }
    }

    override fun getChangePayload(
        oldItem: MergedJourney,
        newItem: MergedJourney
    ): Any? {
        return LineDiffItemCallback.getChangePayload(
            oldItem.line,
            newItem.line
        ) ?: DirectionDiffItemCallback.getChangePayload(
            oldItem.direction,
            newItem.direction
        )
    }

}