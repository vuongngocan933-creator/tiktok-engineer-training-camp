package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BuildConfig
import com.example.myapplication.service.AudioService
import com.example.myapplication.service.BilibiliService
import com.example.myapplication.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class TranscribeViewModel(application: Application) : AndroidViewModel(application) {

    private val _statusLog = MutableStateFlow("准备就绪")
    val statusLog = _statusLog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val context = getApplication<Application>().applicationContext

    // 初始化 Service
    private val audioService = AudioService(apiKey = BuildConfig.DASHSCOPE_API_KEY)

    fun processBilibiliLink(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _statusLog.value = "正在分析链接: $url..."

            try {
                // 1. 提取 BV 号
                val bvid = BilibiliService.extractBvid(url)
                if (bvid == null) {
                    updateStatus("错误：无效的 BV 号")
                    return@launch
                }

                // 2. 获取下载链接
                updateStatus("正在获取音频流地址 (BV: $bvid)...")
                val audioUrl = BilibiliService.fetchAudioUrl(bvid)

                // 3. 下载文件
                updateStatus("正在下载音频...")
                val headers = mapOf(
                    "Referer" to "https://www.bilibili.com",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64)..."
                )
                val rawFile = FileUtils.downloadFile(context, audioUrl, "temp_audio.m4s", headers)

                // 4. 导出备份 (可选)
                val savedPath = FileUtils.exportToDownloads(context, rawFile)
                updateStatus("音频已保存至: $savedPath")

                // 5. 转码
                updateStatus("正在转码为 16k mp3...")
                val mp3File = File(context.cacheDir, "temp_audio.mp3")
                val convertSuccess = audioService.convertToMp3(rawFile, mp3File)

                if (!convertSuccess) {
                    updateStatus("错误：FFmpeg 转码失败")
                    return@launch
                }

                // 6. 阿里云识别
                updateStatus("正在提交阿里云识别...")
                val resultText = audioService.transcribe(mp3File)

                updateStatus("识别完成！\n\n$resultText")

            } catch (e: Exception) {
                e.printStackTrace()
                updateStatus("流程出错: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateStatus(msg: String) {
        _statusLog.value = msg
    }
}