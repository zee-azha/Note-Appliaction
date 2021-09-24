package com.example.mynoteapps.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynoteapps.Preferences.PrefViewModelFactory
import com.example.mynoteapps.Preferences.PreferencesViewModel
import com.example.mynoteapps.Preferences.SettingPreference
import com.example.mynoteapps.R
import com.example.mynoteapps.ViewModelFactory
import com.example.mynoteapps.databinding.ActivityMainBinding
import com.example.mynoteapps.insert.NoteAddUpdateActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private var _activityMainBinding : ActivityMainBinding? = null
    private val binding get() = _activityMainBinding

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val mainViewModel = obtainViewModel(this@MainActivity)
        mainViewModel.getAllnote().observe(this,{noteList ->
            if (noteList != null){
                adapter.setListNotes(noteList)
            }
        })

        adapter = NoteAdapter()

        binding?.rvNotes?.layoutManager = LinearLayoutManager(this)
        binding?.rvNotes?.setHasFixedSize(true)
        binding?.rvNotes?.adapter = adapter

        binding?.fabAdd?.setOnClickListener{view->
            val intent = Intent(this@MainActivity,NoteAddUpdateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel{
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(MainViewModel::class.java)

    }

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        val switchTheme = menu.findItem(R.id.darkMode).actionView as SwitchMaterial
        val pref = SettingPreference.getInstance(dataStore)
        val mainViewModel = ViewModelProvider(this, PrefViewModelFactory(pref)).get(
            PreferencesViewModel::class.java
        )
        mainViewModel.getThemeSetting().observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            })
        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }
        return super.onCreateOptionsMenu(menu)
    }

}