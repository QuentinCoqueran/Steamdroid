package com.example.steamdroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CreateAccountActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)
        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)
        val usernameInput = findViewById<TextInputEditText>(R.id.username_input)
        val createAccount = findViewById<Button>(R.id.create_account)
        val logInRedirect = findViewById<Button>(R.id.log_in_redirect)
        val validatePassword = findViewById<TextInputEditText>(R.id.password_confirm_label)
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

        //redirect to log in page
        logInRedirect.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
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
        validatePassword.doOnTextChanged() { _, _, _, _ ->
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
        passwordInput.doOnTextChanged() { _, _, _, _ ->
            passwordInput.setBackgroundResource(R.drawable.input_border_rounded)
            passwordInput.error = null
            checkPass = false
            if (passwordInput.text.toString().isNotEmpty()) {
                if (passwordInput.text.toString() != validatePassword.text.toString()) {
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

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }


    private fun createAccountWithUsername(
        emailInput: TextInputEditText,
        passwordInput: TextInputEditText,
        usernameInput: TextInputEditText
    ) {
        val username = usernameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "password" to password
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("steamandroid").add(user).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                createAccount(email, password)
            } else {
                updateUI(null)
            }
        }
    }
}

private fun updateUI(user: FirebaseUser?) {

}

private fun reload() {

}


