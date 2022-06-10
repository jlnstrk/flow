package de.julianostarek.flow.util.recyclerview

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

inline fun <reified A : RecyclerView.Adapter<*>> RecyclerView.Adapter<*>.parentAdapterOfType(): A? {
    return parentAdapterOfType(A::class)
}

fun <A : RecyclerView.Adapter<*>> RecyclerView.Adapter<*>.parentAdapterOfType(adapterType: KClass<A>): A? {
    if (adapterType.isInstance(this)) {
        return this as A
    }
    if (this is ConcatAdapter) {
        for (child in adapters) {
            val nested = child.parentAdapterOfType(adapterType)
            if (nested != null) {
                return nested
            }
        }
    }
    return null
}