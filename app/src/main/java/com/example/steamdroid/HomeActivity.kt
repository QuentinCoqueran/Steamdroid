package com.example.steamdroid

import android.app.Activity
import android.os.Bundle

class HomeActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        //call class BestsellersApiSteam method getResponse()
        BestsellersApiSteam().getResponse()
    }
}
