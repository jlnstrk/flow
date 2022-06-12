package de.julianostarek.flow.provider.service

import de.jlnstrk.transit.common.model.DataHeader
import de.jlnstrk.transit.common.model.NetworkMap
import de.jlnstrk.transit.common.model.ProductClass
import de.jlnstrk.transit.common.model.TransportMode
import de.jlnstrk.transit.common.response.NetworkMapsData
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.NetworkMapsService
import de.julianostarek.flow.profile.FlowProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.orderBy
import dev.gitlive.firebase.firestore.where
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FlowNetworkMapsService(
    private val profile: FlowProfile
) : NetworkMapsService {
    override val supportsFilterProducts: Boolean get() = true

    override suspend fun networkMaps(
        name: String?,
        filterProducts: Set<ProductClass>?
    ): ServiceResult<NetworkMapsData, NetworkMapsService.Error> {
        try {
            var queryBuilder = Firebase.firestore.collection("network-maps")
                .where("profile", profile.name.lowercase())
                .orderBy("weight", Direction.ASCENDING)
            if (filterProducts != null) {
                queryBuilder = queryBuilder.where(
                    "products",
                    arrayContainsAny = filterProducts
                        .map { PRODUCTS[it.mode] }
                        .distinct() as List<Any>
                )
            }
            val result = queryBuilder.get()
            val maps = result.documents.map { document ->
                NetworkMap(
                    id = document.id,
                    title = document.get("title"),
                    place = document.get("place"),
                    author = document.get("author"),
                    thumbnailUrl = document.get("thumbnailUrl"),
                    fileUrl = document.get("fileUrl"),
                    published = document.get<Double?>("published")
                        ?.let { Instant.fromEpochMilliseconds(it.toLong()) },
                    modified = document.get<Double?>("modified")
                        ?.let { Instant.fromEpochMilliseconds(it.toLong()) },
                    // TODO
                    validFrom = document.get<String?>("validFrom")
                        ?.let {
                            println(it)
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        },
                    // TODO
                    validTo = document.get<String?>("validTo")
                        ?.let {
                            println(it)
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                        },
                    products = (document.get("products") as? List<String>?)
                        ?.mapNotNull(PRODUCTS::get)
                        ?.flatMap { mode -> listOf(mode) + profile.products.filter { it.mode == mode } }
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
        private val PRODUCTS: BiMap<String, TransportMode> = BiMap()

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