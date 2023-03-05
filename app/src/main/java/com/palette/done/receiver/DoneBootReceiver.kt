package com.palette.done.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class DoneBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val workRequest = OneTimeWorkRequestBuilder<SetAlarmWorker>().build()
            val workManager = WorkManager.getInstance(context.applicationContext)
            workManager.enqueue(workRequest)
        }
    }
}