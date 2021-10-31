package com.example.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.photogallery.api.FlickrApi

class PhotoGalleryViewModel: ViewModel() {
    var galleryItems: LiveData<List<GalleryItem>>
    private var repository: FlickrFetcher
    init {
        FlickrApi.initialize()
        repository = FlickrFetcher(FlickrApi.get())
        galleryItems =  repository.fetchPhotos()
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancelRequestInFlight()
    }
}