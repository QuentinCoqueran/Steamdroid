package com.example.steamdroid.user

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.steamdroid.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CreateAccountFragment : Fragment() {
    private val handler = Handler()
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private var isFinished = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.username_input)
        val createAccount = view.findViewById<Button>(R.id.create_account)
        val logInRedirect = view.findViewById<Button>(R.id.log_in_redirect)
        val validatePassword = view.findViewById<TextInputEditText>(R.id.password_confirm_label)
        var checkPass = false
        createAccount.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val username = usernameInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && checkPass) {
                createAccountWithUsername(emailInput, passwordInput, usernameInput)
            } else {
                if (email.isEmpty()) {
                    emailInput.setBackgroundResource(R.drawable.border_red)
                    emailInput.error = resources.getString(R.string.email_error_required)
                }
                if (password.isEmpty()) {
                    passwordInput.setBackgroundResource(R.drawable.border_red)
                    passwordInput.error = resources.getString(R.string.password_error_required)
                }
                if (username.isEmpty()) {
                    usernameInput.setBackgroundResource(R.drawable.border_red)
                    usernameInput.error = resources.getString(R.string.username_error_required)
                }
            }
        }

        logInRedirect.setOnClickListener {
            navController.navigate(R.id.action_createAccountFragment_to_signInFragment2)
        }

        usernameInput.doOnTextChanged { text, _, _, _ ->
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("steamandroid").whereEqualTo("username", text.toString())
            docRef.get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    usernameInput.setBackgroundResource(R.drawable.border_red)
                    usernameInput.error = resources.getString(R.string.username_error)
                    checkPass = false
                } else {
                    usernameInput.setBackgroundResource(R.drawable.input_border_rounded)
                    usernameInput.error = null
                    checkPass = true
                }
            }
        }
        emailInput.doOnTextChanged { text, _, _, _ ->
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("steamandroid").whereEqualTo("email", text.toString())
            docRef.get().addOnSuccessListener { documents ->
                if (!Patterns.EMAIL_ADDRESS.matcher(text.toString())
                        .matches()
                ) {
                    emailInput.setBackgroundResource(R.drawable.border_red)
                    checkPass = false
                    emailInput.error = resources.getString(R.string.email_error_regex)
                } else if (!documents.isEmpty) {
                    emailInput.setBackgroundResource(R.drawable.border_red)
                    checkPass = false
                    emailInput.error = resources.getString(R.string.email_error)
                } else {
                    emailInput.setBackgroundResource(R.drawable.input_border_rounded)
                    emailInput.error = null
                    checkPass = true
                }
            }
        }
        // check if validatePassword is same of passwordInput
        validatePassword.doOnTextChanged { _, _, _, _ ->
            validatePassword.setBackgroundResource(R.drawable.input_border_rounded)
            validatePassword.error = null
            checkPass = false
            if (passwordInput.text.toString() != validatePassword.text.toString()) {
                validatePassword.setBackgroundResource(R.drawable.border_red)
                validatePassword.error = resources.getString(R.string.password_error)
                checkPass = false
            } else {
                validatePassword.setBackgroundResource(R.drawable.input_border_rounded)
                validatePassword.error = null
                checkPass = true
            }
        }
        // check if validatePassword is same of passwordInput
        passwordInput.doOnTextChanged { _, _, _, _ ->
            passwordInput.setBackgroundResource(R.drawable.input_border_rounded)
            passwordInput.error = null
            checkPass = false
            if (passwordInput.text.toString().isNotEmpty()) {
                if (passwordInput.text.toString().length < 6) {
                    passwordInput.setBackgroundResource(R.drawable.border_red)
                    passwordInput.error = resources.getString(R.string.password_error_regex)
                    checkPass = false
                } else if (passwordInput.text.toString() != validatePassword.text.toString()) {
                    validatePassword.setBackgroundResource(R.drawable.border_red)
                    validatePassword.error = resources.getString(R.string.password_error)
                    checkPass = false
                } else {
                    validatePassword.setBackgroundResource(R.drawable.input_border_rounded)
                    validatePassword.error = null
                    checkPass = true
                }
            } else {
                validatePassword.setBackgroundResource(R.drawable.input_border_rounded)
                validatePassword.error = null
                checkPass = false
            }
        }
        auth = Firebase.auth
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    SignInFragment().signIn(email, password, false)
                    isFinished = true
                    navController.navigate(R.id.action_createAccountFragment_to_homeFragment)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.error_server),
                        Toast.LENGTH_SHORT
                    ).show()
                    task.exception?.printStackTrace()
                }
            }
    }

    private fun createAccountWithUsername(
        emailInput: TextInputEditText,
        passwordInput: TextInputEditText,
        usernameInput: TextInputEditText
    ) {
        isFinished = false
        showWaitingDots()
        val username = usernameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "password" to password
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("steamandroid").add(user).addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                createAccount(email, password)
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.error_server),
                    Toast.LENGTH_SHORT
                ).show()
                task.exception?.printStackTrace()
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
