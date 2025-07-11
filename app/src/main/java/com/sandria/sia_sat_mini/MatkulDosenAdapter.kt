package com.sandria.sia_sat_mini

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sandria.sia_sat_mini.model.Matkul

class MatkulDosenAdapter(
    private val list: List<Matkul>,
    private val onItemClick: (Matkul) -> Unit
) : RecyclerView.Adapter<MatkulDosenAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaMatkulDosen)
        val tvKode: TextView = itemView.findViewById(R.id.tvKodeMatkulDosen)
        val tvRuangan: TextView = itemView.findViewById(R.id.tvRuanganMatkulDosen)

        fun bind(matkul: Matkul) {
            tvNama.text = matkul.nama
            tvKode.text = "Kode: ${matkul.kode}"
            tvRuangan.text = "Ruangan: ${matkul.ruangan}"

            itemView.setOnClickListener {
                onItemClick(matkul)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_matkul_dosen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matkul = list[position]
        holder.bind(matkul)
    }

    override fun getItemCount(): Int = list.size
}