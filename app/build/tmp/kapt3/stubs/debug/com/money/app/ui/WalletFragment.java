package com.money.app.ui;

/**
 * Dashboard Fragment representing the user's primary wallet overview.
 * Features:
 * - Real-time balance calculation
 * - Monthly analysis charts (Donut & Line)
 * - Recent transaction history
 * - Shared fund management
 *
 * Automatically refreshes data in [onResume] to ensure UI consistency.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J$\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0016J\u001a\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u00172\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0016J\b\u0010!\u001a\u00020\u001fH\u0016J\u0010\u0010\"\u001a\u00020\u001f2\u0006\u0010#\u001a\u00020$H\u0002J\u0012\u0010%\u001a\u0004\u0018\u00010&2\u0006\u0010#\u001a\u00020$H\u0002J\b\u0010\'\u001a\u00020\u001fH\u0002J\b\u0010(\u001a\u00020\u001fH\u0002J\b\u0010)\u001a\u00020\u001fH\u0002J\b\u0010*\u001a\u00020\u001fH\u0002J\b\u0010-\u001a\u00020\u001fH\u0002J\u0016\u0010.\u001a\u00020\u001f2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020100H\u0002J\u0016\u00102\u001a\u00020\u001f2\f\u0010/\u001a\b\u0012\u0004\u0012\u00020100H\u0002J\b\u00103\u001a\u00020\u001fH\u0002J\u0016\u00104\u001a\u00020\u001f2\f\u00105\u001a\b\u0012\u0004\u0012\u00020100H\u0002J\b\u00106\u001a\u00020\u001fH\u0002J\u0010\u00107\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0010\u0010:\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0010\u0010;\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0010\u0010<\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0010\u0010=\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0010\u0010>\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0010\u0010?\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0010\u0010@\u001a\u00020\u001f2\u0006\u00108\u001a\u000209H\u0002J\u0018\u0010A\u001a\u00020\u001f2\u0006\u00108\u001a\u0002092\u0006\u0010B\u001a\u00020\nH\u0002J \u0010C\u001a\u00020\u001f2\u0006\u00108\u001a\u0002092\u0006\u0010D\u001a\u00020\r2\u0006\u0010B\u001a\u00020\nH\u0002J\b\u0010E\u001a\u00020\u001fH\u0016R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00058BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n \u0010*\u0004\u0018\u00010\u000f0\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0011\u001a\u0010\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00130\u00130\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0014\u001a\u0010\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00150\u00150\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010+\u001a\u0004\u0018\u00010,X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006F"}, d2 = {"Lcom/money/app/ui/WalletFragment;", "Landroidx/fragment/app/Fragment;", "<init>", "()V", "_binding", "Lcom/money/app/databinding/FragmentWalletBinding;", "binding", "getBinding", "()Lcom/money/app/databinding/FragmentWalletBinding;", "isExpenseMode", "", "isBalanceVisible", "actualBalance", "", "analysisMonth", "Ljava/util/Calendar;", "kotlin.jvm.PlatformType", "requestPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "pickMedia", "Landroidx/activity/result/PickVisualMediaRequest;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "", "view", "onResume", "saveAvatarUri", "uri", "Landroid/net/Uri;", "saveImageToInternalStorage", "Ljava/io/File;", "loadAvatar", "setupToggle", "setupQuickActions", "updateAnalysisUI", "loadDataJob", "Lkotlinx/coroutines/Job;", "loadData", "updateDonutChart", "transactions", "", "Lcom/money/app/data/Transaction;", "updateLineChart", "updateBalanceDisplay", "renderRecent", "list", "renderFunds", "showEditDeleteDialog", "fund", "Lcom/money/app/data/Fund;", "confirmLeaveFund", "leaveFundAndRefund", "confirmDeleteFund", "deleteFundAndRefund", "showContributionsDialog", "showAddMemberDialog", "showUpdateFundDialog", "showAmountDialog", "isDeposit", "processFundTransaction", "amount", "onDestroyView", "app_debug"})
public final class WalletFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.money.app.databinding.FragmentWalletBinding _binding;
    private boolean isExpenseMode = true;
    private boolean isBalanceVisible = true;
    private double actualBalance = 0.0;
    private java.util.Calendar analysisMonth;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> requestPermissionLauncher = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> pickMedia = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job loadDataJob;
    
    public WalletFragment() {
        super();
    }
    
    private final com.money.app.databinding.FragmentWalletBinding getBinding() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onResume() {
    }
    
    private final void saveAvatarUri(android.net.Uri uri) {
    }
    
    private final java.io.File saveImageToInternalStorage(android.net.Uri uri) {
        return null;
    }
    
    private final void loadAvatar() {
    }
    
    private final void setupToggle() {
    }
    
    private final void setupQuickActions() {
    }
    
    private final void updateAnalysisUI() {
    }
    
    private final void loadData() {
    }
    
    private final void updateDonutChart(java.util.List<com.money.app.data.Transaction> transactions) {
    }
    
    private final void updateLineChart(java.util.List<com.money.app.data.Transaction> transactions) {
    }
    
    private final void updateBalanceDisplay() {
    }
    
    private final void renderRecent(java.util.List<com.money.app.data.Transaction> list) {
    }
    
    private final void renderFunds() {
    }
    
    private final void showEditDeleteDialog(com.money.app.data.Fund fund) {
    }
    
    private final void confirmLeaveFund(com.money.app.data.Fund fund) {
    }
    
    private final void leaveFundAndRefund(com.money.app.data.Fund fund) {
    }
    
    private final void confirmDeleteFund(com.money.app.data.Fund fund) {
    }
    
    private final void deleteFundAndRefund(com.money.app.data.Fund fund) {
    }
    
    private final void showContributionsDialog(com.money.app.data.Fund fund) {
    }
    
    private final void showAddMemberDialog(com.money.app.data.Fund fund) {
    }
    
    private final void showUpdateFundDialog(com.money.app.data.Fund fund) {
    }
    
    private final void showAmountDialog(com.money.app.data.Fund fund, boolean isDeposit) {
    }
    
    private final void processFundTransaction(com.money.app.data.Fund fund, double amount, boolean isDeposit) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}