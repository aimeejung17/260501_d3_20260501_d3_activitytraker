package com.flowxp.app.util

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import java.time.LocalDate
import java.time.ZoneId

object UsageStatsHelper {

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName,
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName,
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Sum foreground time in [day, day+1) for given packages. Returns per-package minutes and total.
     */
    fun minutesForDay(
        context: Context,
        day: LocalDate,
        packageNames: Collection<String>,
    ): Map<String, Int> {
        if (packageNames.isEmpty()) return emptyMap()
        val zone = ZoneId.systemDefault()
        val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats: List<UsageStats> =
            usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end) ?: emptyList()
        val want = packageNames.toSet()
        val byPkg = stats.filter { it.packageName in want }.groupBy { it.packageName }
        val result = mutableMapOf<String, Int>()
        for (pkg in want) {
            val list = byPkg[pkg].orEmpty()
            val millis = list.sumOf { it.totalTimeInForeground }
            result[pkg] = (millis / 60_000L).toInt()
        }
        return result
    }

    fun totalMinutesForDay(
        context: Context,
        day: LocalDate,
        packageNames: Collection<String>,
    ): Int = minutesForDay(context, day, packageNames).values.sum()
}
