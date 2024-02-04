package com.example.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.databinding.FragmentNotesBinding

class NotesFragment : Fragment(), NotesAdapterInterface {

    private lateinit var binding: FragmentNotesBinding
    lateinit var viewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = NotesAdapter(requireContext(), this)
        binding.recyclerView.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[NoteViewModel::class.java]
        viewModel.allNotes.observe(requireActivity()) { list ->
            list?.let {
                adapter.updateList(it)
            }
        }

        binding.addNote.setOnClickListener {
            val noteText = binding.enterANote.text.toString()
            if (noteText.isNotEmpty()) {
                viewModel.insertNote(Note(noteText))
            }
        }

        return binding.root
    }

    override fun onItemClicked(note: Note) {
        viewModel.deleteNote(note)
    }
}