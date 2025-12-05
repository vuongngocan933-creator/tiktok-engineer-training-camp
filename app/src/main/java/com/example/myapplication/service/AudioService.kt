package com.example.myapplication.service

import android.content.Context
import com.alibaba.dashscope.audio.asr.recognition.Recognition
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam
import com.alibaba.dashscope.utils.Constants
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.example.myapplication.model.AliRecognitionResult
import com.example.myapplication.utils.TimeUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AudioService(private val apiKey: String) {

    suspend fun convertToMp3(input: File, output: File): Boolean = withContext(Dispatchers.IO) {
        // -ac 1 单声道, -ar 16000 采样率
        val cmd = "-i ${input.absolutePath} -ac 1 -ar 16000 -map 0:a:0 -y ${output.absolutePath}"
        val session = FFmpegKit.execute(cmd)
        ReturnCode.isSuccess(session.returnCode)
    }

    suspend fun transcribe(audioFile: File): String = withContext(Dispatchers.IO) {
        Constants.apiKey = apiKey

        val recognition = Recognition()
        val param = RecognitionParam.builder()
            .model("paraformer-realtime-v1")
            .format("mp3")
            .sampleRate(16000)
            .build()

        val jsonResult = recognition.call(param, audioFile)

        // 简单容错
        if (jsonResult.contains("\"code\"") && !jsonResult.contains("\"sentences\"")) {
            return@withContext "API 错误: $jsonResult"
        }

        val gson = Gson()
        val result = gson.fromJson(jsonResult, AliRecognitionResult::class.java)

        val sb = StringBuilder()
        result.sentences?.forEach { sentence ->
            val start = TimeUtils.formatMillis(sentence.begin_time)
            val end = TimeUtils.formatMillis(sentence.end_time)
            val text = sentence.text?.trim()
            if (!text.isNullOrEmpty()) {
                sb.append("[$start - $end] $text\n")
            }
        }

        if (sb.isEmpty()) "识别成功但无内容" else sb.toString()
    }
}