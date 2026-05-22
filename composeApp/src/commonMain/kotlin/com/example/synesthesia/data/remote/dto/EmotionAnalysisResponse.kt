package com.example.synesthesia.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmotionAnalysisResponse(
    val mainCategoryId: String, // HEU, HEP, LEP, LEU
    val subEmotion: String,
    val aiResonance: String? = null
)
