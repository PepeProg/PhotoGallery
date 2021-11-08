package com.example.photogallery

import android.app.Application
import androidx.lifecycle.*
import com.example.photogallery.api.FlickrApi

class PhotoGalleryViewModel(private val app: Application): AndroidViewModel(app) {
    val galleryItems: LiveData<List<GalleryItem>>
    private var repository: FlickrFetcher
    private val searchQuery = MutableLiveData(SharedPreferences.getStoredQuery(app))

    val storedSearch
        get() = searchQuery.value ?: ""

    init {
        FlickrApi.initialize()
        repository = FlickrFetcher(FlickrApi.get())
        galleryItems = Transformations.switchMap(searchQuery) {
            if (it.isBlank())
                repository.fetchPhotos()    //downloading page with interesting photos
            else
                repository.searchPhotos(it)
        }
    }

    fun searchPhotos(query: String?) {
        query?.let {
            SharedPreferences.setStoredQuery(app, query)    //saving our last query
            searchQuery.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancelRequestInFlight()
    }
}