package com.sandria.sia_sat_mini.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.sandria.sia_sat_mini.FragmentAmbilMatkul
import com.sandria.sia_sat_mini.FragmentLihatNilai
import com.sandria.sia_sat_mini.FragmentMatkulSaya
import com.sandria.sia_sat_mini.R
import com.sandria.sia_sat_mini.databinding.ActivityMahasiswaBinding

class MahasiswaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMahasiswaBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val nama = intent.getStringExtra("nama") ?: "Mahasiswa"
        supportActionBar?.title = "Selamat Datang, $nama"

        binding.btnAmbilMatkul.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.frameMahasiswa, FragmentAmbilMatkul())

            }
        }

        binding.btnMatkulSaya.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameMahasiswa, FragmentMatkulSaya())
                .commit()
        }

        binding.btnLihatNilai.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.frameMahasiswa, FragmentLihatNilai())
                addToBackStack(null)
            }
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    auth.signOut()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
}
