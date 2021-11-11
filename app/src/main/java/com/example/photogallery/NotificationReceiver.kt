package com.example.photogallery

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

private const val TAG = "NotificationReceiver"

/*We are declaring this receiver in the manifest and it sends a notification if nobody before has
cancelled it(checking result code). It has the smallest priority(-999), so it will work last of all
 */
class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        else {
            val requestCode = p1!!.getIntExtra(CustomWorker.REQUEST_CODE, 0)
            val notification: Notification = p1.getParcelableExtra(CustomWorker.NOTIFICATION)!!

            val notificationManager = NotificationManagerCompat.from(p0!!)
            notificationManager.notify(requestCode, notification)   //request code is using to identify
                                                                    // notification later(updating download status for example)
        }
    }
}