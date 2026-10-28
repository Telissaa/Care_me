package pl.wluczak.care_me

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import pl.wluczak.care_me.di.appModule
import pl.wluczak.care_me.di.databaseModule
import pl.wluczak.care_me.di.networkModule

class CareMeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.ERROR)
            }
            androidContext(this@CareMeApplication)
            modules(appModule, databaseModule, networkModule)
        }
        
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Care me Powiadomienia",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
