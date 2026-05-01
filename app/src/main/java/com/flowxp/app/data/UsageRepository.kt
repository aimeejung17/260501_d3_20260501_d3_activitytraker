package com.flowxp.app.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.flowxp.app.model.DailyUsage
import com.flowxp.app.model.RecommendedSns
import com.flowxp.app.model.TrackedApp
import com.flowxp.app.model.UserProgress
import com.flowxp.app.util.UsageStatsHelper
import com.flowxp.app.util.XpCalculator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UsageRepository(
    private val context: Context,
    private val prefs: PreferenceManager,
) {

    private val dateFmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun hasUsagePermission(): Boolean = UsageStatsHelper.hasUsageStatsPermission(context)

    /**
     * Ensure tracked app list exists; merge launcher apps with recommended defaults.
     */
    fun ensureTrackedAppsLoaded(pm: PackageManager) {
        if (prefs.loadTrackedApps() != null) return
        prefs.saveTrackedApps(buildInitialTrackedApps(pm))
    }

    fun getTrackedApps(): List<TrackedApp> =
        prefs.loadTrackedApps() ?: buildInitialTrackedApps(context.packageManager).also { prefs.saveTrackedApps(it) }

    fun saveTrackedApps(apps: List<TrackedApp>) {
        prefs.saveTrackedApps(apps)
    }

    fun getUserProgress(): UserProgress = prefs.loadUserProgress()

    fun getDailyHistory(): List<DailyUsage> = prefs.loadDailyHistory()

    /**
     * Finalize all calendar days from day after [lastFinalized] through yesterday; then expose today stats.
     */
    fun syncProgressIfNeeded() {
        if (!hasUsagePermission()) return

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val selected = getTrackedApps().filter { it.isSelected }.map { it.packageName }
        if (selected.isEmpty()) return

        val lastFinalized = prefs.getLastFinalizedDate()?.let { LocalDate.parse(it, dateFmt) }
        var progress = prefs.loadUserProgress()
        val history = prefs.loadDailyHistory().toMutableList()

        val start: LocalDate = if (lastFinalized == null) {
            today.minusDays(7)
        } else {
            lastFinalized.plusDays(1)
        }

        var d = start
        var newestFinalized: LocalDate? = null
        while (!d.isAfter(yesterday)) {
            val minutes = UsageStatsHelper.totalMinutesForDay(context, d, selected)
            val score = XpCalculator.scoreFromMinutes(minutes)
            val (totalEarned, newProgress) = XpCalculator.finalizeDay(progress, minutes)
            progress = newProgress
            val entry = DailyUsage(
                date = d.format(dateFmt),
                usageMinutes = minutes,
                score = score,
                earnedXp = totalEarned,
            )
            history.removeAll { it.date == entry.date }
            history.add(entry)
            newestFinalized = d
            d = d.plusDays(1)
        }

        prefs.saveUserProgress(progress)
        prefs.saveDailyHistory(history)
        newestFinalized?.let { prefs.setLastFinalizedDate(it.format(dateFmt)) }
    }

    fun todayTotalMinutes(): Int {
        val selected = getTrackedApps().filter { it.isSelected }.map { it.packageName }
        if (selected.isEmpty()) return 0
        return UsageStatsHelper.totalMinutesForDay(context, LocalDate.now(), selected)
    }

    fun todayPerAppMinutes(): List<Pair<String, Int>> {
        val tracked = getTrackedApps().filter { it.isSelected }
        if (tracked.isEmpty()) return emptyList()
        val pkgs = tracked.map { it.packageName }
        val map = UsageStatsHelper.minutesForDay(context, LocalDate.now(), pkgs)
        return tracked.map { app -> app.appName to (map[app.packageName] ?: 0) }
    }

    fun previewTodayXp(): Int = XpCalculator.previewTodayXp(todayTotalMinutes())

    private fun buildInitialTrackedApps(pm: PackageManager): List<TrackedApp> {
        val launcherPkgs = queryLauncherPackages(pm)
        val merged = linkedMapOf<String, String>()
        for (pkg in launcherPkgs) {
            val label = runCatching {
                pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
            }.getOrDefault(pkg)
            merged[pkg] = label
        }
        for (e in RecommendedSns.entries) {
            if (e.packageName !in merged && isPackageInstalled(pm, e.packageName)) {
                merged[e.packageName] = e.displayLabel
            }
        }
        return merged.map { (pkg, label) ->
            TrackedApp(
                packageName = pkg,
                appName = if (pkg in RecommendedSns.packageNames) RecommendedSns.labelFor(pkg) else label,
                isSelected = pkg in RecommendedSns.packageNames,
            )
        }.sortedBy { it.appName.lowercase() }
    }

    private fun queryLauncherPackages(pm: PackageManager): Set<String> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        @Suppress("DEPRECATION")
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.MATCH_ALL
        } else {
            0
        }
        @Suppress("DEPRECATION")
        val resolves = pm.queryIntentActivities(intent, flag)
        return resolves.map { it.activityInfo.packageName }.toSet()
    }

    private fun isPackageInstalled(pm: PackageManager, packageName: String): Boolean = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(packageName, 0)
        }
        true
    } catch (_: Exception) {
        false
    }
}
