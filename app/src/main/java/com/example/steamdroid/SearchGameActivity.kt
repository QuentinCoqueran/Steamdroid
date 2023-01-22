package com.example.steamdroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class SearchGameActivity : Activity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_game)

        //GameDetailsRequest().getGame(750)

        val cross = findViewById<ImageView>(R.id.white_cross);
        /*
        cross.setOnClickListener { finish()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }*/

        val searchInput = findViewById<EditText>(R.id.search_input)

        if(searchInput.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }

        searchInput.setOnKeyListener { _, keyCode, event ->
                // If the event is a key-down event on the "enter" button
                if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    val search = searchInput.text.toString()

                    SearchGameRequest().searchGame(search)

                }

            false

        }




    }

}