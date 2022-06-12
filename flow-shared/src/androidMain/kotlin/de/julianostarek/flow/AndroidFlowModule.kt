package de.julianostarek.flow

import android.content.Context
import de.julianostarek.flow.database.DriverFactory

fun initKoin(context: Context) {
    val driverFactory = DriverFactory(context)
    initKoin(driverFactory)
}