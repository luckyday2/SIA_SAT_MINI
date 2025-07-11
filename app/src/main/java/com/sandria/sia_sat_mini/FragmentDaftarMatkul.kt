package com.sandria.sia_sat_mini

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sandria.sia_sat_mini.databinding.FragmentDaftarMatkulBinding
import com.sandria.sia_sat_mini.model.Matkul

class FragmentDaftarMatkul : Fragment() {
    private lateinit var binding: FragmentDaftarMatkulBinding
    private val db = FirebaseFirestore.getInstance()
    private val matkulList = mutableListOf<Matkul>()
    private lateinit var adapter: MatkulAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaftarMatkulBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MatkulAdapter(matkulList) {
            matkul -> confirmDelete(matkul)
        }

        binding.recyclerMatkul.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMatkul.adapter = adapter

        loadMatkul()
    }

    private fun loadMatkul() {
        db.collection("mata_kuliah").get()
            .addOnSuccessListener { documents ->
                matkulList.clear()
                for (doc in documents) {
                    val matkul = doc.toObject(Matkul::class.java).copy(id = doc.id)
                    matkulList.add(matkul)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal mengambil data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmDelete(matkul: Matkul) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Mata Kuliah")
            .setMessage("Yakin ingin menghapus ${matkul.nama}?")
            .setPositiveButton("Hapus") { _, _ -> deleteMatkul(matkul) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteMatkul(matkul: Matkul) {
        db.collection("mata_kuliah").document(matkul.id).delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadMatkul()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal hapus: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}