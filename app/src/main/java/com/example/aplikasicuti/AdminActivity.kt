package com.example.aplikasicuti

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter // Import ini mungkin perlu manual
import com.google.firebase.database.*

class AdminActivity : AppCompatActivity() {

    private lateinit var rvCuti: RecyclerView
    private lateinit var listCuti: ArrayList<CutiData>
    private lateinit var adapter: CutiAdapter
    private lateinit var pieChart: PieChart // Variabel Chart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Inisialisasi Chart
        pieChart = findViewById(R.id.pieChart)
        setupChartTampilan()

        rvCuti = findViewById(R.id.rvCuti)
        rvCuti.layoutManager = LinearLayoutManager(this)

        listCuti = arrayListOf()

        adapter = CutiAdapter(listCuti) { dataCuti, pilihanStatus ->
            prosesTransaksiCuti(dataCuti, pilihanStatus)
        }
        rvCuti.adapter = adapter

        getDataFromFirebase()
    }

    private fun setupChartTampilan() {
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.centerText = "Statistik\nCuti"
        pieChart.setCenterTextSize(16f)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
    }

    private fun getDataFromFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("pengajuan_cuti")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listCuti.clear()

                // Variabel hitung-hitungan buat Grafik
                var countSetuju = 0
                var countTolak = 0
                var countPending = 0

                if (snapshot.exists()) {
                    for (dataSnap in snapshot.children) {
                        val cuti = dataSnap.getValue(CutiData::class.java)
                        if (cuti != null) {
                            listCuti.add(cuti)

                            // Cek Status buat dihitung
                            val st = cuti.status?.lowercase() ?: ""
                            if (st.contains("disetujui")) {
                                countSetuju++
                            } else if (st.contains("ditolak")) {
                                countTolak++
                            } else {
                                countPending++
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()

                    // UPDATE GRAFIK SETELAH DATA MASUK
                    updateGrafik(countSetuju, countTolak, countPending)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateGrafik(setuju: Int, tolak: Int, pending: Int) {
        val entries = ArrayList<PieEntry>()

        // Cuma masukkan ke grafik kalau datanya ada (biar ga nol)
        if (setuju > 0) entries.add(PieEntry(setuju.toFloat(), "Disetujui"))
        if (tolak > 0) entries.add(PieEntry(tolak.toFloat(), "Ditolak"))
        if (pending > 0) entries.add(PieEntry(pending.toFloat(), "Pending"))

        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#4CAF50")) // Hijau buat Setuju
        colors.add(Color.parseColor("#F44336")) // Merah buat Tolak
        colors.add(Color.parseColor("#FFC107")) // Kuning buat Pending

        val dataSet = PieDataSet(entries, "Status Cuti")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE

        val data = PieData(dataSet)
        // Format angka jadi persen (Opsional, kalau mau angka biasa hapus baris bawah ini)
        // data.setValueFormatter(PercentFormatter(pieChart))

        pieChart.data = data
        pieChart.invalidate() // Refresh gambar grafik
        pieChart.animateY(1400) // Animasi muter pas muncul
    }

    private fun prosesTransaksiCuti(data: CutiData, keputusan: String) {
        if (data.id == null) return

        val database = FirebaseDatabase.getInstance().getReference("pengajuan_cuti")
        var statusFinal = ""

        if (keputusan == "Disetujui") {
            val jatahAwal = 12
            val lamaCutiDiambil = data.lamaCuti?.toIntOrNull() ?: 0
            val sisaCuti = jatahAwal - lamaCutiDiambil
            statusFinal = "Disetujui (Sisa: $sisaCuti Hari)"
        } else {
            statusFinal = "Ditolak"
        }

        database.child(data.id).child("status").setValue(statusFinal)
            .addOnSuccessListener {
                Toast.makeText(this, "Status Update!", Toast.LENGTH_SHORT).show()
            }
    }
}