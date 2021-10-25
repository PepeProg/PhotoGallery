package com.example.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "GalleryMainFragment"

class GalleryMainFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flickrLiveData = FlickrFetcher().fetchContents()    //here we are getting livedata with string of response
        flickrLiveData.observe(
            this
        ) {
            Log.d(TAG, "Received response: $it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_main, container, false)
        recyclerView = view.findViewById(R.id.gallery_recycler_view) as RecyclerView
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
        }
        return view
    }

    companion object {
        fun newInstance(): GalleryMainFragment {
            return GalleryMainFragment()
        }
    }
}