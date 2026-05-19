package com.example.synesthesia.domain.model

data class EmotionCategory(
    val id: String,
    val name: String,
    val color: String,
    val subEmotions: List<String>
)

object EmotionSystem {
    val categories = listOf(
        EmotionCategory(
            id = "HEU",
            name = "High Energy, Unpleasant",
            color = "#FF5722",
            subEmotions = listOf("Agitated", "Volatile", "Frantic", "Furious", "Frenzied")
        ),
        EmotionCategory(
            id = "HEP",
            name = "High Energy, Pleasant",
            color = "#FFC107",
            subEmotions = listOf("Lively", "Enthusiastic", "Exuberant", "Elated", "Ecstatic")
        ),
        EmotionCategory(
            id = "LEP",
            name = "Low Energy, Pleasant",
            color = "#4CAF50",
            subEmotions = listOf("Relaxed", "Mellow", "Peaceful", "Serene", "Tranquil")
        ),
        EmotionCategory(
            id = "LEU",
            name = "Low Energy, Unpleasant",
            color = "#3F51B5",
            subEmotions = listOf("Disappointed", "Weary", "Gloomy", "Desolate", "Lethargic")
        )
    )

    fun getCategoryBySubEmotion(subEmotion: String?): EmotionCategory? {
        return categories.find { it.subEmotions.contains(subEmotion) }
    }
}
