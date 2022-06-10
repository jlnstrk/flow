package de.julianostarek.flow.util

import android.content.Context
import de.julianostarek.flow.profile.StyledProfile
import de.julianostarek.flow.util.res.resolveResId

fun String.resolveResourceId(context: Context): Int {
    println("resolving resid $this")
    return context.resources.getIdentifier(this, "drawable", context.packageName)
}

fun StyledProfile.LineStyle.featureIconResId(context: Context): Int? = featureIconRes?.resolveResourceId(context)

fun StyledProfile.ProductStyle.iconResId(context: Context): Int = iconRes.resolveResourceId(context)

fun StyledProfile.ProductStyle.iconRawResId(context: Context): Int? = iconRawRes?.resolveResourceId(context)

fun StyledProfile.ProductStyle.iconRawOrRegularResId(context: Context) = (iconRawRes ?: iconRes).resolveResourceId(context)