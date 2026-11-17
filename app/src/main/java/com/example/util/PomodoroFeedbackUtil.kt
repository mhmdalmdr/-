package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.model.PomodoroPhase

private const val TAG = "PomodoroFeedbackUtil"
private const val POMODORO_NOTIFICATION_ID = 9901

object PomodoroFeedbackUtil {

    fun notifyPhaseCompleted(
        context: Context,
        finishedPhase: PomodoroPhase,
        subjectTitle: String,
        playSound: Boolean,
        sendNotification: Boolean
    ) {
        if (playSound) {
            playCompletionChime(context)
            triggerGentleVibration(context)
        }

        if (sendNotification) {
            val (title, body) = when (finishedPhase) {
                PomodoroPhase.FOCUS -> {
                    "انتهت جلسة التركيز • SudX" to
                        "أحسنت يا بطل! أتممت جلسة مذاكرة في «$subjectTitle». خذ استراحة الآن لتجديد نشاطك."
                }
                PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
                    "انتهت الاستراحة • SudX" to
                        "حان وقت العودة إلى «$subjectTitle» بجولة تركيز جديدة!"
                }
            }
            try {
                StudyNotificationUtil.showImmediateNotification(
                    context = context,
                    notificationId = POMODORO_NOTIFICATION_ID,
                    title = title,
                    message = body
                )
            } catch (t: Throwable) {
                Log.w(TAG, "Unable to show Pomodoro completion notification", t)
            }
        }
    }

    private fun playCompletionChime(context: Context) {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(context, uri)
                if (ringtone != null) {
                    ringtone.play()
                    return
                }
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Ringtone playback unavailable, trying ToneGenerator", t)
        }

        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
        } catch (t: Throwable) {
            Log.w(TAG, "ToneGenerator unavailable in runtime", t)
        }
    }

    @Suppress("DEPRECATION")
    private fun triggerGentleVibration(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (!vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0L, 180L, 100L, 220L), -1)
                )
            } else {
                vibrator.vibrate(300L)
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Vibration unavailable", t)
        }
    }
}
