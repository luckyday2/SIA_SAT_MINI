package com.sandria.sia_sat_mini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.adapter.NilaiAdapter
import com.sandria.sia_sat_mini.databinding.FragmentLihatNilaiBinding
import com.sandria.sia_sat_mini.model.NilaiMahasiswa

class FragmentLihatNilai : Fragment() {
    private lateinit var binding: FragmentLihatNilaiBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val nilaiList = mutableListOf<NilaiMahasiswa>()
    private lateinit var adapter: NilaiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLihatNilaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NilaiAdapter(nilaiList)
        binding.recyclerNilai.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNilai.adapter = adapter

        loadNilai()
    }

    private fun loadNilai() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("nilai_mahasiswa")
            .whereEqualTo("mahasiswaId", uid)
            .get()
            .addOnSuccessListener { nilaiDocs ->
                val mapNilai = mutableMapOf<String, Int>()
                val matkulIdList = mutableListOf<String>()

                for (doc in nilaiDocs) {
                    val matkulId = doc.getString("matkulId") ?: continue
                    val nilai = doc.getLong("nilai")?.toInt() ?: continue

                    mapNilai[matkulId] = nilai
                    matkulIdList.add(matkulId)
                }

                if (matkulIdList.isEmpty()) {
                    binding.tvKosongNilai.visibility = View.VISIBLE
                    return@addOnSuccessListener
                }

                db.collection("mata_kuliah")
                    .whereIn(FieldPath.documentId(), matkulIdList)
                    .get()
                    .addOnSuccessListener { matkulDocs ->
                        nilaiList.clear()

                        for (doc in matkulDocs) {
                            val id = doc.id
                            val nama = doc.getString("nama") ?: "-"
                            val kode = doc.getString("kode") ?: "-"
                            val dosen = doc.getString("dosen") ?: "-"
                            val nilai = mapNilai[id] ?: 0

                            nilaiList.add(
                                NilaiMahasiswa(
                                    matkulNama = nama,
                                    kode = kode,
                                    dosen = dosen,
                                    nilai = nilai
                                )
                            )
                        }

                        binding.tvKosongNilai.visibility =
                            if (nilaiList.isEmpty()) View.VISIBLE else View.GONE

                        adapter.notifyDataSetChanged()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal memuat data nilai", Toast.LENGTH_SHORT).show()
            }
    }
}
