package com.example.steamdroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class InitializationPasswordActivity : Activity() {
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initialization_password)
        val logInRedirect = findViewById<Button>(R.id.log_in_redirect)
        val sendEmail = findViewById<Button>(R.id.reset_action_button)
        val emailInput = findViewById<TextInputEditText>(R.id.email_input_reset)

        sendEmail.setOnClickListener {
            val email = emailInput.text.toString()
            if (email.isNotEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setBackgroundResource(R.drawable.border_red)
                    emailInput.error = resources.getString(R.string.email_error_regex)
                } else {
                    sendEmail(emailInput)
                }
            } else {
                if (email.isEmpty()) {
                    emailInput.setBackgroundResource(R.drawable.border_red)
                    emailInput.error = resources.getString(R.string.email_error_required)
                }
            }
        }
        logInRedirect.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()

    }

    private fun sendEmail(emailInput: TextInputEditText?) {
        val email = emailInput?.text.toString()
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result!!.signInMethods
                if (signInMethods!!.isEmpty()) {
                    emailInput?.setBackgroundResource(R.drawable.border_red)
                    emailInput?.error = resources.getString(R.string.email_error_regex)
                } else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            startActivity(Intent(this, SignInActivity::class.java))
                        }
                    }
                }
            }
        }

    }

}