package com.engency.jotarudyardkipling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DataUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 is OnRequestDataUpdate) {
            p0.onUpdateRequested()
        }
        Log.d("FCM", "triggered refresh")
    }

}