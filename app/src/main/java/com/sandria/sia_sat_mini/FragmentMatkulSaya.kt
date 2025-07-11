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
import com.sandria.sia_sat_mini.databinding.FragmentMatkulSayaBinding
import com.sandria.sia_sat_mini.model.Matkul

class FragmentMatkulSaya : Fragment() {
    private lateinit var binding: FragmentMatkulSayaBinding
    private val db = FirebaseFirestore.getInstance()
    private val matkulList = mutableListOf<Matkul>()
    private lateinit var adapter: MatkulAdapter
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatkulSayaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil UID dari user saat ini
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Setup RecyclerView
        adapter = MatkulAdapter(matkulList) {}
        binding.recyclerMatkulSaya.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMatkulSaya.adapter = adapter

        loadMatkulSaya()
    }

    private fun loadMatkulSaya() {
        db.collection("ambil_matkul")
            .whereEqualTo("mahasiswaId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val matkulIdList = documents.mapNotNull { it.getString("matkulId") }

                if (matkulIdList.isEmpty()) {
                    binding.tvKosong.visibility = View.VISIBLE
                    matkulList.clear()
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                // Ambil data nilai yang sudah diberikan
                db.collection("nilai_mahasiswa")
                    .whereEqualTo("mahasiswaId", userId)
                    .get()
                    .addOnSuccessListener { nilaiDocs ->
                        val nilaiMap = mutableMapOf<String, Int>()
                        for (doc in nilaiDocs) {
                            val matkulId = doc.getString("matkulId") ?: continue
                            val nilai = doc.getLong("nilai")?.toInt() ?: continue
                            nilaiMap[matkulId] = nilai
                        }

                        // Ambil data mata kuliah yang sesuai
                        db.collection("mata_kuliah")
                            .whereIn(FieldPath.documentId(), matkulIdList)
                            .get()
                            .addOnSuccessListener { matkulDocs ->
                                matkulList.clear()
                                for (doc in matkulDocs) {
                                    val matkul = doc.toObject(Matkul::class.java).copy(id = doc.id)

                                    // Cek apakah ada nilai untuk matkul ini
                                    val nilai = nilaiMap[matkul.id]
                                    matkul.nilai = nilai // ← Pastikan model Matkul punya `var nilai: Int?`

                                    matkulList.add(matkul)
                                }

                                binding.tvKosong.visibility = View.GONE
                                adapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Gagal mengambil data matkul", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Gagal mengambil nilai", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal mengambil daftar matkul saya", Toast.LENGTH_SHORT).show()
            }
    }
}
