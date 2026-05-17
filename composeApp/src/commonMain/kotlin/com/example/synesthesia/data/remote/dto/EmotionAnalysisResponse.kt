package com.example.synesthesia.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmotionAnalysisResponse(
    val sentiment: String,
    val emotion: String,
    val emotionScore: Int,
    val artToken: String
)