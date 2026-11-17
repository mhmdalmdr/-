package com.example.util

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object StudyNotificationUtil {

    const val CHANNEL_ID = "study_reminder_channel"
    private const val CHANNEL_NAME = "تذكيرات جدول المذاكرة"
    private const val CHANNEL_DESC = "إشعارات تذكيرية بمواعيد مذاكرة مواد الشهادة السودانية"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showImmediateNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String
    ) {
        createNotificationChannel(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("StudyNotificationUtil", "POST_NOTIFICATIONS permission not granted; skipping notification.")
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_sudx)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Log.e("StudyNotificationUtil", "Permission denied for notification", e)
        } catch (e: Exception) {
            Log.e("StudyNotificationUtil", "Failed to show notification", e)
        }
    }

    fun scheduleStudyReminder(
        context: Context,
        sessionId: String,
        subjectTitle: String,
        topic: String,
        dayOfWeek: Int, // Calendar.SATURDAY, Calendar.SUNDAY, etc.
        hour: Int,
        minute: Int,
        isDaily: Boolean
    ) {
        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, StudyNotificationReceiver::class.java).apply {
            putExtra("SESSION_ID", sessionId)
            putExtra("SUBJECT_TITLE", subjectTitle)
            putExtra("TOPIC", topic)
            putExtra("IS_DAILY", isDaily)
            putExtra("DAY_OF_WEEK", dayOfWeek)
            putExtra("HOUR", hour)
            putExtra("MINUTE", minute)
        }

        val requestCode = sessionId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (!isDaily) {
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
            }

            // If time already passed today/this week, schedule for next occurrence
            if (timeInMillis <= System.currentTimeMillis()) {
                if (isDaily) {
                    add(Calendar.DAY_OF_YEAR, 1)
                } else {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }
        }

        val canUseExactAlarm = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        try {
            if (canUseExactAlarm) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d("StudyNotificationUtil", "Scheduled alarm for $subjectTitle at ${calendar.time} (exact=$canUseExactAlarm)")
        } catch (e: SecurityException) {
            Log.w("StudyNotificationUtil", "Exact alarm denied, falling back to inexact alarm", e)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelStudyReminder(context: Context, sessionId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, StudyNotificationReceiver::class.java)
        val requestCode = sessionId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
