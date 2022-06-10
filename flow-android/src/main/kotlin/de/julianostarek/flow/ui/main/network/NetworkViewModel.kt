package de.julianostarek.flow.ui.main.network

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import de.julianostarek.flow.viewmodel.LoadingLiveData
import de.julianostarek.flow.viewmodel.SequenceViewModel
import de.jlnstrk.transit.common.extensions.optional
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.NetworkMapsResult
import de.jlnstrk.transit.common.service.NetworkMapsService

class NetworkViewModel(application: Application) : SequenceViewModel(application) {
    val networkMapsLoading = LoadingLiveData()
    val networkMapsRefreshing = LoadingLiveData()

    val networkMaps: LiveData<NetworkMapsResult> = profileConfig.switchMap { profileConfig ->
        liveData {
            networkMapsLoading.pushLoading()
            val filterProducts =
                if (productFilter.isDefaultFilter()) null else productFilter.value
            val response = profileConfig.provider.optional<NetworkMapsService>()
                ?.networkMaps(filterProducts = filterProducts)
            emit(response ?: ServiceResult.noResult())
            networkMapsRefreshing.popLoading()
        }
    }
}