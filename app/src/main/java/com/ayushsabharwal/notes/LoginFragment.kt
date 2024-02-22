package com.ayushsabharwal.notes

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ayushsabharwal.notes.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: NoteViewModel
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences(KEY_NOTES_PREFERENCES, Context.MODE_PRIVATE)

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        updateUI(account)
        account?.let {
            val personDisplayName = account.displayName
            Toast.makeText(
                requireContext(),
                "Welcome Back! \uD83C\uDF89 $personDisplayName",
                Toast.LENGTH_SHORT
            ).show()
        }

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(Application())
        )[NoteViewModel::class.java]

        binding.signInButton.setSize(SignInButton.SIZE_WIDE)
        binding.signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.sourceCode.movementMethod = LinkMovementMethod.getInstance()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            findNavController().navigate(R.id.action_loginFragment_to_notesFragment)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
            account?.let {
                val personId = account.id
                val personDisplayName = account.displayName

                if (sharedPreferences.getString(KEY_NOTES_Id, "") != personId) {
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.putString(KEY_NOTES_Id, personId)
                    editor.apply()
                    viewModel.deleteAllNotes()
                }
                Toast.makeText(
                    requireContext(), "Hello! \uD83D\uDC4B $personDisplayName", Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: ApiException) {
            Log.d("message", "signInResult:failed code=${e.statusCode}")
            updateUI(null)
        }
    }
}