package com.example.photogallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface FlickrApiInterface {
    @GET("/")   //using base URL-address
    fun fetchContents(): Call<String>   //type parameter is a type of response's deserializing

    @GET(
        "services/rest?method=flickr.interestingness.getList" +    //HTML-GET Request
                "&api_key=98254a27f1cb6135e49d5bb32acd7dc9" +
                "&format=json" +    //default is xml
                "&nojsoncallback=1" +   //deleting () from response
                "&extras=url_s"
    )    //adding url-address of small picture's version
    fun fetchPhotos(): Call<PhotoResponse>

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody> //using anno @Url allows to add it to Get-parameter
}
