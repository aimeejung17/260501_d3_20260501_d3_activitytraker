package com.flowxp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProgress(
    val level: Int,
    val currentXp: Int,
    val totalXp: Int,
    val streak: Int,
) {
    companion object {
        fun initial() = UserProgress(level = 1, currentXp = 0, totalXp = 0, streak = 0)
    }
}
