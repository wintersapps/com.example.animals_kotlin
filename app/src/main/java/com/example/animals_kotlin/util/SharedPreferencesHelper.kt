package com.example.animals_kotlin.util

import android.content.Context
import androidx.preference.PreferenceManager

class SharedPreferencesHelper(context: Context) {

    private val prefApiKey = "prefApiKey"

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    fun saveApiKey(key: String?){
        prefs.edit().putString(prefApiKey, key).apply()
    }

    fun getApiKey() = prefs.getString(prefApiKey, null)
}