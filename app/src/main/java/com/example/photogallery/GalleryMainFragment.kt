package com.example.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException

private const val TAG = "GalleryMainFragment"

class GalleryMainFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>  //downloaded images will be saved in the PhotoHolder,
                                                                                //so it can be an ID for Downloading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
        retainInstance = true   //it is not the best decision, but it helps us to match downloader to fragment's lifecycle during configuration changing
        val responseHandler = Handler(Looper.getMainLooper())
        thumbnailDownloader = ThumbnailDownloader(responseHandler) {holder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            holder.bindDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)  //allows observing fragment's lifecycle
        viewLifecycleOwnerLiveData.observe(this) {
            it?.lifecycle?.addObserver(thumbnailDownloader.viewLifecycleObserver)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_main, container, false)
        recyclerView = view.findViewById(R.id.gallery_recycler_view) as RecyclerView
        val gridLayoutManager = GridLayoutManager(context, 1)
        recyclerView.layoutManager = gridLayoutManager

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {     //this func will be called when Recycler's size is known and we can change number of columns according to it
                gridLayoutManager.spanCount = 3
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItems.observe(viewLifecycleOwner) {
            recyclerView.adapter = PhotoAdapter(it)
        }
    }

    private class PhotoHolder(itemView: ImageView): RecyclerView.ViewHolder(itemView) {
        val bindDrawable: (Drawable) -> Unit = itemView::setImageDrawable
    }

    private inner class PhotoAdapter(val galleryList: List<GalleryItem>):RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val imageView = layoutInflater.inflate(
                R.layout.item_gallery_view,
                parent,
                false
            ) as ImageView
            return PhotoHolder(imageView)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val url = galleryList[position].url
            thumbnailDownloader.queueThumbnail(holder, url)
            val placeHolder = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bill_up_close
            ) ?: ColorDrawable()

            holder.bindDrawable(placeHolder)
        }

        override fun getItemCount(): Int {
            return galleryList.size
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    companion object {
        fun newInstance(): GalleryMainFragment {
            return GalleryMainFragment()
        }
    }
}