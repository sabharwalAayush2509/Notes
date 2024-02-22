package com.ayushsabharwal.notes

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ayushsabharwal.notes.databinding.FragmentPersonBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn

class PersonFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentPersonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences(KEY_NOTES_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonBinding.inflate(inflater, container, false)

        binding.outlineClose24.setOnClickListener {
            findNavController().navigate(R.id.action_personFragment_to_notesFragment)
        }

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        account?.let {
            val personPhoto: Uri? = account.photoUrl
            val personGivenName: String? = account.givenName
            val personFamilyName: String? = account.familyName
            val personEmail: String? = account.email

            Glide.with(this).load(personPhoto)
                .apply(RequestOptions().placeholder(R.drawable.outline_person_24))
                .transition(DrawableTransitionOptions.withCrossFade()).into(binding.personPhoto)
            binding.personName.text =
                getString(R.string.person_name, personGivenName, personFamilyName)
            binding.personEmail.text = personEmail
        }

        val notesCreated = sharedPreferences.getInt(KEY_NOTES_CREATED, 0)
        val notesEdited = sharedPreferences.getInt(KEY_NOTES_EDITED, 0)
        val notesDeleted = sharedPreferences.getInt(KEY_NOTES_DELETED, 0)

        binding.notesCreated.text = getString(R.string.notes_created, notesCreated)
        binding.notesEdited.text = getString(R.string.noted_edited, notesEdited)
        binding.notesDeleted.text = getString(R.string.notes_deleted, notesDeleted)

        return binding.root
    }
}