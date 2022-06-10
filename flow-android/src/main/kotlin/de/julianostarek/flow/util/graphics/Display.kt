package de.julianostarek.flow.util.graphics

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

inline fun Float.dp(context: Context): Float {
    return context.resources.displayMetrics.density * this
}

inline fun Float.dp(view: View): Float {
    return view.resources.displayMetrics.density * this
}

inline fun Float.dp(fragment: Fragment): Float {
    return fragment.resources.displayMetrics.density * this
}

inline fun Float.dp(activity: Activity): Float {
    return activity.resources.displayMetrics.density * this
}

inline fun Float.dp(viewHolder: RecyclerView.ViewHolder): Float {
    return viewHolder.itemView.resources.displayMetrics.density * this
}