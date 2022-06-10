package de.julianostarek.flow.provider

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import de.julianostarek.flow.provider.service.SequenceNetworkGeometryService
import de.julianostarek.flow.provider.service.SequenceNetworkMapsService
import de.jlnstrk.transit.common.Provider
import de.jlnstrk.transit.common.service.NetworkGeometryService
import de.jlnstrk.transit.common.service.NetworkMapsService
import kotlinx.datetime.TimeZone

open class SequenceProvider(
    private val profileName: String,
    private val firebaseAppName: String = FirebaseApp.DEFAULT_APP_NAME
) : Provider.Implementation() {
    override val timezone: TimeZone get() = TimeZone.of("Europe/Berlin")

    init {
        val firebaseApp: FirebaseApp by lazy { FirebaseApp.getInstance(firebaseAppName) }
        registerService<NetworkMapsService> {
            val firestore = FirebaseFirestore.getInstance(firebaseApp)
            SequenceNetworkMapsService(firestore, profileName)
        }
        registerService<NetworkGeometryService> {
            val firestore = FirebaseFirestore.getInstance(firebaseApp)
            SequenceNetworkGeometryService(firestore, profileName)
        }
    }
}