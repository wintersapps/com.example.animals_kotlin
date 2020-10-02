package com.example.animals_kotlin

import android.app.Application
import com.example.animals_kotlin.di.PrefsModule
import com.example.animals_kotlin.util.SharedPreferencesHelper

class PrefsModuleTest(val mockPrefs: SharedPreferencesHelper): PrefsModule() {

    override fun provideSharedPreferences(app: Application): SharedPreferencesHelper {
        return mockPrefs
    }
}