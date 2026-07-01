package com.money.app.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Hỗ trợ Chủ đề (Theme Helper): Quản lý việc thay đổi giao diện Sáng (Light) và Tối (Dark).
 */
object ThemeHelper {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"

    const val THEME_LIGHT = 0
    const val THEME_DARK = 1
    const val THEME_SYSTEM = 2

    /**
     * Áp dụng chủ đề đã lưu vào toàn bộ ứng dụng thông qua AppCompatDelegate.
     */
    fun applyTheme(context: Context) {
        val theme = getSavedTheme(context)
        val mode = when (theme) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Lưu tùy chọn chủ đề và áp dụng ngay lập tức.
     */
    fun saveTheme(context: Context, theme: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_THEME, theme).apply()
        applyTheme(context)
    }

    /**
     * Lấy chủ đề đang được lưu trong bộ nhớ máy.
     */
    fun getSavedTheme(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_THEME, THEME_SYSTEM)
    }
}
