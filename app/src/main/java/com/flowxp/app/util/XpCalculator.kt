package com.flowxp.app.util

import com.flowxp.app.model.UserProgress

/**
 * Score from SNS minutes (spec §8), XP = score × 10 (§9), level: 100 XP per level (§10).
 *
 * Streak (§11): usage ≤ 60 min → success (streak+1), else streak=0.
 * Bonus XP: when streak becomes exactly 3 → +20; exactly 7 → +50 (same day not both 3 and 7).
 */
object XpCalculator {

    fun scoreFromMinutes(minutes: Int): Int = when {
        minutes <= 30 -> 10
        minutes <= 60 -> 8
        minutes <= 120 -> 5
        minutes <= 180 -> 3
        else -> 0
    }

    fun baseXpFromScore(score: Int): Int = score * 10

    fun streakBonusXp(newStreakAfterSuccess: Int): Int = when (newStreakAfterSuccess) {
        7 -> 50
        3 -> 20
        else -> 0
    }

    fun applyEarnedXp(progress: UserProgress, earnedXp: Int): UserProgress {
        var level = progress.level
        var current = progress.currentXp + earnedXp
        var total = progress.totalXp + earnedXp
        while (current >= 100) {
            level += 1
            current -= 100
        }
        return progress.copy(level = level, currentXp = current, totalXp = total)
    }

    /**
     * @param totalMinutes SNS usage for the closed calendar day (selected apps).
     * @return Pair(Daily base + bonus XP for records, updated progress with streak)
     */
    fun finalizeDay(
        progress: UserProgress,
        totalMinutes: Int,
    ): Pair<Int, UserProgress> {
        val score = scoreFromMinutes(totalMinutes)
        val baseXp = baseXpFromScore(score)
        val success = totalMinutes <= 60
        val newStreak = if (success) progress.streak + 1 else 0
        val bonus = if (success) streakBonusXp(newStreakAfterSuccess = newStreak) else 0
        val totalEarned = baseXp + bonus
        var updated = progress.copy(streak = newStreak)
        updated = applyEarnedXp(updated, totalEarned)
        return totalEarned to updated
    }

    /** Today's "preview" XP from current minutes (no streak bonus until day closes). */
    fun previewTodayXp(minutes: Int): Int = baseXpFromScore(scoreFromMinutes(minutes))
}
