package com.example.fromnone.utils

import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder


/**
 * description ： 服务工具类
 * author : lfy
 * email : 1623658271@qq.com
 * date : 2022/8/11 14:11
 */
class ServiceUtils {
    inline fun <reified T>createService(context: Context){
        context.startService(Intent(context,T::class.java))
    }
}
//<service
//android:name=".utils.ServiceUtils$MyService"
//android:enabled="true"
//android:exported="true">
//</service>