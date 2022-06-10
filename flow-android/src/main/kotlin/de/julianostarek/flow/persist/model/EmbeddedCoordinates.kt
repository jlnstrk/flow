package de.julianostarek.flow.persist.model

import androidx.annotation.NonNull

data class EmbeddedCoordinates(
    @NonNull val latitude: Double,
    @NonNull val longitude: Double
)