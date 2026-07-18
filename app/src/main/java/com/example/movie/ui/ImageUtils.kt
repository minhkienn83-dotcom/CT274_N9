package com.example.movie.ui

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Lưu hình ảnh từ Uri (thư viện) vào bộ nhớ riêng của ứng dụng
 * Trả về đường dẫn tuyệt đối của tệp ảnh đã lưu
 */
fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    val fileName = "movie_${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, fileName)
    
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
