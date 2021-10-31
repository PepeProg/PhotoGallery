package com.example.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.api.FlickrApi
import com.example.photogallery.api.FlickrResponse
import com.example.photogallery.api.PhotoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetcher"

class FlickrFetcher {   //some kind of repository
    private var flickrApi: FlickrApi
    private lateinit var flickrRequest: Call<FlickrResponse>
    init {
        val retrofit: Retrofit = Retrofit.Builder()     //building retrofit-object, that later will be used for creating FlickrApi
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())  //converter from okhttp3.ResponseBody to string
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)  //here is our request
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val fetchingData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        flickrRequest = flickrApi.fetchPhotos()
        flickrRequest.enqueue(object: Callback<FlickrResponse> {    //we are putting request into the queue
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Fetching went wrong", t)
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
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