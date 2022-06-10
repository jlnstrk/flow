package de.julianostarek.flow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import de.jlnstrk.transit.common.Profile
import de.jlnstrk.transit.common.model.ProductClass

class ProductFilterLiveData(liveProfile: LiveData<Profile>) :
    MediatorLiveData<Set<ProductClass>>() {
    val isDefaultFilter: LiveData<Boolean> = Transformations.map(this) { isDefaultFilter() }
    private var defaultProducts: Set<ProductClass> = emptySet()

    fun isDefaultFilter(): Boolean = value == defaultProducts

    init {
        liveProfile.observeForever { profile ->
            defaultProducts = profile.filterConfig
                .filter(Profile.FilterEntry::isDefault)
                .flatMap { it.products.toList() }
                .toSet()
            if (this.value == null) {
                this.value = defaultProducts
            }
        }
    }
}