package com.example.steamdroid.user

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.example.steamdroid.R
import com.example.steamdroid.search.SearchGame

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        auth = Firebase.auth
        isConnected()

        val args = Bundle()

        val emailInput = view.findViewById<EditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextView>(R.id.password_input)
        val loginButton = view.findViewById<TextView>(R.id.login_button)
        val createAccountRedirect = view.findViewById<TextView>(R.id.create_account_redirect)
        val forgotPasswordRedirect = view.findViewById<TextView>(R.id.text_view)

        emailInput.setText(arguments?.getString("email"))
        passwordInput.text = arguments?.getString("password")

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        createAccountRedirect.setOnClickListener {
            args.putString("email", emailInput.text.toString())
            args.putString("password", passwordInput.text.toString())
            navController.navigate(R.id.action_signInFragment_to_createAccountFragment, args)
        }

        forgotPasswordRedirect.setOnClickListener {
            args.putString("email", emailInput.text.toString())
            navController.navigate(R.id.action_signInFragment_to_initializationPasswordFragment, args)
        }
    }

    private fun isConnected() {
        val user = auth.currentUser
        if (user != null) {
            auth.signOut()
            navController.navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    fun signIn(email: String, password: String, redirect: Boolean = true) {
        auth = Firebase.auth
        (context as Activity?)?.let {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        //redirect to home
                        if (redirect) {
                            navController.navigate(R.id.action_signInFragment_to_homeFragment)
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}