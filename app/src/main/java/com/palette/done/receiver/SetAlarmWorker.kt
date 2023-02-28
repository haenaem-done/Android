package com.palette.done.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.palette.done.DoneApplication
import com.palette.done.data.db.entity.Alarm
import com.palette.done.view.signin.ObAlarmFragment
import com.palette.done.view.util.AlarmManagerUtil
import com.palette.done.view.util.CalendarUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class SetAlarmWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val repo = (context.applicationContext as DoneApplication).doneRepository

            val alarm = repo.getAlarm().firstOrNull() ?: return Result.failure()
            AlarmManagerUtil(context.applicationContext).setAlarm(alarm)

            return Result.success() // return statement
        } catch (e: Exception) {
            return Result.failure() // return statement
        }

    }
}