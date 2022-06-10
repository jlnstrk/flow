package de.julianostarek.flow.viewmodel

import androidx.lifecycle.MutableLiveData

inline fun <T> MutableLiveData<T>.setAgain() {
    value = value
}

inline fun <T> MutableLiveData<T>.postAgain() = postValue(value)