package com.example.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "FlickrFetcher"

class FlickrFetcher(private var flickrApi: FlickrApi) {   //some kind of repository
    private lateinit var flickrRequest: Call<PhotoResponse>

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val fetchingData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        flickrRequest = flickrApi.flickrCall.fetchPhotos()

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

    fun cancelRequestInFlight() {
        if (::flickrRequest.isInitialized)
            flickrRequest.cancel()
    }
}