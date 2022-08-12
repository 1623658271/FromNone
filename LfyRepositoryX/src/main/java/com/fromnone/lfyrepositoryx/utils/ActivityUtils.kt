package com.example.fromnone.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

/**
 * description ： 活动工具类
 * author : lfy
 * email : 1623658271@qq.com
 * date : 2022/8/11 15:42
 */
object ActivityUtils {
     inline fun <reified T:AppCompatActivity> startActivity(context: Context, block:Intent.()->Unit){
        val intent = Intent(context,T::class.java)
        intent.block()
        context.startActivity(intent)
    }
}