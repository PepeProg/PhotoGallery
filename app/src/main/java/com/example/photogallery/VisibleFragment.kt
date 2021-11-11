package com.example.photogallery

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment

abstract class VisibleFragment: Fragment() {
    /*
    * Dynamically registered broadcast receiver that can catch intents and cancel them before
    * second receiver's working if application is using(to avoid receiving notifications)
    * */
    private val notificationReceiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            resultCode = Activity.RESULT_CANCELED   //this code will be available for all next receivers
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(CustomWorker.ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(
            notificationReceiver,
            intentFilter,
            CustomWorker.NOTIFICATION_PERMISSION,
            null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(notificationReceiver)
    }
}