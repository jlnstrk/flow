package de.julianostarek.flow.util.datetime

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

inline fun ZoneId.currentOffset(): ZoneOffset = rules.getOffset(Instant.now())

inline fun ZoneOffset.totalMillis(): Long = totalSeconds * 1000L