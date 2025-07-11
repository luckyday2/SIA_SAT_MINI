package com.sandria.sia_sat_mini.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.MatkulDosenAdapter
import com.sandria.sia_sat_mini.databinding.ActivityDosenBinding
import com.sandria.sia_sat_mini.model.Matkul

class DosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDosenBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    private val matkulList = mutableListOf<Matkul>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val namaDosen = intent.getStringExtra("nama") ?: "Dosen"

        supportActionBar?.title = "Selamat Datang, $namaDosen"

        // Setup RecyclerView
        binding.recyclerDosen.layoutManager = LinearLayoutManager(this)
        val adapter = MatkulDosenAdapter(matkulList) { matkul ->
            val intent = Intent(this, InputNilaiActivity::class.java)
            intent.putExtra("matkulId", matkul.id)
            intent.putExtra("matkulNama", matkul.nama)
            intent.putExtra("matkulKode", matkul.kode)
            intent.putExtra("matkulDosen", matkul.dosen)
            startActivity(intent)
        }
        binding.recyclerDosen.adapter = adapter

        // Load mata kuliah
        db.collection("mata_kuliah")
            .whereEqualTo("dosen", namaDosen)
            .get()
            .addOnSuccessListener { documents ->
                matkulList.clear()
                for (doc in documents) {
                    val matkul = doc.toObject(Matkul::class.java).copy(id = doc.id)
                    matkulList.add(matkul)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }

        // Tombol logout
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah kamu yakin ingin logout?")
                .setPositiveButton("Ya") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

    }
}
