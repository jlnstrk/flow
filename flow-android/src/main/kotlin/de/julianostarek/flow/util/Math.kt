package de.julianostarek.flow.util

/**
 * Redeclare since [Math.floorMod] is 1.8+
 */

infix fun Long.floorMod(y: Long): Long {
    return this - floorDiv(y) * y
}

infix fun Long.floorDiv(y: Long): Long {
    var r: Long = this / y
    // if the signs are different and modulo not zero, round down
    if (this xor y < 0 && r * y != this) {
        r--
    }
    return r
}