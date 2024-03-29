package com.ayushsabharwal.notes

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayushsabharwal.notes.databinding.FragmentNotesBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class NotesFragment : Fragment(), NotesAdapterInterface {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var createNote: String
    private lateinit var saveNote: String
    private lateinit var binding: FragmentNotesBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var currentNote: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences(KEY_NOTES_PREFERENCES, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString(KEY_USER_ID, "").toString()

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        createNote = getString(R.string.create_note)
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
        viewModel.getAllNotesByUserId(userId).observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter.updateList(it)
            }
        }

        binding.outlinePerson24.setOnClickListener {
            findNavController().navigate(R.id.action_notesFragment_to_personFragment)
        }

        binding.outlineLogout24.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                findNavController().navigate(R.id.action_notesFragment_to_loginFragment)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAllNotesByUserId(userId).observe(viewLifecycleOwner) { list ->
                list?.let {
                    adapter.updateList(it)
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.clearNote.setOnClickListener {
            binding.enterANote.setText("")
        }

        binding.createNote.setOnClickListener {
            val noteText = binding.enterANote.text.toString().trim()
            val buttonText = binding.createNote.text
            if (noteText.isNotEmpty() && buttonText == createNote) {
                viewModel.insertNote(Note(noteText, userId))
            } else if (noteText.isNotEmpty() && buttonText == saveNote) {
                currentNote.text = noteText
                viewModel.updateNote(currentNote)
                binding.createNote.text = createNote
            }
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
        binding.createNote.text = saveNote
    }

    override fun onItemClicked2(note: Note) {
        viewModel.deleteNote(note)
    }
}