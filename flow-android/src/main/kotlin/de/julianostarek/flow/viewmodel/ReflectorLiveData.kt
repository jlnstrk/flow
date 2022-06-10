package de.julianostarek.flow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class ReflectorLiveData(private val holdValue: Boolean = false) : MediatorLiveData<Boolean>() {
    var isAbsorbing: Boolean = true

    fun observeAll(vararg sources: LiveData<*>) {
        sources.forEach { source ->
            addSource(source) {
                // To allow for ObserverLiveDatas to be chained together,
                // the value reset parse one cannot be considered an actual 'change'
                if (source !is ReflectorLiveData || (it as? Boolean) == true) {
                    if (!isAbsorbing) {
                        this@ReflectorLiveData.value = true
                    }
                }
                if (!holdValue) {
                    this@ReflectorLiveData.value = false
                }
            }
        }
    }
}