package com.flowxp.app.data

import android.content.Context
import com.flowxp.app.model.DailyUsage
import com.flowxp.app.model.TrackedApp
import com.flowxp.app.model.UserProgress
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class PreferenceManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun loadUserProgress(): UserProgress {
        val raw = prefs.getString(KEY_USER_PROGRESS, null) ?: return UserProgress.initial()
        return runCatching { json.decodeFromString(UserProgress.serializer(), raw) }
            .getOrElse { UserProgress.initial() }
    }

    fun saveUserProgress(progress: UserProgress) {
        prefs.edit().putString(KEY_USER_PROGRESS, json.encodeToString(UserProgress.serializer(), progress)).apply()
    }

    fun loadTrackedApps(): List<TrackedApp>? {
        val raw = prefs.getString(KEY_TRACKED_APPS, null) ?: return null
        return runCatching {
            json.decodeFromString(ListSerializer(TrackedApp.serializer()), raw)
        }.getOrNull()
    }

    fun saveTrackedApps(apps: List<TrackedApp>) {
        prefs.edit()
            .putString(KEY_TRACKED_APPS, json.encodeToString(ListSerializer(TrackedApp.serializer()), apps))
            .apply()
    }

    fun loadDailyHistory(): List<DailyUsage> {
        val raw = prefs.getString(KEY_DAILY_USAGE, null) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(DailyUsage.serializer()), raw)
        }.getOrElse { emptyList() }
    }

    fun saveDailyHistory(list: List<DailyUsage>) {
        val trimmed = list.sortedByDescending { it.date }.take(MAX_HISTORY_DAYS)
        prefs.edit()
            .putString(KEY_DAILY_USAGE, json.encodeToString(ListSerializer(DailyUsage.serializer()), trimmed))
            .apply()
    }

    fun getLastFinalizedDate(): String? = prefs.getString(KEY_LAST_FINALIZED, null)

    fun setLastFinalizedDate(date: String) {
        prefs.edit().putString(KEY_LAST_FINALIZED, date).apply()
    }

    companion object {
        private const val PREFS_NAME = "flowxp_prefs"
        private const val KEY_USER_PROGRESS = "user_progress"
        private const val KEY_TRACKED_APPS = "tracked_apps"
        private const val KEY_DAILY_USAGE = "daily_usage"
        private const val KEY_LAST_FINALIZED = "last_finalized_date"
        private const val MAX_HISTORY_DAYS = 7
    }
}
