package com.example.photogallery

import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s") var url: String = ""   //it's a name for field in the JSON-file
            // , Gson will automatically convert it to Kotlin-classes
)