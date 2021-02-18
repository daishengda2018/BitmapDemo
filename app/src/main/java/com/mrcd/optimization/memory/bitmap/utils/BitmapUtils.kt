package com.mrcd.optimization.memory.bitmap.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.util.Log
import androidx.annotation.Keep
import java.io.ByteArrayOutputStream

/**
 *
 * Create by im_dsd 2020/8/13 13:02
 */
@Keep
object BitmapUtils {
    enum class SizeType {
        B,
        KB,
        MB,
        GB
    }

    /**
     * 获取 Bitmap 体积
     */
    @SuppressLint("ObsoleteSdkInt")
    fun getMemorySize(bitmap: Bitmap, sizeType: SizeType = SizeType.KB): Int {
        val byte = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmap.allocationByteCount
        } else {
            bitmap.byteCount
        }
        return when (sizeType) {
            SizeType.B -> byte
            SizeType.KB -> byte / 1024
            SizeType.MB -> byte / 1024 / 1024
            SizeType.GB -> byte / 1024 / 1024 / 1024
        }
    }

    /**
     * 模拟 [getMemorySize] 计算方式
     * 占用内存 = 宽 * 高 * 每个像素点占用的字节数
     *
     * 以 ARGB_8888 为例， 每个像素点占用4个字节，
     * 所以 占用内存 = 宽 * 高 * 4
     **/
    fun calculateMemorySize(bitmap: Bitmap, sizeType: SizeType = SizeType.KB): Int {

        val pixels = bitmap.width * bitmap.height
        val bytes = when (bitmap.config) {
            Bitmap.Config.ALPHA_8 -> pixels * 1
            Bitmap.Config.ARGB_4444 -> pixels * 2
            Bitmap.Config.ARGB_8888 -> pixels * 4
            Bitmap.Config.RGB_565 -> pixels * 2
            else -> pixels * 4
        }

        return when (sizeType) {
            SizeType.B -> bytes
            SizeType.KB -> bytes / 1024
            SizeType.MB -> bytes / 1024 / 1024
            SizeType.GB -> bytes / 1024 / 1024 / 1024
        }
    }

    /**
     * 文件质量压缩，将传入的 [bitmap] 文件大小压缩 [targetSizeKB] 大小，单位是 KB
     * 注意：
     * 1. 这里面压缩的是文件大小，而不是内存大小
     * 2. 此方法不稳定，压缩后的结果可能大于传入的 bitmap 文件大小
     * 3. quality 表示的是质量比指定 50 就表示质量改为原来的 50%
     * 3. 不能使用 bitmap.compress 重复压缩会越压越大
     *
     * 压缩图片(质量压缩):
     * 1. 质量压缩后并不会减图片的像素，他是在保证像素的前提下改变图片的深度和透明度，来达到压缩图片的作用。
     * 2. 压缩之后的 bitmap 宽高、像素数量、像素占用的内存都不会改变，所有无法压缩内存大小
     * 3. 这里必须使用 jpeg 格式，因为 png 是无算压缩无法改变文件体积
     * 4. 因为图片的质量降低了，所以文件体积将会较少
     *
     * @param bitmap 将要处理的 bitmap
     * @param targetSizeKB 目标文件大小单位 kb
     * @param declineQuality 每次减少的质量
     */
    fun compressQuality(bitmap: Bitmap, quality: Int = 10): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * 第一种：质量压缩法
     * @param image     目标原图
     * @param maxSize   最大的图片大小
     * @return          bitmap，注意可以测试以下压缩前后bitmap的大小值
     */
    fun compressImage(image: Bitmap, maxSize: Long): ByteArray {
        val byteCount = image.byteCount
        Log.i("yc压缩图片", "压缩前大小$byteCount")
        val baos = ByteArrayOutputStream()
        // 把ByteArrayInputStream数据生成图片
        // 质量压缩方法，options的值是0-100，这里100表示原来图片的质量，不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var options = 90
        // 循环判断如果压缩后图片是否大于maxSize,大于继续压缩
        while (baos.toByteArray().size > maxSize) {
            // 重置baos即清空baos
            baos.reset()
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)
            // 每次都减少10，当为1的时候停止，options<10的时候，递减1
            options -= if (options == 1) {
                break
            } else if (options <= 10) {
                1
            } else {
                10
            }
        }
        return baos.toByteArray()
    }

    fun compressInSampleSize(byteArray: ByteArray, requestWidth: Int, requestHeight: Int): Bitmap {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            inPreferredConfig = Bitmap.Config.RGB_565
            val size = byteArray.size
            BitmapFactory.decodeByteArray(byteArray, 0, size, this)
            inSampleSize = calculateInSampleSize(this, requestWidth, requestHeight)
            inJustDecodeBounds = false;
            BitmapFactory.decodeByteArray(byteArray, 0, size, this)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


    /**
     * 压缩尺寸
     */
    fun compressSize(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(result)
        val rect = Rect(0, 0, width, height)
        canvas.drawBitmap(bitmap, null, rect, null)
        return result
    }

    fun compressScale(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}