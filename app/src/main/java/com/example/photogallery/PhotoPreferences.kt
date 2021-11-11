package com.example.photogallery

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.core.content.edit

private const val STORED_QUERY = "savedQuery"
private const val LAST_RESULT = "LastResult"
private const val IS_POLLING = "isPolling"

object PhotoPreferences {
    fun getStoredQuery(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)  //using Preferences to store data
        return prefs.getString(STORED_QUERY, "") ?: ""
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit() {
                putString(STORED_QUERY, query)
            }
    }

    fun getLastResultId(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(LAST_RESULT, "") ?: ""
    }

    fun setLastResultId(context: Context, result: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit() {
                putString(LAST_RESULT, result)
            }
    }

    fun isPolling(context: Context) : Boolean{
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(IS_POLLING, false)
    }

    fun setPolling(context: Context, isPolling: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit() {
                putBoolean(IS_POLLING, isPolling)
            }
    }
}