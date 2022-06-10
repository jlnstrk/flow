package de.julianostarek.flow.ui.main.trips.detail.viewholder

import android.view.ViewGroup
import de.julianostarek.flow.R
import de.julianostarek.flow.databinding.ItemLegIndividualBinding
import de.julianostarek.flow.ui.common.viewholder.base.BindingViewHolder
import de.jlnstrk.transit.common.model.Leg

class IndividualLegViewHolder(parent: ViewGroup) :
    BindingViewHolder<Leg.Individual, ItemLegIndividualBinding>(
        parent,
        ItemLegIndividualBinding::inflate
    ) {

    override fun bindTo(data: Leg.Individual) {
        viewBinding.text.setCompoundDrawablesRelativeWithIntrinsicBounds(
            when (data.type) {
                Leg.Individual.Type.WALK -> R.drawable.ic_individual_walk_24dp
                Leg.Individual.Type.BIKE -> R.drawable.ic_individual_bike_24dp
                Leg.Individual.Type.CAR -> R.drawable.ic_individual_car_24dp
                Leg.Individual.Type.TAXI -> R.drawable.ic_individual_taxi_24dp
            }, 0, 0, 0
        )
        val distance = data.gis.distance
        val formattedDistance =
            if (distance >= 100) {
                "%.1f km".format(distance.toFloat() / 1000)
            } else {
                "%d meters".format(distance)
            }
        val formatted =
            viewBinding.text.context.getString(
                when (data.type) {
                    Leg.Individual.Type.WALK -> R.string.mode_walk_km_min
                    Leg.Individual.Type.BIKE -> R.string.mode_bike_km_min
                    Leg.Individual.Type.CAR -> R.string.mode_car_km_min
                    Leg.Individual.Type.TAXI -> R.string.mode_taxi_km_min
                },
                formattedDistance,
                data.gis.duration.inWholeMinutes
            )
        viewBinding.text.text = formatted
    }

    override fun unbind() {
        super.unbind()
        viewBinding.text.setCompoundDrawablesRelative(null, null, null, null)
        viewBinding.text.text = null
    }
}