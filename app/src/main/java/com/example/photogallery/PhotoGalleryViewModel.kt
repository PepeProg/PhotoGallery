package com.example.photogallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel: ViewModel() {
    var galleryItems: LiveData<List<GalleryItem>>
    var repository = FlickrFetcher()
    init {
        galleryItems =  repository.fetchPhotos()
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancelRequestInFlight()
    }
}