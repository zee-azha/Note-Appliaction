package com.example.mynoteapps.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mynoteapps.database.Note
import com.example.mynoteapps.database.NoteRepository

class MainViewModel(application: Application): ViewModel() {
    private val mNoteRepository: NoteRepository = NoteRepository(application)

    fun getAllnote(): LiveData<List<Note>> = mNoteRepository.getAllNotes()
}