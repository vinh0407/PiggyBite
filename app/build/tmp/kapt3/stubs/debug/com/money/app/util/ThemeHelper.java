package com.money.app.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\bJ\u000e\u0010\u0011\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/money/app/util/ThemeHelper;", "", "<init>", "()V", "PREFS_NAME", "", "KEY_THEME", "THEME_LIGHT", "", "THEME_DARK", "THEME_SYSTEM", "applyTheme", "", "context", "Landroid/content/Context;", "saveTheme", "theme", "getSavedTheme", "app_debug"})
public final class ThemeHelper {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "theme_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_THEME = "selected_theme";
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;
    @org.jetbrains.annotations.NotNull()
    public static final com.money.app.util.ThemeHelper INSTANCE = null;
    
    private ThemeHelper() {
        super();
    }
    
    public final void applyTheme(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final void saveTheme(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int theme) {
    }
    
    public final int getSavedTheme(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0;
    }
}