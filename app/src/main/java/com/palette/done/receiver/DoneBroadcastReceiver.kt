package com.palette.done.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings.Global
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Alarm
import com.palette.done.view.SplashActivity
import com.palette.done.view.util.AlarmManagerUtil.Companion.WRITE_DONE_LIST_ALARM
import com.palette.done.view.util.parcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar

class DoneBroadcastReceiver : BroadcastReceiver() {

    companion object {

        const val NOTIFICATION_ID = 0
        const val CHANNEL_ID = "notification_local_channel"
    }

    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarm = intent.parcelable<Alarm>(WRITE_DONE_LIST_ALARM) ?: return

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        /**
         * WorkManager를 사용하여 다음날에 울릴 알람 등록
         * 왜? The BroadcastReceiver is deactivated after onReceive()
         * https://developer.android.com/guide/components/broadcasts
         */
        val workRequest = OneTimeWorkRequestBuilder<SetAlarmWorker>().build()
        val workManager = WorkManager.getInstance(context.applicationContext)
        workManager.enqueue(workRequest)


        if(currentDay in alarm.days) {
            val channelName = context.getString(R.string.channel_write_done_list)
            createNotificationChannel(channelName)
            deliverNotification(context.applicationContext)
        }
    }

    private fun createNotificationChannel(channelName: String){
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(
            notificationChannel)
    }

    // Notification 등록
    private fun deliverNotification(context: Context){
        val contentIntent = Intent(context, SplashActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("타이틀 입니다.")
            .setContentText("내용 입니다.")
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}