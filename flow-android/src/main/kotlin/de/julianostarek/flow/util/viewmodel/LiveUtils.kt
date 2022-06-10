package de.julianostarek.flow.util.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import de.julianostarek.flow.viewmodel.LoadingLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun CoroutineScope.launchWithIndicator(
    state: LoadingLiveData,
    crossinline block: suspend () -> Unit
) {
    state.pushLoading()
    launch {
        block()
    }.invokeOnCompletion {
        state.popLoading()
    }
}

inline fun <reified R, S> fold(
    vararg sources: LiveData<R>,
    crossinline fold: (Array<R?>) -> S?
): MediatorLiveData<S> {
    val mediator = MediatorLiveData<S>()
    val values = arrayOfNulls<R?>(sources.size)
    val updateAction = { _: R ->
        sources.forEachIndexed { index, liveData ->
            values[index] = liveData.value
        }
        mediator.value = fold(values)
    }
    sources.forEach {
        mediator.addSource(it, updateAction)
    }
    return mediator
}
