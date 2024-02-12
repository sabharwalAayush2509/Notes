package com.ayushsabharwal.notes

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    val getAllNotes: LiveData<List<Note>> = noteDao.getAllNotes()
}