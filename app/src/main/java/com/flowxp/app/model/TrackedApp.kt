package com.flowxp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackedApp(
    val packageName: String,
    val appName: String,
    val isSelected: Boolean,
)
