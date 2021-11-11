package com.example.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import java.util.concurrent.TimeUnit

private const val TAG = "GalleryMainFragment"
private const val POLL_WORK = "pollWork"

class GalleryMainFragment: VisibleFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>  //downloaded images will be saved in the PhotoHolder,
                                                                                //so it can be an ID for Downloading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)
        retainInstance = true   //it is not the best decision, but it helps us to match downloader to fragment's lifecycle during configuration changing
        val responseHandler = Handler(Looper.getMainLooper())
        thumbnailDownloader = ThumbnailDownloader(responseHandler) {holder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            holder.bindDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)  //allows observing fragment's lifecycle
        viewLifecycleOwnerLiveData.observe(this) {
            it?.lifecycle?.addObserver(thumbnailDownloader.viewLifecycleObserver)   //observing fragment's view lifecycle
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_gallery_main, menu)

        val searchItem: MenuItem = menu.findItem(R.id.search_view)
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {  //calls when the whole text has been typed
                    photoGalleryViewModel.searchPhotos(p0)
                    searchView.clearFocus() //closing keyboard
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {  //calls every time when text is being typed
                    return false
                }
            })

            setOnSearchClickListener {
                searchView.setQuery(photoGalleryViewModel.storedSearch, false)  //setting title for the search when SearchView is opening
            }
        }

        val pollingControl = menu.findItem(R.id.polling_control)
        val isPolling = PhotoPreferences.isPolling(requireContext())
        val controlText = if (isPolling) {
            R.string.stop_polling
        } else {
           R.string.start_polling
        }
        pollingControl.setTitle(controlText)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.clear_search -> {
                photoGalleryViewModel.searchPhotos("")  //clearing stored query
                true
            }
            R.id.polling_control -> {
                val isPolling = PhotoPreferences.isPolling(requireContext())
                if (isPolling) {    //if work exists we shall delete it
                    WorkManager.getInstance().cancelUniqueWork(POLL_WORK)
                    PhotoPreferences.setPolling(requireContext(), false)
                }
                else {    //creating periodic work
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()

                    val workPeriodicRequest = PeriodicWorkRequest
                        .Builder(CustomWorker::class.java, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build()    //creating a request for work using our class Worker

                    WorkManager.getInstance()
                        .enqueueUniquePeriodicWork(POLL_WORK, ExistingPeriodicWorkPolicy.KEEP, workPeriodicRequest) //policy shows what manager should do if this work already exists
                    PhotoPreferences.setPolling(requireContext(), true)
                }

                activity?.invalidateOptionsMenu()   //calling onCreateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
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