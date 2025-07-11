package com.sandria.sia_sat_mini

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.adapter.AmbilMatkulAdapter
import com.sandria.sia_sat_mini.databinding.FragmentAmbilMatkulBinding
import com.sandria.sia_sat_mini.model.Matkul

class FragmentAmbilMatkul : Fragment() {
    private lateinit var binding: FragmentAmbilMatkulBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private val matkulList = mutableListOf<Matkul>()
    private lateinit var adapter: AmbilMatkulAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAmbilMatkulBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        adapter = AmbilMatkulAdapter(matkulList) { matkul ->
            ambilMatkul(matkul)
        }

        binding.recyclerAmbilMatkul.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAmbilMatkul.adapter = adapter

        loadMatkulTersedia()
    }

    private fun loadMatkulTersedia() {
        db.collection("mata_kuliah")
            .get()
            .addOnSuccessListener { result ->
                matkulList.clear()
                for (doc in result) {
                    val matkul = doc.toObject(Matkul::class.java).copy(id = doc.id)
                    matkulList.add(matkul)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal mengambil matkul", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ambilMatkul(matkul: Matkul) {
        val userId = auth.currentUser?.uid ?: return

        val data = hashMapOf(
            "mahasiswaId" to userId,
            "matkulId" to matkul.id,
            "matkulNama" to matkul.nama,
            "matkulKode" to matkul.kode
        )

        // Cek duplikat
        db.collection("ambil_matkul")
            .whereEqualTo("mahasiswaId", userId)
            .whereEqualTo("matkulId", matkul.id)
            .get()
            .addOnSuccessListener { docs ->
                if (docs.isEmpty) {
                    db.collection("ambil_matkul").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Berhasil mengambil ${matkul.nama}", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal mengambil matkul", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Mata kuliah ini sudah diambil", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
