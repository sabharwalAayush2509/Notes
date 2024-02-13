package com.ayushsabharwal.notes

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayushsabharwal.notes.databinding.FragmentNotesBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class NotesFragment : Fragment(), NotesAdapterInterface {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentNotesBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var currentNote: Note
    private lateinit var addNote: String
    private lateinit var saveNote: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        addNote = getString(R.string.add_note)
        saveNote = getString(R.string.save_note)
    }

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

        binding.signOut.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                findNavController().navigate(R.id.action_notesFragment_to_loginFragment)
                Toast.makeText(requireContext(), "You're signed out", Toast.LENGTH_SHORT).show()
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
            val noteText = binding.enterANote.text.toString().trim()
            val buttonText = binding.addNote.text
            if (noteText.isNotEmpty() && buttonText == addNote) {
                viewModel.insertNote(Note(noteText))
            } else if (noteText.isNotEmpty() && buttonText == saveNote) {
                currentNote.text = noteText
                viewModel.updateNote(currentNote)
                binding.addNote.text = addNote
            }
        }

        binding.clearNote.setOnClickListener {
            binding.enterANote.setText("")
        }

        return binding.root
    }

    override fun onDataChanged(isEmpty: Boolean) {
        if (isEmpty) {
            binding.noNotesToDisplay.visibility = View.VISIBLE
        } else {
            binding.noNotesToDisplay.visibility = View.GONE
        }
    }

    override fun onItemClicked(note: Note) {
        currentNote = note
        binding.enterANote.setText(note.text)
        binding.addNote.text = saveNote
    }

    override fun onItemClicked2(note: Note) {
        viewModel.deleteNote(note)
    }
}