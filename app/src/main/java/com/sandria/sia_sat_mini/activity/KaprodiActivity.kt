package com.sandria.sia_sat_mini.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.FragmentDaftarMatkul
import com.sandria.sia_sat_mini.R
import com.sandria.sia_sat_mini.databinding.ActivityKaprodiBinding

class KaprodiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKaprodiBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprodiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val namaUser = intent.getStringExtra("nama") ?: "Kaprodi"
        Toast.makeText(this, "Selamat datang, $namaUser", Toast.LENGTH_SHORT).show()

        setupSpinner()
        loadDosenList()

        binding.btnSimpan.setOnClickListener {
            val kode = binding.etKodeMatkul.text.toString().trim()
            val nama = binding.etNamaMatkul.text.toString().trim()
            val sks = binding.etSKS.text.toString().trim()
            val ruangan = binding.etRuangan.text.toString().trim()
            val dosen = binding.spinnerDosen.selectedItem.toString()

            if (dosen == "Pilih Dosen Pengampu") {
                Toast.makeText(this, "Silakan pilih dosen pengampu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (kode.isEmpty() || nama.isEmpty() || sks.isEmpty() || ruangan.isEmpty()) {
                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sksInt = sks.toIntOrNull()
            if (sksInt == null) {
                Toast.makeText(this, "Jumlah SKS harus berupa angka", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val matkulData = hashMapOf(
                "kode" to kode,
                "nama" to nama,
                "sks" to sksInt,
                "ruangan" to ruangan,
                "dosen" to dosen
            )

            db.collection("mata_kuliah").add(matkulData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Mata kuliah berhasil disimpan", Toast.LENGTH_SHORT).show()
                    clearForm()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal simpan: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

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

        binding.btnDaftarMatkul.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.frameFragment, FragmentDaftarMatkul())
                addToBackStack(null)
            }
        }
    }

    private fun setupSpinner() {
        val defaultList = listOf("Pilih Dosen Pengampu")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, defaultList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDosen.adapter = adapter
    }

    private fun loadDosenList() {
        db.collection("users")
            .whereEqualTo("role", "dosen")
            .get()
            .addOnSuccessListener { documents ->
                val dosenList = mutableListOf("Pilih Dosen Pengampu")
                for (doc in documents) {
                    val nama = doc.getString("nama")
                    if (nama != null) {
                        dosenList.add(nama)
                        Log.d("KaprodiActivity", "Dosen ditemukan: $nama")
                    }
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dosenList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerDosen.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat dosen", Toast.LENGTH_SHORT).show()
                Log.e("KaprodiActivity", "Error memuat dosen: ${it.message}")
            }
    }

    private fun clearForm() {
        binding.etKodeMatkul.text.clear()
        binding.etNamaMatkul.text.clear()
        binding.etSKS.text.clear()
        binding.etRuangan.text.clear()
        binding.spinnerDosen.setSelection(0)
    }
}