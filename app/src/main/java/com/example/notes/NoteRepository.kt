package com.example.notes

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    val getAllNotes: LiveData<List<Note>> = noteDao.getAllNotes()
}