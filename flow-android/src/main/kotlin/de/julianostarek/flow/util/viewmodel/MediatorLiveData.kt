package de.julianostarek.flow.util.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

inline fun <R, S> MediatorLiveData<R>.on(
    source: LiveData<S>,
    crossinline onChanged: (isActive: Boolean, ownValue: R?, sourceValue: S?) -> Unit
) {
    addSource(source) { sourceValue ->
        onChanged(hasActiveObservers(), value, sourceValue)
    }
}

inline fun <R> MediatorLiveData<R>.onAll(
    vararg sources: LiveData<*>,
    crossinline onChanged: () -> Unit
): MediatorLiveData<R> {
    for (source in sources) {
        addSource(source) { onChanged() }
    }
    return this
}