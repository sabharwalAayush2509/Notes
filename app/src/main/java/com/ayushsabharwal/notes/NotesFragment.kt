package com.ayushsabharwal.notes

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayushsabharwal.notes.databinding.FragmentNotesBinding

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
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(Application())
        )[NoteViewModel::class.java]
        viewModel.allNotes.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.updateList(it)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.allNotes.observe(viewLifecycleOwner) { list ->
                list?.let {
                    adapter.updateList(it)
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.addNote.setOnClickListener {
            val noteText = binding.enterANote.text.toString()
            if (noteText.isNotEmpty()) {
                viewModel.insertNote(Note(noteText))
            }
        }

        binding.removeNote.setOnClickListener {
            binding.enterANote.setText("")
        }

        return binding.root
    }

    override fun onItemClicked(note: Note) {
        viewModel.deleteNote(note)
    }
}