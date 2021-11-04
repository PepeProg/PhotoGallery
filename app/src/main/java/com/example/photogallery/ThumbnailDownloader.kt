package com.example.photogallery

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.photogallery.api.FlickrApi
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader< in T: Any>(
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
): HandlerThread(TAG){    //T is a parameter for Downloading ID, allows using different UI-elements
    val fragmentLifecycleObserver = object: LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup() {
            Log.i(TAG, "Thread has been started")
            start()
            looper
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() {
            Log.i(TAG, "Thread has been finished")
            quit()
        }
    }

    val viewLifecycleObserver = object: LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearQueue() {
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrFetcher = FlickrFetcher(FlickrApi.get())

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onLooperPrepared() {
        requestHandler = object: Handler(looper) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetcher.fetchPhoto(url) ?: return
        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit)
                return@Runnable
            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })


    }

    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Url :$url")
        requestMap[target] = url    //holders are being recycled so we should check if url has changed or not
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
            .sendToTarget()
    }



}