package com.example.uday.diaster_management

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.uday.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class disastermap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_disastermap)
        replacefragment(dsmap())

        findViewById<BottomNavigationView>(R.id.bottom_nav_bar).setOnItemSelectedListener{ item ->
            when(item.itemId){
                R.id.bottom_map -> replacefragment(dsmap())
                R.id.bottom_town -> replacefragment(dtown())
                R.id.bottom_profile -> replacefragment(dprofile())
                else -> {
                }
            }
        return@setOnItemSelectedListener true
    }
    }

    private fun replacefragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
      fragmentTransaction.replace(R.id.frame_layout,fragment).commit()
    }
}