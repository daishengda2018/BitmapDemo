package com.mrcd.optimization.memory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrcd.optimization.memory.bitmap.BitmapActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Bitmap 相关
        about_bitmap_btn.setOnClickListener {
            startActivity(Intent(this, BitmapActivity::class.java))
        }
    }
}