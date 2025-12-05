package com.example.myapplication.model

data class AliRecognitionResult(
    val sentences: List<AliSentenceData>?
)

data class AliSentenceData(
    val text: String?,
    val begin_time: Long?,
    val end_time: Long?
)

data class AliRecognitionResponse(
    val code: String?,
    val message: String?,
    val requestId: String?,
    val output: AliRecognitionOutput?
)

data class AliRecognitionOutput(
    val text: String?,
    val finish_time: Long?
)