package com.example.mynoteapps.helper

import androidx.recyclerview.widget.DiffUtil
import com.example.mynoteapps.database.Note

class NoteDiffCallback(private val mOldNoteList: List<Note>, private val mNewNote: List<Note>):DiffUtil.Callback() {


    override fun getOldListSize(): Int {
      return mOldNoteList.size
    }

    override fun getNewListSize(): Int {
        return mNewNote.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldNoteList[oldItemPosition].id == mNewNote[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldNoteList[oldItemPosition]
        val newEmployee = mNewNote[newItemPosition]

        return oldEmployee.title == newEmployee.title && oldEmployee.description == newEmployee.description
    }

}