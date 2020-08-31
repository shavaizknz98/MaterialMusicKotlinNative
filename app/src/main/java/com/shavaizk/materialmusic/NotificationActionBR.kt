package com.shavaizk.materialmusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationActionBR: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        p0!!.sendBroadcast(Intent("TRACKS_TRACKS").putExtra("actionname", p1!!.action))
    }
}