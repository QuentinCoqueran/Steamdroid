package com.example.steamdroid

import android.util.Log
import android.widget.Toast
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateAccountActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<TextView>(R.id.password_input)
        val createAccount = findViewById<TextView>(R.id.create_account)
        val logInRedirect = findViewById<TextView>(R.id.log_in_redirect)
        createAccount.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                val create = createAccountWithUsername(email, password);
                if (create != null) {
                    if (create.isSuccessful) {
                        createAccount(email, password)
                    } else {
                        Toast.makeText(this, "Account not created", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        logInRedirect.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        auth = Firebase.auth
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    createAccountWithUsername(email, password)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }


    private fun createAccountWithUsername(email: String, password: String): Task<QuerySnapshot>? {
        val usernameInput = findViewById<TextView>(R.id.username_input)
        val username = usernameInput.text.toString()
        if (username.isNotEmpty()) {
            val user = hashMapOf(
                "username" to username,
                "email" to email,
                "password" to password
            )
            val db = Firebase.firestore
            return db.collection("steamandroid")
                .whereEqualTo("username", username)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        db.collection("steamandroid")
                            .add(user)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    } else {
                        Log.w(TAG, "Username already taken.")
                        Toast.makeText(
                            baseContext, "Username already taken.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        return null
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}


private fun updateUI(user: FirebaseUser?) {

}

private fun reload() {

}


