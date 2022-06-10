package de.julianostarek.flow.provider.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import de.julianostarek.flow.provider.util.await
import de.jlnstrk.transit.common.model.*
import de.jlnstrk.transit.common.response.NetworkGeometryData
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.NetworkGeometryResult
import de.jlnstrk.transit.common.service.NetworkGeometryService

class SequenceNetworkGeometryService(
    private val firestore: FirebaseFirestore,
    private val profileName: String
) : NetworkGeometryService {

    override suspend fun networkGeometry(): NetworkGeometryResult {
        try {
            val queryBuilder = firestore.collection("networks")
                .document(profileName)
                .collection("subway")
                .get()
                .await()
            return ServiceResult.success(
                NetworkGeometryData(
                    DataHeader(),
                    mapOf(TransportMode.SUBWAY to queryBuilder.documents.map {
                        LineGeometry(
                            it["lines"] as List<String>,
                            Polyline((it["coordinates"] as List<GeoPoint>).map {
                                Coordinates(
                                    it.latitude,
                                    it.longitude
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