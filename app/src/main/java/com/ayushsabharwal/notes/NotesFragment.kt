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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
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
            if (noteText.isNotEmpty() && buttonText == "Add note") {
                viewModel.insertNote(Note(noteText))
            } else if (noteText.isNotEmpty() && buttonText == "Save note") {
                currentNote.text = noteText
                viewModel.updateNote(currentNote)
                binding.addNote.text = "Add note"
            }
        }

        binding.clearNote.setOnClickListener {
            binding.enterANote.setText("")
        }

        return binding.root
    }

    override fun onItemClicked(note: Note) {
        currentNote = note
        binding.enterANote.setText(note.text)
        binding.addNote.text = "Save note"
    }

    override fun onItemClicked2(note: Note) {
        viewModel.deleteNote(note)
    }
}