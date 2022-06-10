package de.julianostarek.flow.util.context

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import de.julianostarek.flow.FlowApp
import de.julianostarek.flow.profile.FlowProfile
import de.jlnstrk.transit.common.Provider
import de.julianostarek.flow.profile.StyledProfile

inline val Context.profile: FlowProfile
    get() = (applicationContext as FlowApp).networkProfile

inline val Context.provider: Provider
    get() = (applicationContext as FlowApp).networkProfile.provider

inline val Context.styles: StyledProfile
    get() = (applicationContext as FlowApp).networkProfile.styles

fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED