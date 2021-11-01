package com.example.photogallery.api

import com.google.gson.*
import java.lang.reflect.Type

class PhotoDeserializer: JsonDeserializer<PhotoResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PhotoResponse {
        val jsonObject = json?.asJsonObject ?: throw JsonParseException("Json is empty")    //casting to Json-object
        val photoResponse = Gson().fromJson<PhotoResponse>(jsonObject.get("photos"), typeOfT)   //getting PhotoResponse from Json-object and type of this Object
        return photoResponse
    }
}