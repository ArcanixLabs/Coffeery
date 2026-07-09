package co.coffeery.app.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import co.coffeery.app.MainActivity
import co.coffeery.app.R

class TimerService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001

        var isRunning = false
        var currentEquipment: String = ""
        var currentStep: String = ""
        var currentRemaining: String = ""
        var stepIndex: Int = 0
        var totalSteps: Int = 0

        fun update(context: Context, equipment: String, stepTitle: String, remaining: String, step: Int, total: Int) {
            currentEquipment = equipment
            currentStep = stepTitle
            currentRemaining = remaining
            stepIndex = step
            totalSteps = total
            if (isRunning) {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIFICATION_ID, buildNotification(context))
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val equipment = intent?.getStringExtra("equipment") ?: currentEquipment
        currentEquipment = equipment
        startForeground(NOTIFICATION_ID, buildNotification(this))
        isRunning = true
        return START_STICKY
    }

    override fun onDestroy() {
        isRunning = false
        super.onDestroy()
    }
}

private fun buildNotification(context: Context): Notification {
    val channelId = "coffeery_timer"
    val stopIntent = Intent(context, TimerStopReceiver::class.java)
    val stopPending = PendingIntent.getBroadcast(
        context, 0, stopIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val openIntent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val openPending = PendingIntent.getActivity(
        context, 1, openIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    val title = context.getString(R.string.notification_brewing_title, TimerService.currentEquipment)
    val stepText = if (TimerService.totalSteps > 0) {
        context.getString(R.string.notification_step, TimerService.stepIndex + 1, TimerService.totalSteps)
    } else {
        ""
    }
    val body = if (stepText.isNotEmpty()) {
        "$stepText · ${TimerService.currentRemaining}"
    } else {
        TimerService.currentRemaining
    }

    return NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSilent(true)
        .addAction(android.R.drawable.ic_media_pause, context.getString(R.string.notification_stop), stopPending)
        .setContentIntent(openPending)
        .build()
}
