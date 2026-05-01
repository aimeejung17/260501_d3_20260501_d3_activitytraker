package com.flowxp.app.model

/**
 * Default SNS packages (spec §7). Labels for UI when installed.
 */
object RecommendedSns {
    data class Entry(val packageName: String, val displayLabel: String)

    val entries: List<Entry> = listOf(
        Entry("com.instagram.android", "Instagram"),
        Entry("com.google.android.youtube", "YouTube"),
        Entry("com.zhiliaoapp.musically", "TikTok"),
        Entry("com.facebook.katana", "Facebook"),
        Entry("com.twitter.android", "X"),
        Entry("com.instagram.barcelona", "Threads"),
        Entry("com.kakao.talk", "KakaoTalk"),
        Entry("com.nhn.android.search", "Naver"),
    )

    val packageNames: Set<String> = entries.map { it.packageName }.toSet()

    fun labelFor(packageName: String): String =
        entries.find { it.packageName == packageName }?.displayLabel ?: packageName
}
