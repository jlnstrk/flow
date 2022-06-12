package de.julianostarek.flow

import de.julianostarek.flow.database.DriverFactory

fun initKoin() {
    val driverFactory = DriverFactory()
    initKoin(driverFactory)
}