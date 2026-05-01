package com.flowxp.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flowxp.app.data.PreferenceManager
import com.flowxp.app.data.UsageRepository
import com.flowxp.app.model.DailyUsage
import com.flowxp.app.model.TrackedApp
import com.flowxp.app.model.UserProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlowUiState(
    val progress: UserProgress = UserProgress.initial(),
    val todayMinutes: Int = 0,
    val todayPreviewXp: Int = 0,
    val perAppMinutes: List<Pair<String, Int>> = emptyList(),
    val history: List<DailyUsage> = emptyList(),
    val hasUsagePermission: Boolean = false,
    /** First [refresh] finished — avoids acting on stale default permission before load. */
    val hydrated: Boolean = false,
)

class FlowXpViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferenceManager(application)
    private val repository = UsageRepository(application, prefs)

    private val _state = MutableStateFlow(FlowUiState())
    val state: StateFlow<FlowUiState> = _state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val app = getApplication<Application>()
            repository.ensureTrackedAppsLoaded(app.packageManager)
            val permitted = repository.hasUsagePermission()
            if (permitted) {
                repository.syncProgressIfNeeded()
            }
            _state.update {
                FlowUiState(
                    progress = repository.getUserProgress(),
                    todayMinutes = if (permitted) repository.todayTotalMinutes() else 0,
                    todayPreviewXp = if (permitted) repository.previewTodayXp() else 0,
                    perAppMinutes = if (permitted) repository.todayPerAppMinutes() else emptyList(),
                    history = repository.getDailyHistory().sortedByDescending { it.date },
                    hasUsagePermission = permitted,
                    hydrated = true,
                )
            }
        }
    }

    fun repository(): UsageRepository = repository

    fun trackedAppsForEditor(): List<TrackedApp> = repository.getTrackedApps()

    fun saveTrackedApps(apps: List<TrackedApp>) {
        repository.saveTrackedApps(apps)
        refresh()
    }
}
