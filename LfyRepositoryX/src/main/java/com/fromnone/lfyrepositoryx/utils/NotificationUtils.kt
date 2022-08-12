package com.example.fromnone.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * description ： Notification工具类
 * author : lfy
 * email : 1623658271@qq.com
 * date : 2022/8/9 09:46
 */
class NotificationUtils(val context: Context) {
    private val TAG = "lfy"
    private lateinit var manager:NotificationManager
    private lateinit var channelId: String
    private lateinit var notification: Notification
    private lateinit var name:String
    private var pendingIntent: PendingIntent? = null
    private var id:Int = 0
    fun createNotificationManager(channelId:String,name:String,importance:Int): NotificationUtils {
        this.channelId = channelId
        this.name = name
        this.manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,name,importance)
            manager.createNotificationChannel(channel)
        }
        return this
    }

    fun createPendingIntent(intent: Intent,requestCode:Int,flags:Int): NotificationUtils {
        pendingIntent = PendingIntent.getActivity(context,requestCode,intent,flags)
        return this
    }

    fun createNotification(id:Int,block: NotificationCompat.Builder.() -> NotificationCompat.Builder): NotificationUtils {
        this.id = id
        this.notification = NotificationCompat.Builder(context, channelId)
            .apply {
                block()
                pendingIntent?.let {
                    setContentIntent(pendingIntent)
                }
            }
            .build()
        return this
    }

    fun getNotificationManager(): NotificationManager {
        return manager
    }
    fun getNotification(): Notification {
        return notification
    }
    fun sendNotification(){
        manager.notify(id,notification)
    }
    fun cancelNotification(){
        manager.cancel(id)
    }
}
