package de.julianostarek.flow.util.type

import android.content.Context
import de.julianostarek.flow.util.res.resolveResId

inline fun Context.headline6AppearanceResId(): Int =
    resolveResId(com.google.android.material.R.attr.textAppearanceHeadline6)[0]

inline fun Context.subtitle1AppearanceResId(): Int =
    resolveResId(com.google.android.material.R.attr.textAppearanceSubtitle1)[0]

inline fun Context.subtitle2AppearanceResId(): Int =
    resolveResId(com.google.android.material.R.attr.textAppearanceSubtitle2)[0]

inline fun Context.captionAppearanceResId(): Int =
    resolveResId(com.google.android.material.R.attr.textAppearanceCaption)[0]