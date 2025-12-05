package com.example.myapplication.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object FileUtils {

    /**
     * 通用下载方法
     * 注意：需要在 IO 线程调用
     */
    fun downloadFile(context: Context, url: String, fileName: String, headers: Map<String, String> = emptyMap()): File {
        val client = OkHttpClient()
        val requestBuilder = Request.Builder().url(url)

        headers.forEach { (k, v) ->
            requestBuilder.addHeader(k, v)
        }

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) throw Exception("下载失败: ${response.code}")

        val file = File(context.cacheDir, fileName)
        response.body?.source()?.use { source ->
            file.sink().buffer().use { sink ->
                sink.writeAll(source)
            }
        }
        return file
    }

    /**
     * 导出文件到系统的 Downloads 文件夹
     */
    fun exportToDownloads(context: Context, sourceFile: File): String {
        val fileName = "BiliAudio_${System.currentTimeMillis()}.mp3"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/BiliExtract")
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("无法创建 MediaStore 记录")

            resolver.openOutputStream(uri).use { outputStream ->
                FileInputStream(sourceFile).use { inputStream ->
                    inputStream.copyTo(outputStream!!)
                }
            }
            return "下载/BiliExtract/$fileName"
        } else {
            // 旧版 Android 处理
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val myDir = File(downloadDir, "BiliExtract")
            if (!myDir.exists()) myDir.mkdirs()

            val destFile = File(myDir, fileName)
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            return destFile.absolutePath
        }
    }
}