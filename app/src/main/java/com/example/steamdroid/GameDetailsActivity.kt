package com.example.steamdroid

import android.app.Activity
import android.os.Bundle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameDetailsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_details)
        //GameDetailsRequest().getGame(431960)
    }
}