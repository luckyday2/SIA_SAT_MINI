package com.sandria.sia_sat_mini.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sandria.sia_sat_mini.R
import com.sandria.sia_sat_mini.model.UserModel

class MahasiswaNilaiAdapter(
    private val mahasiswaList: List<UserModel>,
    private val onSaveClick: (UserModel, Int) -> Unit
) : RecyclerView.Adapter<MahasiswaNilaiAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val etNilai: EditText = itemView.findViewById(R.id.etNilai)
        val btnSimpan: Button = itemView.findViewById(R.id.btnSimpan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_input_nilai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        holder.tvNama.text = mahasiswa.nama
        holder.btnSimpan.setOnClickListener {
            val nilai = holder.etNilai.text.toString().toIntOrNull()
            if (nilai != null) {
                onSaveClick(mahasiswa, nilai)
            } else {
                Toast.makeText(holder.itemView.context, "Nilai tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = mahasiswaList.size
}