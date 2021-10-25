package com.example.photogallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photogallery.api.FlickrApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "FlickrFetcher"

class FlickrFetcher {   //some kind of repository
    private var flickrApi: FlickrApi
    init {
        val retrofit: Retrofit = Retrofit.Builder()     //building retrofit-object, that later will be used for creating FlickrApi
            .baseUrl("https://www.flickr.com/")
            .addConverterFactory(ScalarsConverterFactory.create())  //converter from okhttp3.ResponseBody to string
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)  //here is our request
    }

    fun fetchContents(): LiveData<String> {
        val fetchingData: MutableLiveData<String> = MutableLiveData()
        val flickrRequest = flickrApi.fetchContents()
        flickrRequest.enqueue(object: Callback<String> {    //we are putting request into the queue
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Fetching went wrong", t)
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                fetchingData.value = response.body()    //text of request's response
                Log.d(TAG, "Response received successfully!")
            }
        })
        return fetchingData
    }
}