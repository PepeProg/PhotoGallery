package com.example.photogallery

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.photogallery.api.FlickrApi

private const val TAG = "CustomWorker"

class CustomWorker(private val context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams) {
    override fun doWork(): Result {
        val query = PhotoPreferences.getStoredQuery(context)
        val lastResult = PhotoPreferences.getLastResultId(context)
        FlickrApi.initialize()
        val currentPhotoList: List<GalleryItem> = if (query.isEmpty()) {    //making another request to compare results
            FlickrFetcher(FlickrApi.get()).fetchPhotosRequest()
                .execute()                                                  //making request in the current thread
                .body()
                ?.galleryItems
        } else {
            FlickrFetcher(FlickrApi.get()).searchPhotosRequest(query)
                .execute()
                .body()
                ?.galleryItems
        } ?: emptyList()

        if (currentPhotoList.isEmpty())
            return Result.success()

        val currentResult = currentPhotoList[0].id  //comparing first photos
        if (lastResult != currentResult) {
            PhotoPreferences.setLastResultId(context, currentResult)

            val intent = PhotoGalleryActivity.newIntent(context)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)    //pending Intent allows NotificationManager
                                                        //to start Activity with Application's permissions(this equals launching from that Application)
            val resources = context.resources
            val notification = NotificationCompat
                    .Builder(context, NOTIFICATION_CHANNEL_ID)  //channel will be ignored if version is less than Oreo
                    .setTicker(resources.getString(R.string.new_pictures_title))    //text in the status bar's top
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))  //title for the notification in the panel
                    .setContentText(resources.getString(R.string.new_pictures_text))    //text in the panel
                    .setContentIntent(pendingIntent)    //this intent will be sent after pressing on the notification
                    .setAutoCancel(true)    //deleting notification after pressing
                    .build()

            showBackgroundNotification(0, notification)
        }

        return Result.success()
    }

    private fun showBackgroundNotification(requestCode: Int, notification: Notification) {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notification)
        }
        context.sendOrderedBroadcast(intent, NOTIFICATION_PERMISSION)  //sending intent with notification to all receivers
                                                                        // using intent action(Receivers' order is important!)
    }

    companion object {
        const val ACTION_SHOW_NOTIFICATION = "com.example.photogallery.SHOW_NOTIFICATION"
        const val NOTIFICATION_PERMISSION = "com.example.photogallery.PRIVATE"
        const val REQUEST_CODE = "requestCode"
        const val NOTIFICATION = "Notification"
    }
}