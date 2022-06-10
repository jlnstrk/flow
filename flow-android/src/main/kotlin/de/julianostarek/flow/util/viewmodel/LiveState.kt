package de.julianostarek.flow.util.viewmodel

sealed class LiveState<out D, out E> {
    data class Data<D>(val data: D) : LiveState<D, Nothing>()
    data class Error<E>(val error: E) : LiveState<Nothing, E>()
}