package de.julianostarek.flow

import de.julianostarek.flow.database.DatabaseRepository
import de.julianostarek.flow.profile.FlowProfile
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier

class FlowRepository(
    private val profile: FlowProfile
) : KoinComponent {
    val databaseRepository: DatabaseRepository by inject(qualifier = qualifier(profile))


    companion object {
        lateinit var profile: FlowProfile

        fun instance(): FlowRepository = FlowRepository(profile)
    }
}