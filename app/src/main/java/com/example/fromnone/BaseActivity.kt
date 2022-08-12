package com.example.fromnone

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * description ： TODO:类的作用
 * author : lfy
 * email : 1623658271@qq.com
 * date : 2022/8/1 11:50
 */
open class BaseActivity: AppCompatActivity() {
    val wuwu = 0
    val TAG = "lfy"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ${javaClass.simpleName}")
    }
}