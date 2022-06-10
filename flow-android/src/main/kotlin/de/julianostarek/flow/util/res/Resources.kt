package de.julianostarek.flow.util.res

import android.content.res.Resources
import de.julianostarek.flow.R

inline val Resources.colorOnTime: Int get() = getColor(R.color.realtime_on_time, null)

inline val Resources.colorCancelled: Int get() = getColor(R.color.realtime_delayed, null)

inline val Resources.colorDelayed: Int get() = getColor(R.color.realtime_delayed, null)

inline val Resources.colorEarly: Int get() = getColor(R.color.realtime_early, null)