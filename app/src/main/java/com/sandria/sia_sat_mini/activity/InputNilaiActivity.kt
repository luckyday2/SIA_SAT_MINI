package com.sandria.sia_sat_mini.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.adapter.MahasiswaNilaiAdapter
import com.sandria.sia_sat_mini.databinding.ActivityInputNilaiBinding
import com.sandria.sia_sat_mini.model.NilaiMahasiswa
import com.sandria.sia_sat_mini.model.UserModel

class InputNilaiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputNilaiBinding
    private val db = FirebaseFirestore.getInstance()

    private val mahasiswaList = mutableListOf<UserModel>()
    private lateinit var adapter: MahasiswaNilaiAdapter

    private var matkulId: String = ""
    private var matkulNama: String = ""
    private var matkulKode: String = ""
    private var matkulDosen: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputNilaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        matkulKode = intent.getStringExtra("matkulKode") ?: ""
        matkulDosen = intent.getStringExtra("matkulDosen") ?: ""
        matkulId = intent.getStringExtra("matkulId") ?: ""
        matkulNama = intent.getStringExtra("matkulNama") ?: ""

        binding.tvNamaMatkul.text = "Input Nilai: $matkulNama"

        adapter = MahasiswaNilaiAdapter(mahasiswaList) { mahasiswa, nilai ->
            simpanNilai(mahasiswa, nilai)
        }

        binding.recyclerMahasiswa.layoutManager = LinearLayoutManager(this)
        binding.recyclerMahasiswa.adapter = adapter

        loadMahasiswa()
    }

    private fun loadMahasiswa() {
        db.collection("users")
            .whereEqualTo("role", "mahasiswa")
            .get()
            .addOnSuccessListener { result ->
                mahasiswaList.clear()
                for (doc in result) {
                    val mhs = doc.toObject(UserModel::class.java).copy(uid = doc.id)
                    mahasiswaList.add(mhs)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal ambil data mahasiswa", Toast.LENGTH_SHORT).show()
            }
    }

    private fun simpanNilai(mahasiswa: UserModel, nilai: Int) {
        val nilaiRef = db.collection("nilai_mahasiswa")
            .whereEqualTo("matkulId", matkulId)
            .whereEqualTo("mahasiswaId", mahasiswa.uid)

        nilaiRef.get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {

                    val nilaiBaru = NilaiMahasiswa(
                        matkulId = matkulId,
                        matkulNama = matkulNama,
                        mahasiswaId = mahasiswa.uid,
                        mahasiswaNama = mahasiswa.nama,
                        nilai = nilai,
                        kode = matkulKode,
                        dosen = matkulDosen
                    )
                    db.collection("nilai_mahasiswa").add(nilaiBaru)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Nilai berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal menambahkan nilai", Toast.LENGTH_SHORT).show()
                        }
                } else {

                    val docId = result.documents.first().id
                    db.collection("nilai_mahasiswa").document(docId)
                        .update(
                            mapOf(
                                "nilai" to nilai,
                                "kode" to matkulKode,
                                "dosen" to matkulDosen
                            )
                        )
                        .addOnSuccessListener {
                            Toast.makeText(this, "Nilai diperbarui", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Gagal update nilai", Toast.LENGTH_SHORT).show()
                        }

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal cek data sebelumnya", Toast.LENGTH_SHORT).show()
            }
    }

}
