package de.julianostarek.flow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ActivityLiveData<T> : MutableLiveData<T>() {
    val isActive: LiveData<Boolean> = MutableLiveData()
    private var onActive: (() -> Unit)? = null
    private var onInactive: (() -> Unit)? = null

    override fun onActive() {
        super.onActive()
        (isActive as MutableLiveData).value = true
        onActive?.invoke()
    }

    override fun onInactive() {
        super.onInactive()
        (isActive as MutableLiveData).value = false
        onInactive?.invoke()
    }

    fun doOnActive(block: () -> Unit): ActivityLiveData<T> {
        this.onActive = block
        return this
    }

    fun doOnInactive(block: () -> Unit): ActivityLiveData<T> {
        this.onInactive = block
        return this
    }
}