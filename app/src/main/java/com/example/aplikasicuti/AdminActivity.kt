package com.example.aplikasicuti

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AdminActivity : AppCompatActivity() {

    private lateinit var rvCuti: RecyclerView
    private lateinit var listCuti: ArrayList<CutiData>
    private lateinit var adapter: CutiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        rvCuti = findViewById(R.id.rvCuti)
        rvCuti.layoutManager = LinearLayoutManager(this)

        listCuti = arrayListOf()

        // UPDATE DI SINI: Kita kirim seluruh data (it) ke fungsi updateStatus
        adapter = CutiAdapter(listCuti) { dataCuti, pilihanStatus ->
            prosesTransaksiCuti(dataCuti, pilihanStatus)
        }
        rvCuti.adapter = adapter

        getDataFromFirebase()
    }

    private fun getDataFromFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("pengajuan_cuti")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listCuti.clear()
                if (snapshot.exists()) {
                    for (dataSnap in snapshot.children) {
                        val cuti = dataSnap.getValue(CutiData::class.java)
                        if (cuti != null) {
                            listCuti.add(cuti)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- BAGIAN INI YANG MERUPAKAN "TRANSAKSI & PERHITUNGAN" ---
    private fun prosesTransaksiCuti(data: CutiData, keputusan: String) {
        if (data.id == null) return

        val database = FirebaseDatabase.getInstance().getReference("pengajuan_cuti")
        var statusFinal = ""

        if (keputusan == "Disetujui") {
            // 1. FITUR PERHITUNGAN (ARITMATIKA)
            // Kita anggap jatah cuti awal setahun adalah 12 hari
            val jatahAwal = 12
            // Ambil lama cuti yang diajukan (ubah string jadi angka/integer)
            val lamaCutiDiambil = data.lamaCuti?.toIntOrNull() ?: 0

            // Rumus: Sisa = Jatah - Ambil
            val sisaCuti = jatahAwal - lamaCutiDiambil

            // 2. Simpan hasil hitungan ke dalam Status
            statusFinal = "Disetujui (Sisa: $sisaCuti Hari)"
        } else {
            // Kalau ditolak, tidak ada pengurangan
            statusFinal = "Ditolak"
        }

        // 3. UPDATE DATABASE (TRANSAKSI)
        database.child(data.id).child("status").setValue(statusFinal)
            .addOnSuccessListener {
                Toast.makeText(this, "Transaksi Berhasil: $statusFinal", Toast.LENGTH_SHORT).show()
            }
    }
}