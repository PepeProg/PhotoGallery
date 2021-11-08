package com.example.photogallery.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalStateException

class FlickrApi private constructor() {
    var flickrCall: FlickrApiInterface
    init {
        val gsonBuilder = GsonBuilder()
            .registerTypeAdapter(PhotoResponse::class.java, PhotoDeserializer())    //adding custom deserializer
            .create()

        val client = OkHttpClient.Builder()     //creating client to add our interceptor
            .addInterceptor(PhotoInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()     //building retrofit-object, that later will be used for creating FlickrApi
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))  //adding Gson converter(Deserializer for JSON) with our built Gson
            .client(client)
            .build()

        flickrCall = retrofit.create(FlickrApiInterface::class.java)  //here is our request
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