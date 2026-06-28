package com.money.app.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005J\u000e\u0010\u0010\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/money/app/util/CurrencyHelper;", "", "<init>", "()V", "PREFS_NAME", "", "KEY_CURRENCY", "CURRENCY_VND", "CURRENCY_USD", "EXCHANGE_RATE_USD_TO_VND", "", "saveCurrency", "", "context", "Landroid/content/Context;", "currency", "getSelectedCurrency", "convertFromBase", "amountVnd", "targetCurrency", "app_debug"})
public final class CurrencyHelper {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "currency_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_CURRENCY = "selected_currency";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CURRENCY_VND = "VND";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String CURRENCY_USD = "USD";
    public static final double EXCHANGE_RATE_USD_TO_VND = 25000.0;
    @org.jetbrains.annotations.NotNull()
    public static final com.money.app.util.CurrencyHelper INSTANCE = null;
    
    private CurrencyHelper() {
        super();
    }
    
    public final void saveCurrency(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String currency) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSelectedCurrency(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    /**
     * Quy đổi giá trị từ VND (đơn vị gốc) sang loại tiền tệ đích
     */
    public final double convertFromBase(double amountVnd, @org.jetbrains.annotations.NotNull()
    java.lang.String targetCurrency) {
        return 0.0;
    }
}