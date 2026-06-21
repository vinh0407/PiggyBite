package com.money.app.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u000bH\u0002J\u0010\u0010\u0015\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u000bH\u0002J\u0012\u0010\u0017\u001a\u00020\u00132\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014J\u0010\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u000bH\u0002J\u0010\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u001b\u001a\u00020\u000bH\u0002J\b\u0010\u001d\u001a\u00020\u0013H\u0002J\u0010\u0010\u001e\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020\u000fH\u0002J\b\u0010 \u001a\u00020\u0013H\u0002J\b\u0010!\u001a\u00020\u0013H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/money/app/ui/AddTransactionActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "btnExpense", "Landroid/widget/TextView;", "btnIncome", "btnSave", "Landroid/widget/Button;", "cgCategories", "Lcom/google/android/material/chip/ChipGroup;", "currentRawAmount", "", "etDescription", "Landroid/widget/EditText;", "isExpenseMode", "", "isFromOCR", "tvAmount", "autoCategorize", "", "desc", "handleKey", "key", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "parseOCRText", "text", "parseVoiceText", "setupKeypad", "switchMode", "isExpense", "updateCategories", "validateAndSave", "app_debug"})
public final class AddTransactionActivity extends androidx.appcompat.app.AppCompatActivity {
    private android.widget.TextView tvAmount;
    private android.widget.EditText etDescription;
    private com.google.android.material.chip.ChipGroup cgCategories;
    private android.widget.TextView btnExpense;
    private android.widget.TextView btnIncome;
    private android.widget.Button btnSave;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String currentRawAmount = "0";
    private boolean isExpenseMode = true;
    private boolean isFromOCR = false;
    
    public AddTransactionActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void parseVoiceText(java.lang.String text) {
    }
    
    private final void parseOCRText(java.lang.String text) {
    }
    
    private final void autoCategorize(java.lang.String desc) {
    }
    
    private final void switchMode(boolean isExpense) {
    }
    
    private final void setupKeypad() {
    }
    
    private final void handleKey(java.lang.String key) {
    }
    
    private final void updateCategories() {
    }
    
    private final void validateAndSave() {
    }
}