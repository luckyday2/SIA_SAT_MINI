package com.sandria.sia_sat_mini.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sandria.sia_sat_mini.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val role = intent.getStringExtra("role")
        val name = intent.getStringExtra("nama")

        binding.tvWelcome.text = "Welcome, $name!\nRole: $role"
    }
}
