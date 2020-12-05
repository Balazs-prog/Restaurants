package com.example.restaurants

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpNavigation()
    }
    private fun setUpNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bttm_nav)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment) as NavHostFragment?
        NavigationUI.setupWithNavController(bottomNavigationView,
            navHostFragment!!.navController)

    }

    fun setBottomNavInvisible(){
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bttm_nav)
        bottomNavigationView.visibility = View.INVISIBLE
    }
    fun setBottomNavVisible(){
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bttm_nav)
        bottomNavigationView.visibility = View.VISIBLE
    }
}