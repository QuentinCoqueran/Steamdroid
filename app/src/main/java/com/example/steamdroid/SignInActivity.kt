package com.example.steamdroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.steamdroid.home.HomeActivity
import com.example.steamdroid.search.SearchGameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.steamdroid.game_details.GameDetailsActivity

class SignInActivity : Activity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]


    @SuppressLint("MissingInflatedId")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        auth = Firebase.auth
        isConnected()
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<TextView>(R.id.password_input)
        val loginButton = findViewById<TextView>(R.id.login_button)
        val createAccountRedirect = findViewById<TextView>(R.id.create_account_redirect)
        val forgotPasswordRedirect = findViewById<TextView>(R.id.text_view)
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        createAccountRedirect.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
        forgotPasswordRedirect.setOnClickListener {
            startActivity(Intent(this, GameDetailsActivity::class.java))
        }
/*        forgotPasswordRedirect.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }*/
    }

    private fun isConnected() {
        val user = auth.currentUser
        if (user != null) {
            auth.signOut();
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }
    // [END on_start_check_user]

    fun signIn(email: String, password: String, redirect: Boolean = true) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    //redirect to home
                    if (redirect) {
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}