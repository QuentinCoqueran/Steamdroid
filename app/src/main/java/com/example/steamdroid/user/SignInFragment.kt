package com.example.steamdroid.user

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.steamdroid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInFragment : Fragment() {
    private val handler = Handler()
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private var isFinished = true
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
        val emailInput = view.findViewById<EditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextView>(R.id.password_input)
        val loginButton = view.findViewById<TextView>(R.id.login_button)
        val createAccountRedirect = view.findViewById<TextView>(R.id.create_account_redirect)
        val forgotPasswordRedirect = view.findViewById<TextView>(R.id.text_view)
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(context, getString(R.string.error_empty_signin), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        createAccountRedirect.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_createAccountFragment)
        }
        forgotPasswordRedirect.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_initializationPasswordFragment)
        }
    }

    private fun isConnected() {
        val user = auth.currentUser
        if (user != null) {
            auth.signOut();
            navController.navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }

    fun signIn(email: String, password: String, redirect: Boolean = true) {
        isFinished = false
        showWaitingDots()
        auth = Firebase.auth
        (context as Activity?)?.let {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(it) { task ->
                    isFinished = true
                    if (task.isSuccessful) {
                        if (redirect) {
                            navController.navigate(R.id.action_signInFragment_to_homeFragment)
                        }
                    } else {
                        Toast.makeText(
                            context, getString(R.string.error_empty_auth),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun showWaitingDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarWishlist)
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
        updateDots()
    }

    private fun updateDots() {
        val progressBar = view?.findViewById<ProgressBar>(R.id.progressBarWishlist)
        if (isFinished) {
            if (progressBar != null) {
                progressBar.visibility = View.GONE
            }
            return
        }
        handler.postDelayed({ updateDots() }, 1000)
    }
}