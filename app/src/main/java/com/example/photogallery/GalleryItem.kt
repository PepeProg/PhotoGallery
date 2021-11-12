package com.example.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s") var url: String = "",   //it's a name for field in the JSON-file
            // , Gson will automatically convert it to Kotlin-classes
    @SerializedName("owner") var owner: String = ""
) {
    val photoPageUri: Uri
        get() {
            return Uri.parse("https://www.flickr.com/") //url address of each photo
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build()
        }
}