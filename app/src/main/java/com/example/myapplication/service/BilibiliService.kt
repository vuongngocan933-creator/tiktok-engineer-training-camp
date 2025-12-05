package com.example.myapplication.service

import com.example.myapplication.model.BiliPlayUrlResponse
import com.example.myapplication.model.BiliViewResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

object BilibiliService {

    private val client = OkHttpClient()
    private val gson = Gson()

    // 提取 BV 号
    fun extractBvid(url: String): String? {
        val regex = "BV[a-zA-Z0-9]+".toRegex()
        return regex.find(url)?.value
    }

    // 获取音频下载链接
    fun fetchAudioUrl(bvid: String): String {
        // 1. 获取 CID
        val viewUrl = "https://api.bilibili.com/x/web-interface/view?bvid=$bvid"
        val viewRequest = Request.Builder()
            .url(viewUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)...")
            .build()

        val viewResponse = client.newCall(viewRequest).execute()
        val viewData = gson.fromJson(viewResponse.body?.string(), BiliViewResponse::class.java)

        if (viewData?.data == null) throw Exception("无法获取视频信息(CID)")
        val cid = viewData.data.cid

        // 2. 获取 PlayURL
        val playUrl = "https://api.bilibili.com/x/player/playurl?bvid=$bvid&cid=$cid&qn=16&fnval=16&fourk=1"
        val playRequest = Request.Builder()
            .url(playUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)...")
            .build()

        val playResponse = client.newCall(playRequest).execute()
        val playData = gson.fromJson(playResponse.body?.string(), BiliPlayUrlResponse::class.java)

        return playData?.data?.dash?.audio?.get(0)?.baseUrl
            ?: throw Exception("未找到音频流地址")
    }
}