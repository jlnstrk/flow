package de.julianostarek.flow.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveEvent

class TimeTickLiveEvent(context: Context) : LiveEvent.Simple() {
    private val context: Context = context.applicationContext
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            invoke()
        }
    }

    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(Intent.ACTION_TIME_TICK)
        context.registerReceiver(receiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(receiver)
    }
}