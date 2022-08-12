package com.fromnone.lfyrepositoryx

import androidx.fragment.app.FragmentActivity

/**
 * description ： TODO:类的作用
 * author : lfy
 * email : 1623658271@qq.com
 * date : 2022/8/12 17:04
 */
object PermissionX {
    private const val TAG = "InvisibleFragment"

    fun request(activity:FragmentActivity,vararg permissions:String,callback: PermissionCallback){
        val fragmentManager = activity.supportFragmentManager
        val existedFragment = fragmentManager.findFragmentByTag(TAG)
        val fragment = if(existedFragment!=null){
            existedFragment as InvisibleFragment
        }else{
            val invisibleFragment = InvisibleFragment()
            fragmentManager.beginTransaction().add(invisibleFragment, TAG).commitNow()
            invisibleFragment
        }
        fragment.requestNow(callback,*permissions)
    }
}