package com.example.photogallery.api

import okhttp3.Interceptor
import okhttp3.Response

private const val API_KEY = "98254a27f1cb6135e49d5bb32acd7dc9"

class PhotoInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {    //every request calls this func
        val originalRequest = chain.request()
        val newUrl = originalRequest.url().newBuilder() //changing our url
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format", "json")
            .addQueryParameter("nojsoncallback", "1")
            .addQueryParameter("extras", "url_s")   //small picture
            .addQueryParameter("safesearch", "1")   //filters the result
            .build()

        val newRequest = originalRequest.newBuilder()   //creating new request
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)    //making request and getting response
    }
}