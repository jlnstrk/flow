package de.julianostarek.flow.provider.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.julianostarek.flow.provider.util.BiMap
import de.julianostarek.flow.provider.util.await
import de.jlnstrk.transit.common.model.DataHeader
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.model.NetworkMap
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.response.NetworkMapsData
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.NetworkMapsService
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class SequenceNetworkMapsService(
    private val firestore: FirebaseFirestore,
    private val profileName: String
) : NetworkMapsService {
    override val supportsFilterProducts: Boolean get() = true

    override suspend fun networkMaps(
        name: String?,
        filterProducts: Set<ProductClass>?
    ): ServiceResult<NetworkMapsData, NetworkMapsService.Error> {
        try {
            var queryBuilder = firestore.collection("network-maps")
                .whereEqualTo("profile", profileName)
                .orderBy("weight", Query.Direction.ASCENDING)
            if (filterProducts != null) {
                queryBuilder = queryBuilder.whereArrayContainsAny(
                    "products",
                    filterProducts.map(PRODUCTS::get)
                )
            }
            val result = queryBuilder.get()
                .await()
            val maps = result.documents.map { document ->
                NetworkMap(
                    id = document.id,
                    title = document.getString("title")!!,
                    place = document.getString("place"),
                    author = document.getString("author")!!,
                    thumbnailUrl = document.getString("thumbnailUrl")!!,
                    fileUrl = document.getString("fileUrl"),
                    published = document.getDate("published")
                        ?.toInstant()
                        ?.let { Instant.fromEpochMilliseconds(it.toEpochMilli()) },
                    modified = document.getDate("modified")
                        ?.toInstant()
                        ?.let {Instant.fromEpochMilliseconds(it.toEpochMilli()) },
                    validFrom = document.getDate("validFrom")
                        ?.let { LocalDate(it.year, it.month, it.day) },
                    validTo = document.getDate("validTo")
                        ?.let { LocalDate(it.year, it.month, it.day) },
                    products = (document.get("products") as? List<String>?)
                        ?.mapNotNull(PRODUCTS::get)
                        ?.toSet()
                )
            }
            val response = NetworkMapsData(DataHeader(), maps)
            return ServiceResult.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            return ServiceResult.failure(e, message = e.message)
        }
    }

    companion object {
        private val PRODUCTS: BiMap<String, ProductClass> = BiMap()

        init {
            PRODUCTS["regional_express"] = TransportMode.TRAIN
            PRODUCTS["regional_train"] = TransportMode.TRAIN
            PRODUCTS["suburban_train"] = TransportMode.TRAIN
            PRODUCTS["subway"] = TransportMode.SUBWAY
            PRODUCTS["tram"] = TransportMode.LIGHT_RAIL
            PRODUCTS["express_bus"] = TransportMode.BUS
            PRODUCTS["metro_bus"] = TransportMode.BUS
            PRODUCTS["regional_bus"] = TransportMode.BUS
            PRODUCTS["bus"] = TransportMode.BUS
            PRODUCTS["ferry"] = TransportMode.WATERCRAFT
        }

    }

}