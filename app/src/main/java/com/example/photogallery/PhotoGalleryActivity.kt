package com.example.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        val isFragmentEmpty = savedInstanceState == null
        if (isFragmentEmpty) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, GalleryMainFragment.newInstance())
                .commit()
        }
    }
}