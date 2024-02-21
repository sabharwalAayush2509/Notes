package com.ayushsabharwal.notes

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

    private lateinit var binding: FragmentPersonBinding

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

        binding.notesCreated.text = "Notes created: 25"
        binding.notesEdited.text = "Notes edited: 25"
        binding.notesDeleted.text = "Notes deleted: 25"

        return binding.root
    }
}