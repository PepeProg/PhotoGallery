package com.example.photogallery

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.core.content.edit

private const val STORED_QUERY = "savedQuery"

object SharedPreferences {
    fun getStoredQuery(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)  //using Preferences to store data
        return prefs.getString(STORED_QUERY, "")!!
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit() {
                putString(STORED_QUERY, query)
            }


    }
}