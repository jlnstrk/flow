package de.julianostarek.flow.ui.main.map

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import de.julianostarek.flow.ui.common.map.BackdropMapView
import de.julianostarek.flow.viewmodel.SequenceViewModel
import de.jlnstrk.transit.common.extensions.offers
import de.jlnstrk.transit.common.extensions.require
import de.jlnstrk.transit.common.service.NetworkGeometryResult
import de.jlnstrk.transit.common.service.NetworkGeometryService

class MapControlViewModel(application: Application) : SequenceViewModel(application) {
    val visibility: LiveData<Boolean> = MutableLiveData()
    val focusMode: LiveData<BackdropMapView.FocusMode> = MutableLiveData()

    val geometries: LiveData<NetworkGeometryResult> = liveData {
        if (requireProvider().offers<NetworkGeometryService>()) {
            emit(requireProvider().require<NetworkGeometryService>()
                .networkGeometry())
        }
    }

    fun setVisibility(visibility: Boolean) {
        (this.visibility as MutableLiveData).value = visibility
    }

    fun setFocusMode(focusMode: BackdropMapView.FocusMode) {
        (this.focusMode as MutableLiveData).value = focusMode
    }

}