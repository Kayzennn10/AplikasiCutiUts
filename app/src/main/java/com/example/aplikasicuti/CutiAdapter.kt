package com.example.aplikasicuti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CutiAdapter(
    private val listCuti: ArrayList<CutiData>,
    private val tombolDiklik: (CutiData, String) -> Unit // Ini fungsi buat lapor ke AdminActivity
) : RecyclerView.Adapter<CutiAdapter.CutiViewHolder>() {

    class CutiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDetail: TextView = itemView.findViewById(R.id.tvDetail)
        val btnSetuju: Button = itemView.findViewById(R.id.btnSetuju)
        val btnTolak: Button = itemView.findViewById(R.id.btnTolak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CutiViewHolder {
        // Ini tugasnya mengambil layout "item_cuti.xml"
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cuti, parent, false)
        return CutiViewHolder(view)
    }

    override fun onBindViewHolder(holder: CutiViewHolder, position: Int) {
        val data = listCuti[position]

        // 1. Tampilkan Data ke Layar
        holder.tvNama.text = data.nama
        holder.tvDetail.text = "${data.alasan} (${data.lamaCuti} Hari)"
        holder.tvStatus.text = data.status

        // 2. Ubah warna status biar cantik
        if (data.status == "Disetujui") {
            holder.tvStatus.setTextColor(0xFF4CAF50.toInt()) // Hijau
        } else if (data.status == "Ditolak") {
            holder.tvStatus.setTextColor(0xFFF44336.toInt()) // Merah
        }

        // 3. Aksi Tombol
        holder.btnSetuju.setOnClickListener {
            tombolDiklik(data, "Disetujui")
        }

        holder.btnTolak.setOnClickListener {
            tombolDiklik(data, "Ditolak")
        }
    }

    override fun getItemCount(): Int {
        return listCuti.size
    }
}