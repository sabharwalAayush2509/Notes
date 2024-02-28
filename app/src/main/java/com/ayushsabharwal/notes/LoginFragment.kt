package com.ayushsabharwal.notes

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

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentLoginBinding
    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        account?.let {
            val personDisplayName = account.displayName

            updateUI()
            Toast.makeText(
                requireContext(),
                "Welcome Back! $personDisplayName \uD83C\uDF89",
                Toast.LENGTH_SHORT
            ).show()
        }

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        sharedPreferences =
            requireContext().getSharedPreferences(KEY_NOTES_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

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

    private fun updateUI() {
        findNavController().navigate(R.id.action_loginFragment_to_notesFragment)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.let {
                val personId = account.id
                val personDisplayName = account.displayName

                if (sharedPreferences.getString(KEY_USER_ID, "") != personId) {
                    val editor = sharedPreferences.edit()
                    editor.putString(KEY_USER_ID, personId)
                    editor.apply()
                }
                updateUI()
                Toast.makeText(
                    requireContext(), "Hello! $personDisplayName \uD83D\uDC4B", Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: ApiException) {
            Log.d("message", "signInResult:failed code=${e.statusCode}")
        }
    }
}