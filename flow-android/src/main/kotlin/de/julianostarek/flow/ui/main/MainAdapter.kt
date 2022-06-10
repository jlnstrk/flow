package de.julianostarek.flow.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.julianostarek.flow.ui.main.info.InfoContentFragment
import de.julianostarek.flow.ui.main.network.NetworkContentFragment
import de.julianostarek.flow.ui.main.stops.StopsContentFragment
import de.julianostarek.flow.ui.main.trips.TripsContentFragment

class MainAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StopsContentFragment()
            1 -> TripsContentFragment()
            2 -> InfoContentFragment()
            3 -> NetworkContentFragment()
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = 4

}