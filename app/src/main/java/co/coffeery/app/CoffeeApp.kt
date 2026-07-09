package co.coffeery.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class CoffeeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "coffeery_timer",
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = ""
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)

            val alertsChannel = NotificationChannel(
                "coffeery_alerts",
                "Brew Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(alertsChannel)
        }
    }
}
