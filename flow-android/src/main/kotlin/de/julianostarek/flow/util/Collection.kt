package de.julianostarek.flow.util

inline fun <T> Iterable<T>.allIndexed(predicate: (Int, T) -> Boolean): Boolean {
    if (this is Collection && isEmpty()) return true
    for ((index, element) in this.withIndex()) if (!predicate(index, element)) return false
    return true
}