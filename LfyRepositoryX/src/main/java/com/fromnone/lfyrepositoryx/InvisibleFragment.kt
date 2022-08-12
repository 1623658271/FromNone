package com.fromnone.lfyrepositoryx

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

/**
 * description ： TODO:类的作用
 * author : lfy
 * email : 1623658271@qq.com
 * date : 2022/8/12 16:47
 */
// 指定一个别名
typealias PermissionCallback = (Boolean, List<String>) -> Unit

class InvisibleFragment:Fragment() {
    private var callback:PermissionCallback? = null

    fun requestNow(cb:PermissionCallback,vararg permissions:String){
        callback = cb
        requestPermissions(permissions,1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            val deniedList = ArrayList<String>()
            for((index, result) in grantResults.withIndex()){
                if(result != PackageManager.PERMISSION_GRANTED){
                    deniedList.add(permissions[index])
                }
                val allGranted = deniedList.isEmpty()
                callback?.let { it(allGranted,deniedList) }
            }
        }
    }
}