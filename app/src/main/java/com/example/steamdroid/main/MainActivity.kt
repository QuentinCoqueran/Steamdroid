package com.example.steamdroid.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.steamdroid.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

