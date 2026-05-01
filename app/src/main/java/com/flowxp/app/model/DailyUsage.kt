package com.flowxp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyUsage(
    val date: String,
    val usageMinutes: Int,
    val score: Int,
    val earnedXp: Int,
)
