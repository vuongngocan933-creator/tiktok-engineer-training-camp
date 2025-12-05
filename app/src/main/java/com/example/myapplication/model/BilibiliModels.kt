package com.example.myapplication.model

data class BiliViewResponse(
    val code: Int,
    val data: BiliViewData?
)

data class BiliViewData(
    val bvid: String,
    val cid: Long,
    val title: String
)

data class BiliPlayUrlResponse(
    val code: Int,
    val data: BiliPlayUrlData?
)

data class BiliPlayUrlData(
    val dash: BiliDashData?
)

data class BiliDashData(
    val audio: List<BiliAudioStream>?
)

data class BiliAudioStream(
    val id: Int,
    val baseUrl: String,
    val bandwidth: Long
)