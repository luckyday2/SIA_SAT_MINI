package com.sandria.sia_sat_mini.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sandria.sia_sat_mini.R
import com.sandria.sia_sat_mini.model.Matkul

class AmbilMatkulAdapter(
    private val list: List<Matkul>,
    private val onAmbil: (Matkul) -> Unit
) : RecyclerView.Adapter<AmbilMatkulAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama = itemView.findViewById<TextView>(R.id.tvNamaMatkul)
        val kode = itemView.findViewById<TextView>(R.id.tvKodeMatkul)
        val dosen = itemView.findViewById<TextView>(R.id.tvDosen)
        val btnAmbil = itemView.findViewById<Button>(R.id.btnAmbil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ambil_matkul, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.nama.text = item.nama
        holder.kode.text = item.kode
        holder.dosen.text = "Dosen: ${item.dosen}"
        holder.btnAmbil.setOnClickListener { onAmbil(item) }
    }

    override fun getItemCount(): Int = list.size
}
