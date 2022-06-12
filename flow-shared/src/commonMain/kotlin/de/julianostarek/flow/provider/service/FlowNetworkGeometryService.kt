package de.julianostarek.flow.provider.service

import de.jlnstrk.transit.common.model.*
import de.jlnstrk.transit.common.response.NetworkGeometryData
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.NetworkGeometryResult
import de.jlnstrk.transit.common.service.NetworkGeometryService
import de.julianostarek.flow.profile.FlowProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FlowNetworkGeometryService(
    private val profile: FlowProfile
) : NetworkGeometryService {

    override suspend fun networkGeometry(): NetworkGeometryResult {
        try {
            val queryBuilder = Firebase.firestore.collection("networks")
                .document(profile.name.lowercase())
                .collection("subway")
                .get()
            return ServiceResult.success(
                NetworkGeometryData(
                    DataHeader(),
                    mapOf(TransportMode.SUBWAY to queryBuilder.documents.map {
                        LineGeometry(
                            it.get("lines"),
                            Polyline((it.get<List<String>>("coordinates")).map {
                                // TODO
                                Coordinates(
                                    0.0, //it.latitude,
                                    0.0 //it.longitude
                                )
                            })
                        )
                    })
                )
            )
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            return ServiceResult.noResult()
        }
    }
}