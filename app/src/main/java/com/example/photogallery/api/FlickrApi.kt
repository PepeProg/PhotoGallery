package com.example.photogallery.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalStateException

class FlickrApi private constructor() {
    var flickrApiCall: FlickrApiInterface
    init {
        val retrofit: Retrofit = Retrofit.Builder()     //building retrofit-object, that later will be used for creating FlickrApi
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())  //adding Gson converter(Deserializer for JSON)
            .build()
        flickrApiCall = retrofit.create(FlickrApiInterface::class.java)  //here is our request
    }

    companion object {
        private var INSTANCE: FlickrApi? = null
        fun initialize() {
            if (INSTANCE == null)
                INSTANCE = FlickrApi()
        }

        fun get(): FlickrApi {
            return INSTANCE ?: throw IllegalStateException("FlickrApi must be initialized!")
        }
    }
}