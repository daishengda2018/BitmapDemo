package com.mrcd.optimization.memory.bitmap

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import com.mrcd.optimization.memory.R
import com.mrcd.optimization.memory.bitmap.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_bitmap.*
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference

/**
 * Bitmap 相关 Demo
 * Create by im_dsd 2020/8/12 18:57
 */
class BitmapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)
        // 从 assets 中读取图片
        btnFormAssets.setOnClickListener {
            val bitmap = loadBitmapFormAssets(config = Bitmap.Config.ARGB_8888)
            showInfo(bitmap)
        }
        // 使用 RGB_565 读取
        btnFormAssets2.setOnClickListener {
            showInfo(loadBitmapFormAssets())
        }
        // 从资源文件路径读取
        btnFromDrawable.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.pic_big)
            showInfo(bitmap)
        }
        // 质量压缩
        btnQualityCompress.setOnClickListener {
            object : Compress(this) {
                override fun onCompress(
                    bitmap: Bitmap, byteArray: ByteArray,
                    options: BitmapFactory.Options
                ): Bitmap {
                    val compressByteArray = BitmapUtils.compressQuality(bitmap)
                    val compressedBitmap = BitmapFactory.decodeByteArray(
                        compressByteArray, 0,
                        compressByteArray.size, options
                    )
                    // 展示结果
                    showCompressInfo(bitmap, byteArray, compressedBitmap, compressByteArray)
                    return compressedBitmap;
                }
            }.compress()
        }
        // 采样率压缩
        btnInSampleCompress.setOnClickListener {
            object : Compress(this) {
                override fun onCompress(
                    bitmap: Bitmap, byteArray: ByteArray,
                    options: BitmapFactory.Options
                ): Bitmap {
                    val compressedBitmap = BitmapUtils.compressInSampleSize(byteArray, 300, 300)
                    val compressOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, compressOutputStream)
                    showCompressInfo(bitmap, byteArray, compressedBitmap, compressOutputStream.toByteArray())
                    return compressedBitmap;
                }
            }.compress()
        }
        // 压缩
        btnScaleCompress1.setOnClickListener {
            object : Compress(this) {
                override fun onCompress(
                    bitmap: Bitmap, byteArray: ByteArray,
                    options: BitmapFactory.Options
                ): Bitmap {
                    val compressedBitmap = BitmapUtils.compressSize(bitmap, 300, 300)
                    val compressOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, compressOutputStream)
                    // 展示结果
                    showCompressInfo(bitmap, byteArray, compressedBitmap, compressOutputStream.toByteArray())
                    return compressedBitmap;
                }
            }.compress()
        }
        // 压缩
        btnScaleCompress2.setOnClickListener {
            object : Compress(this) {
                override fun onCompress(
                    bitmap: Bitmap, byteArray: ByteArray,
                    options: BitmapFactory.Options
                ): Bitmap {
                    val compressedBitmap = BitmapUtils.compressScale(bitmap, 300, 300)
                    val compressOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, compressOutputStream)
                    // 展示结果
                    showCompressInfo(bitmap, byteArray, compressedBitmap, compressOutputStream.toByteArray())
                    return compressedBitmap;
                }
            }.compress()
        }

        btnShowBigBitmap.setOnClickListener {
            startActivity(Intent(this, BigBitmapActivity::class.java))
        }
    }

    private fun loadBitmapFormAssets(
        fileName: String = "pic_big.jpg",
        config: Bitmap.Config = Bitmap.Config.RGB_565
    ): Bitmap {
        val byteArray = assets.open(fileName).readBytes()
        return loadBitmapFormByteArray(byteArray, config)
    }

    private fun loadBitmapFormByteArray(
        byteArray: ByteArray,
        config: Bitmap.Config = Bitmap.Config.RGB_565
    ): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = config
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
    }


    @SuppressLint("SetTextI18n")
    private fun showInfo(bitmap: Bitmap) {
        ivPic.setImageBitmap(bitmap)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        val bitmapInfo = "图像宽高 ： ${bitmap.width} * ${bitmap.height} \n" +
                "图片格式： ${bitmap.config.name}\n" +
                "占用内存大小： ${BitmapUtils.getMemorySize(bitmap)} kb \n" +
                "计算占用内存大小： ${BitmapUtils.calculateMemorySize(bitmap)} kb \n" +
                "bitmap.density : ${bitmap.density} \n" +
                "屏幕的density : ${resources.displayMetrics.densityDpi} \n" +
                "Bitmap转换成文件大小 ：${baos.toByteArray().size / 1024} bk"

        tvInfo.text = bitmapInfo
        Log.e("BitmapActivity", bitmapInfo)
        baos.close()
    }

    @SuppressLint("SetTextI18n")
    private fun showCompressInfo(
        rawBitmap: Bitmap,
        rewByteArray: ByteArray,
        compressedBitmap: Bitmap,
        compressByteArray: ByteArray
    ) {
        val bitmapInfo = "压缩前图像宽高 ： ${rawBitmap.width} * ${rawBitmap.height} \n" +
                "图片格式： ${rawBitmap.config.name}\n" +
                "占用内存大小： ${BitmapUtils.getMemorySize(rawBitmap)} kb \n" +
                "计算占用内存大小： ${BitmapUtils.calculateMemorySize(rawBitmap)} kb \n" +
                "bitmap.density : ${rawBitmap.density} \n" +
                "文件大小: ${rewByteArray.size / 1024} kb \n" +
                "\n" +
                "压缩后图像宽高 ： ${compressedBitmap.width} * ${compressedBitmap.height} \n" +
                "图片格式： ${compressedBitmap.config.name}\n" +
                "占用内存大小： ${BitmapUtils.getMemorySize(compressedBitmap)} kb \n" +
                "计算占用内存大小： ${BitmapUtils.calculateMemorySize(compressedBitmap)} kb \n" +
                "bitmap.density : ${compressedBitmap.density} \n" +
                "文件大小: ${compressByteArray.size / 1024} kb "

        tvInfo.text = bitmapInfo
    }

    private abstract class Compress(context: BitmapActivity, val fileName: String = "pic_big.jpg") {
        val mContextRefer: WeakReference<BitmapActivity> = WeakReference(context)

        fun compress() = mContextRefer.get()?.run {
            val byteArray = assets.open(fileName).readBytes()
            val bitmap = loadBitmapFormByteArray(byteArray)
            ivPic?.setImageBitmap(bitmap)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.RGB_565
            val compressedBitmap = onCompress(bitmap, byteArray, options)
            ivPic?.postDelayed({
                ivPic.setImageBitmap(compressedBitmap) // 5s 后展示压缩过的图片
            }, 3000)
        }

        abstract fun onCompress(bitmap: Bitmap, byteArray: ByteArray, options: BitmapFactory.Options): Bitmap
    }

}
