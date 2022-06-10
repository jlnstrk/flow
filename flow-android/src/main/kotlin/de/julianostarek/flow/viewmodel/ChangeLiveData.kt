package de.julianostarek.flow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.julianostarek.flow.util.viewmodel.onAll

class ChangeLiveData : MediatorLiveData<Boolean>() {

    fun triggerOn(vararg liveData: LiveData<*>): ChangeLiveData {
        onAll(*liveData, onChanged = ::set)
        return this
    }

    fun reset() {
        value = false
    }

    fun set() {
        value = true
    }
}