package com.fittrack.ui.home

/**
 * Enum representing gender options for anatomy display.
 */
enum class Gender {
    MALE, FEMALE
}

/**
 * Enum representing body view options.
 */
enum class BodyView {
    FRONT, BACK
}

/**
 * Data class holding muscle information for touch interaction.
 */
data class MuscleInfo(
    val id: String,
    val name: String,
    val muscleId: Int
)
