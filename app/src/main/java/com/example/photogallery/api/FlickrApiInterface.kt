package com.example.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FlickrApiInterface {
    @GET("/")   //using base URL-address
    fun fetchContents(): Call<String>   //type parameter is a type of response's deserializing

    @GET("services/rest?method=flickr.interestingness.getList")   //HTML-GET Request)
    fun fetchPhotos(): Call<PhotoResponse>

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody> //using anno @Url allows to add it to Get-parameter

    @GET("services/rest?method=flickr.photos.search")
        fun searchPhotos(@Query("text") query: String): Call<PhotoResponse> //adding field with anno "Query" to the url
}
