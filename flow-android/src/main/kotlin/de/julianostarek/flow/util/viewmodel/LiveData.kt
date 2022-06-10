package de.julianostarek.flow.util.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import de.julianostarek.flow.ui.common.time.util.toSystemLocal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

typealias LivePagedList<T> = LiveData<PagedList<T>>

inline fun LiveData<LocalDateTime?>.orNow(): LocalDateTime = value ?: Clock.System.now().toSystemLocal()