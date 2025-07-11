package com.sandria.sia_sat_mini.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sandria.sia_sat_mini.R
import com.sandria.sia_sat_mini.model.NilaiMahasiswa

class NilaiAdapter(private val list: List<NilaiMahasiswa>) :
    RecyclerView.Adapter<NilaiAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMatkul: TextView = view.findViewById(R.id.tvMatkul)
        val tvKode: TextView = view.findViewById(R.id.tvKode)
        val tvDosen: TextView = view.findViewById(R.id.tvDosen)
        val tvNilai: TextView = view.findViewById(R.id.tvNilai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nilai_mahasiswa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvMatkul.text = item.matkulNama
        holder.tvKode.text = "Kode: ${item.kode}"
        holder.tvDosen.text = "Dosen: ${item.dosen}"
        holder.tvNilai.text = "Nilai: ${item.nilai}"
    }

    override fun getItemCount(): Int = list.size
}

