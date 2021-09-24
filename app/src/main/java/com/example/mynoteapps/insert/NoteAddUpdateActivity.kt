package com.example.mynoteapps.insert

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mynoteapps.Preferences.PrefViewModelFactory
import com.example.mynoteapps.Preferences.PreferencesViewModel
import com.example.mynoteapps.Preferences.SettingPreference
import com.example.mynoteapps.R
import com.example.mynoteapps.ViewModelFactory
import com.example.mynoteapps.database.Note
import com.example.mynoteapps.databinding.ActivityNoteAddUpdateBinding
import com.example.mynoteapps.helper.DateHelper
import com.google.android.material.switchmaterial.SwitchMaterial

class NoteAddUpdateActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    companion object{
        const val EXTRA_NOTE = "extra_note"
        const val EXTRA_POSITION = "extra_position"
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
    private var position: Int = 0
    private var isEdit = false
    private  var note:Note? = null

    private lateinit var noteAddUpdateViewModel: NoteAddUpdateViewModel

    private var _activityNoteUpdateBinding : ActivityNoteAddUpdateBinding? =null
    private val binding get() = _activityNoteUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityNoteUpdateBinding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        noteAddUpdateViewModel = obtainViewModel(this@NoteAddUpdateActivity)

        note = intent.getParcelableExtra(EXTRA_NOTE)
        if(note != null){
            isEdit = true
        }
        else{
            note = Note()
        }

        val actionBarTitle: String
        val btnTitle: String

        if (isEdit){
            actionBarTitle = getString(R.string.change)
            btnTitle = getString(R.string.update)
            if (note != null){
                note?.let { note ->
                    binding?.edtTitlr?.setText(note.title)
                    binding?.edtDescription?.setText(note.description)
                }
            }
        }else{
            actionBarTitle = getString(R.string.add)
            btnTitle = getString(R.string.save)
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.btnSubmit?.text = btnTitle

        binding?.btnSubmit?.setOnClickListener{
            val title = binding?.edtTitlr?.text.toString().trim()
            val description = binding?.edtDescription?.text.toString().trim()

            when{
                title.isEmpty() ->{
                    binding?.edtTitlr?.error = getString(R.string.empty)
                }
                else ->{
                    note.let { note ->
                        note?.title = title
                        note?.description = description

                        Log.d("data", note.toString())
                    }
                    Log.d("data", note.toString())
                    if (isEdit){
                        noteAddUpdateViewModel.update(note as Note)
                        showToast(getString(R.string.changed))
                    }else{
                        note.let { note ->
                            note?.date = DateHelper.getCurrentDate()
                        }
                        noteAddUpdateViewModel.insert(note as Note)
                        showToast(getString(R.string.added))
                    }
                    finish()
                }

            }
        }

    }

    private fun showToast(string: String) {
        Toast.makeText(this,string, Toast.LENGTH_SHORT).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _activityNoteUpdateBinding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity): NoteAddUpdateViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(NoteAddUpdateViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit){
            menuInflater.inflate(R.menu.menu_form,menu)

        }

        return super.onCreateOptionsMenu(menu)
    }
    

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose){
            dialogMessage = getString(R.string.message_cancel)
            dialogTitle = getString(R.string.cancel)

        }else{
            dialogMessage = getString(R.string.message_delete)
            dialogTitle = getString(R.string.delete)
        }
        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder){
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)){_,_,->
                if(!isDialogClose){
                    noteAddUpdateViewModel.delete(note as Note)

                    val intent = Intent()
                    intent.putExtra(EXTRA_POSITION, position)
                    setResult(RESULT_DELETE, intent)
                }
                finish()
            }
            setNegativeButton(getString(R.string.no)){dialog,_,-> dialog.cancel()

            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }
}