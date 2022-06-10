package de.julianostarek.flow.ui.main.trips.recents

import android.view.ViewGroup
import de.julianostarek.flow.persist.model.RouteEntity
import de.julianostarek.flow.ui.common.adapter.base.BaseListAdapter
import de.julianostarek.flow.ui.common.diff.RouteDiffItemCallback

class RecentRoutesAdapter(private val listener: Listener) :
    BaseListAdapter<RouteEntity, RecentRouteViewHolder>(
        RouteDiffItemCallback
    ), RecentRouteViewHolder.Observer {

    interface Listener {
        fun onRouteClicked(route: RouteEntity)

        fun onRouteFavoriteClicked(route: RouteEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentRouteViewHolder {
        return RecentRouteViewHolder(parent)
    }

    override fun onRouteClicked(viewHolder: RecentRouteViewHolder) {
        listener.onRouteClicked(viewHolder.data!!)
    }

    override fun onRouteFavoriteClicked(viewHolder: RecentRouteViewHolder) {
        listener.onRouteFavoriteClicked(viewHolder.data!!)
    }

}