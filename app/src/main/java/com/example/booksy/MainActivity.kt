package com.example.booksy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.booksy.R.layout.activity_main

// Se agregó "class MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establece la interfaz visual
        setContentView(activity_main)
    }
}