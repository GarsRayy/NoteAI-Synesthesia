package com.example.synesthesia.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmotionAnalysisResponse(
    val autoTitle: String,
    val paraphrasedContent: String,
    val emotionQuadrant: String, // HEP, HEU, LEP, LEU
    val subEmotion: String, // Specific sub-emotion from our system
    val artColorHex: String,
    val sentiment: String? = null,
    val emotionScore: Int? = null,
    val summary: String? = null
)
