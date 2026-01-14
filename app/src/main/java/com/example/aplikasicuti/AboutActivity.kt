package com.example.aplikasicuti

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Judul di atas layar
        supportActionBar?.title = "Tentang Aplikasi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Biar tombol back di pojok kiri atas jalan
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}