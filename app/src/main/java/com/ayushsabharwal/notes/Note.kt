package com.ayushsabharwal.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
class Note(@ColumnInfo(name = "text") var text: String, val userId: String) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}