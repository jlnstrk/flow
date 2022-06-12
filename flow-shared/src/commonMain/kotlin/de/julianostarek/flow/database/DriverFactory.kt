package de.julianostarek.flow.database

import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): FlowDatabase {
    val driver = driverFactory.createDriver()
    return FlowDatabase(
        driver = driver,
        locationAdapter = Location.Adapter(
            typeAdapter = EnumColumnAdapter()
        )
    )
}