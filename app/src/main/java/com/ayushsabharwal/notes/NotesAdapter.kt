package com.ayushsabharwal.notes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private val context: Context, private val listener: NotesAdapterInterface) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val allNotes = ArrayList<Note>()

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
        val outlineEdit24: ImageView = itemView.findViewById(R.id.outline_edit_24)
        val outlineDeleteForever24: ImageView =
            itemView.findViewById(R.id.outline_delete_forever_24)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val viewHolder =
            NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.item_note, parent, false))
        viewHolder.outlineEdit24.setOnClickListener {
            listener.onItemClicked(allNotes[viewHolder.adapterPosition])
        }
        viewHolder.outlineDeleteForever24.setOnClickListener {
            listener.onItemClicked2(allNotes[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = allNotes[position]
        holder.text.text = currentNote.text
        holder.text.fixTextSelection()
    }

    private fun TextView.fixTextSelection() {
        setTextIsSelectable(false)
        post { setTextIsSelectable(true) }
    }

    fun updateList(newList: List<Note>) {
        allNotes.clear()
        allNotes.addAll(newList)
        listener.onDataChanged(allNotes.isEmpty())
        notifyDataSetChanged()
    }
}

interface NotesAdapterInterface {
    fun onDataChanged(isEmpty: Boolean)
    fun onItemClicked(note: Note)
    fun onItemClicked2(note: Note)
}