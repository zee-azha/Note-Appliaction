package com.example.mynoteapps.Preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import java.lang.IllegalArgumentException


class PrefViewModelFactory(private val pref: SettingPreference): NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       if(modelClass.isAssignableFrom(PreferencesViewModel::class.java)){
            return PreferencesViewModel(pref) as T
       }
        throw IllegalArgumentException("Unknown ViewModel class:"+modelClass.name)
    }
}