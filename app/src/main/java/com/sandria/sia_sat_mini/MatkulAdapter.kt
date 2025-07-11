package com.sandria.sia_sat_mini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sandria.sia_sat_mini.model.Matkul

class MatkulAdapter(
    private val matkulList: List<Matkul>,
    private val onDelete: (Matkul) -> Unit
) : RecyclerView.Adapter<MatkulAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaMatkul)
        val tvKode: TextView = itemView.findViewById(R.id.tvKodeMatkul)
        val tvDosen: TextView = itemView.findViewById(R.id.tvDosen)
        val tvRuangan: TextView = itemView.findViewById(R.id.tvRuangan)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_matkul, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matkul = matkulList[position]
        holder.tvNama.text = matkul.nama
        holder.tvKode.text = matkul.kode
        holder.tvRuangan.text = matkul.ruangan
        holder.tvDosen.text = "Dosen: ${matkul.dosen}"

        holder.btnDelete.setOnClickListener { onDelete(matkul) }
    }

    override fun getItemCount(): Int = matkulList.size
}

