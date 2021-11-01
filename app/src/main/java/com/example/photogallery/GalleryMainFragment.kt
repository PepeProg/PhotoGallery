package com.example.photogallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

private const val TAG = "GalleryMainFragment"

class GalleryMainFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_main, container, false)
        recyclerView = view.findViewById(R.id.gallery_recycler_view) as RecyclerView
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(1, GridLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredGridLayoutManager
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {     //this func will be called when Recycler's size is known and we can change number of columns according to it
                staggeredGridLayoutManager.spanCount = 3
                staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
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

    private class PhotoHolder(itemView: TextView): RecyclerView.ViewHolder(itemView) {
        val bindTitle: (CharSequence) -> Unit = itemView::setText
    }

    private class PhotoAdapter(val galleryList: List<GalleryItem>):RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = TextView(parent.context)
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val title = galleryList[position].title
            holder.bindTitle(title)
        }

        override fun getItemCount(): Int {
            return galleryList.size
        }
    }

    companion object {
        fun newInstance(): GalleryMainFragment {
            return GalleryMainFragment()
        }
    }
}