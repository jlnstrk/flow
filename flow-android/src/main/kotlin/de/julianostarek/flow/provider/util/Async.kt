package de.julianostarek.flow.provider.util

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener {
            continuation.resume(it)
        }
        addOnFailureListener {
            continuation.resumeWithException(it)
        }
    }
}