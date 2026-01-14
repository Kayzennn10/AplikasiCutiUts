package com.example.aplikasicuti

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView // Tambahan import untuk ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Panggil fungsi Splash Screen PALING PERTAMA
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- BAGIAN 1: INISIALISASI ---
        val etNama = findViewById<EditText>(R.id.etNama)
        val etLamaCuti = findViewById<EditText>(R.id.etLamaCuti)
        val etAlasan = findViewById<EditText>(R.id.etAlasan)
        val btnKirim = findViewById<Button>(R.id.btnKirim)
        val btnMasukAdmin = findViewById<Button>(R.id.btnMasukAdmin)
        val tvInfoLibur = findViewById<TextView>(R.id.tvInfoLibur)

        // TAMBAHAN: Inisialisasi Tombol Info (About)
        val btnInfo = findViewById<ImageView>(R.id.btnInfo)

        val database = FirebaseDatabase.getInstance().getReference("pengajuan_cuti")

        // --- BAGIAN 2: PANGGIL API LIBUR (SubCPMK 4) ---
        ambilInfoLibur(tvInfoLibur)

        // --- BAGIAN 3: TOMBOL KIRIM CUTI ---
        btnKirim.setOnClickListener {
            val nama = etNama.text.toString()
            val lama = etLamaCuti.text.toString()
            val alasan = etAlasan.text.toString()

            if (nama.isEmpty() || lama.isEmpty() || alasan.isEmpty()) {
                Toast.makeText(this, "Isi semua data dulu ya!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idUnik = database.push().key
            val dataCuti = CutiData(idUnik, nama, lama, alasan, "Pending")

            if (idUnik != null) {
                database.child(idUnik).setValue(dataCuti).addOnSuccessListener {
                    Toast.makeText(this, "Berhasil Ajukan Cuti!", Toast.LENGTH_SHORT).show()
                    etNama.text.clear()
                    etLamaCuti.text.clear()
                    etAlasan.text.clear()
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // --- BAGIAN 4: TOMBOL PINDAH ADMIN ---
        btnMasukAdmin.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

        // --- BAGIAN 5: TOMBOL INFO / ABOUT (Syarat UAS No. 2) ---
        btnInfo.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    // Fungsi khusus buat ambil API
    private fun ambilInfoLibur(textView: TextView) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://date.nager.at/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(HolidayApi::class.java)

        apiService.getNextHoliday("ID").enqueue(object : Callback<List<HolidayData>> {
            override fun onResponse(call: Call<List<HolidayData>>, response: Response<List<HolidayData>>) {
                if (response.isSuccessful) {
                    val liburList = response.body()
                    if (liburList != null && liburList.isNotEmpty()) {
                        val liburBerikutnya = liburList[0]
                        textView.text = "📅 Libur Berikutnya: ${liburBerikutnya.localName} (${liburBerikutnya.date})"
                    } else {
                        textView.text = "Tidak ada info libur dekat."
                    }
                }
            }

            override fun onFailure(call: Call<List<HolidayData>>, t: Throwable) {
                textView.text = "Gagal memuat info libur (Cek Internet)"
            }
        })
    }
}

// --- CLASS MODEL DATA ---
data class CutiData(
    val id: String? = null,
    val nama: String? = null,
    val lamaCuti: String? = null,
    val alasan: String? = null,
    val status: String? = null
)

data class HolidayData(
    val date: String,
    val localName: String,
    val name: String
)

interface HolidayApi {
    @GET("api/v3/NextPublicHolidays/{countryCode}")
    fun getNextHoliday(@retrofit2.http.Path("countryCode") countryCode: String = "ID"): Call<List<HolidayData>>
}