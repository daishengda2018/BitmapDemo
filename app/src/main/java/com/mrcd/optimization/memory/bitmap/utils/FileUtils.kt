package com.mrcd.optimization.memory.bitmap.utils

import java.io.File
import java.io.FileOutputStream

/**
 *
 * Create by im_dsd 2020/8/13 22:06
 */
object FileUtils {
    fun write2File(byteArray: ByteArray, file: File) {
        val fileOutputStream = FileOutputStream(file)
        try {
            fileOutputStream.write(byteArray)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fileOutputStream.close()
        }
    }

    /**
     * 获取文件大小
     */
    fun fileSize(file: File, sizeType: BitmapUtils.SizeType = BitmapUtils.SizeType.KB): Long {
        return try {
            val size = file.length()

            when (sizeType) {
                BitmapUtils.SizeType.B -> size
                BitmapUtils.SizeType.KB -> size / 1024
                BitmapUtils.SizeType.MB -> size / 1024 / 1024
                BitmapUtils.SizeType.GB -> size / 1024 / 1024 / 1024
            }

        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}