package de.julianostarek.flow

import de.julianostarek.flow.database.DriverFactory
import de.julianostarek.flow.database.createDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ksp.generated.module

@Module
@ComponentScan("de.julianostarek.flow")
class FlowModule

internal fun initKoin(
    driverFactory: DriverFactory
) {
    val flowModule = module {
        singleOf<DriverFactory> { driverFactory }
        singleOf(::createDatabase)
    }

    startKoin {
        printLogger()

        modules(FlowModule().module, flowModule)
    }
}