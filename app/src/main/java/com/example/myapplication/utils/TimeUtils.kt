package com.example.myapplication.utils

object TimeUtils {
    fun formatMillis(millis: Long?): String {
        if (millis == null) return "00:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}