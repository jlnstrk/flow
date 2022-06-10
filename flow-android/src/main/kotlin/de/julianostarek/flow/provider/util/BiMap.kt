package de.julianostarek.flow.provider.util

class BiMap<A, B> {
    private val aToB: MutableMap<A, B> = HashMap()
    private val bToA: MutableMap<B, A> = HashMap()

    operator fun set(a: A, b: B) {
        aToB[a] = b
        bToA[b] = a
    }

    @JvmName("getA")
    operator fun get(a: A) = aToB[a]

    @JvmName("getB")
    operator fun get(b: B) = bToA[b]

}