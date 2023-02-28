package com.palette.done.view.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.palette.done.data.db.entity.Alarm
import com.palette.done.receiver.DoneBroadcastReceiver
import com.palette.done.view.signin.ObAlarmFragment

class AlarmManagerUtil(val context: Context) {

    companion object {
        const val WRITE_DONE_LIST_ALARM = "WRITE_DONE_LIST_ALARM"
        private var alarmManager: AlarmManager? = null
    }

    private fun alarmManager(): AlarmManager {
        if(alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        return alarmManager!!
    }

    fun setAlarm(alarm: Alarm) {
        val pendingIntent = getAlarmPendingIntent(alarm)

        alarmManager().setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            CalendarUtil.getNextTimeInMillisFromNow(alarm.hour, alarm.min), pendingIntent
        )
    }

    fun cancelAndSetAlarm(alarm: Alarm) {
        val pendingIntent = getAlarmPendingIntent(alarm)

        alarmManager().cancel(pendingIntent)

        alarmManager().setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            CalendarUtil.getNextTimeInMillisFromNow(alarm.hour, alarm.min), pendingIntent
        )
    }

    private fun getAlarmPendingIntent(alarm: Alarm): PendingIntent {
        val intent = Intent(context, DoneBroadcastReceiver::class.java)
        intent.putExtra(WRITE_DONE_LIST_ALARM, alarm)

        return PendingIntent.getBroadcast(
            context, DoneBroadcastReceiver.NOTIFICATION_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}