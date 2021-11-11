package com.example.photogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "FlickrFetcher"

class FlickrFetcher(private var flickrApi: FlickrApi) {   //some kind of repository
    private lateinit var flickrRequest: Call<PhotoResponse>
    private val bitmapCache: LruCache<String, Bitmap?>  //this class allows save bitmaps if they were already downloaded(least recently used will be deleted)
    init {
        val cacheSize = 1024 * 1024 * 20
        bitmapCache = LruCache(cacheSize)
    }

    fun fetchPhotosRequest(): Call<PhotoResponse> {
        return flickrApi.flickrCall.fetchPhotos()
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetaData(fetchPhotosRequest())
    }

    fun searchPhotosRequest(query: String): Call<PhotoResponse> {
        return flickrApi.flickrCall.searchPhotos(query)
    }

    fun searchPhotos(query: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetaData(searchPhotosRequest(query))
    }

    private fun fetchPhotoMetaData(request: Call<PhotoResponse>): LiveData<List<GalleryItem>> {
        val fetchingData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        flickrRequest = request

        flickrRequest.enqueue(object: Callback<PhotoResponse> {    //we are putting request into the queue
            override fun onFailure(call: Call<PhotoResponse>, t: Throwable) {
                Log.e(TAG, "Fetching went wrong", t)
            }

            override fun onResponse(call: Call<PhotoResponse>, response: Response<PhotoResponse>) {
                val photoResponse: PhotoResponse? = response.body()
                val galleryList: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryList.filterNot {
                    it.url.isBlank()
                }
                fetchingData.value = galleryList
            }
        })
        return fetchingData
    }

    @WorkerThread   //shows that func should be called in the background thread
    fun fetchPhoto(url: String): Bitmap? {
        if (bitmapCache.get(url) != null) {
            return bitmapCache.get(url)
        }
        val response: Response<ResponseBody> = flickrApi.flickrCall.fetchUrlBytes(url).execute()    //using background thread
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        bitmapCache.put(url, bitmap)
        return bitmap
    }

    fun cancelRequestInFlight() {
        if (::flickrRequest.isInitialized)
            flickrRequest.cancel()
    }
}