package com.mrcd.optimization.memory.bitmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.mrcd.optimization.memory.R
import kotlinx.android.synthetic.main.activity_subsampling.*

/**
 * 大图预览
 * Create by im_dsd 2020/8/13 21:16
 */
class BigBitmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subsampling)
        initWeight()
    }

    private fun initWeight() {
        ivSubSamplingView.setImage(ImageSource.asset("qmsht.jpg"))
    }
}