package com.example.steamdroid

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class InitializationPasswordFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.initialization_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(view)
        val logInRedirect = view.findViewById<Button>(R.id.log_in_redirect)
        val sendEmail = view.findViewById<Button>(R.id.reset_action_button)
        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input_reset)

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
            navController.navigate(R.id.action_initializationPasswordFragment_to_signInFragment)
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
                            navController.navigate(R.id.action_initializationPasswordFragment_to_signInFragment)
                        }
                    }
                }
            }
        }
    }
}