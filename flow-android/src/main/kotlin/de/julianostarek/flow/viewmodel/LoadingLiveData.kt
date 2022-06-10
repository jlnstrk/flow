package de.julianostarek.flow.viewmodel

import androidx.lifecycle.LiveData
import java.util.concurrent.atomic.AtomicInteger

class LoadingLiveData : LiveData<Boolean>(false) {
    private val count = AtomicInteger(0)

    fun pushLoading(post: Boolean = false) {
        if (count.getAndIncrement() == 0) {
            if (post) {
                postValue(true)
            } else {
                value = true
            }
        }
    }

    fun popLoading(post: Boolean = false) {
        if (count.decrementAndGet() == 0) {
            if (post) {
                postValue(false)
            } else {
                value = false
            }
        }
    }
}

inline val LoadingLiveData.isLoading: Boolean get() = value == true