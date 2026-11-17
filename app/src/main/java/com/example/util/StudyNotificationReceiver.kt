package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StudyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getStringExtra("SESSION_ID") ?: return
        val subjectTitle = intent.getStringExtra("SUBJECT_TITLE") ?: "مادة الشهادة السودانية"
        val topic = intent.getStringExtra("TOPIC") ?: "جلسة مذاكرة مقررة"
        val isDaily = intent.getBooleanExtra("IS_DAILY", false)
        val dayOfWeek = intent.getIntExtra("DAY_OF_WEEK", 1)
        val hour = intent.getIntExtra("HOUR", 8)
        val minute = intent.getIntExtra("MINUTE", 0)

        Log.d("StudyNotificationReceiver", "Triggering reminder notification for $subjectTitle ($topic)")

        // Show the notification with encouraging Sudanese teacher tone
        val title = "⏰ حان موعد مذاكرة: $subjectTitle"
        val message = "يا بطل الشهادة، وقت جلسة «$topic». استعن بالله وركز لتنال التفوق بإذن الله!"
        val notificationId = sessionId.hashCode()

        StudyNotificationUtil.showImmediateNotification(
            context = context,
            notificationId = notificationId,
            title = title,
            message = message
        )

        // Reschedule for next time (daily or weekly)
        StudyNotificationUtil.scheduleStudyReminder(
            context = context,
            sessionId = sessionId,
            subjectTitle = subjectTitle,
            topic = topic,
            dayOfWeek = dayOfWeek,
            hour = hour,
            minute = minute,
            isDaily = isDaily
        )
    }
}
