package co.coffeery.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        TimerService.isRunning = false
        context.stopService(Intent(context, TimerService::class.java))
    }
}
