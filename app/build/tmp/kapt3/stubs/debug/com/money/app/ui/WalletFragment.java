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
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\r\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J$\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\u001a\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00132\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0016J\b\u0010\u001d\u001a\u00020\u001bH\u0016J\u0010\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001f\u001a\u00020 H\u0002J\b\u0010!\u001a\u00020\u001bH\u0002J\b\u0010\"\u001a\u00020\u001bH\u0002J\b\u0010#\u001a\u00020\u001bH\u0002J\b\u0010$\u001a\u00020\u001bH\u0002J\u0016\u0010%\u001a\u00020\u001b2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020(0\'H\u0002J\u0016\u0010)\u001a\u00020\u001b2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020(0\'H\u0002J\b\u0010*\u001a\u00020\u001bH\u0002J\u0016\u0010+\u001a\u00020\u001b2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020(0\'H\u0002J\b\u0010-\u001a\u00020\u001bH\u0002J\u0010\u0010.\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00101\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00102\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00103\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00104\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00105\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00106\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00107\u001a\u00020\u001b2\u0006\u0010/\u001a\u000200H\u0002J\u0018\u00108\u001a\u00020\u001b2\u0006\u0010/\u001a\u0002002\u0006\u00109\u001a\u00020\nH\u0002J \u0010:\u001a\u00020\u001b2\u0006\u0010/\u001a\u0002002\u0006\u0010;\u001a\u00020\r2\u0006\u00109\u001a\u00020\nH\u0002J\b\u0010<\u001a\u00020\u001bH\u0016R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00058BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0010\u0012\f\u0012\n \u0011*\u0004\u0018\u00010\u00100\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006="}, d2 = {"Lcom/money/app/ui/WalletFragment;", "Landroidx/fragment/app/Fragment;", "<init>", "()V", "_binding", "Lcom/money/app/databinding/FragmentWalletBinding;", "binding", "getBinding", "()Lcom/money/app/databinding/FragmentWalletBinding;", "isExpenseMode", "", "isBalanceVisible", "actualBalance", "", "pickMedia", "Landroidx/activity/result/ActivityResultLauncher;", "Landroidx/activity/result/PickVisualMediaRequest;", "kotlin.jvm.PlatformType", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "", "view", "onResume", "saveAvatarUri", "uri", "Landroid/net/Uri;", "loadAvatar", "setupToggle", "setupQuickActions", "loadData", "updateDonutChart", "transactions", "", "Lcom/money/app/data/Transaction;", "updateLineChart", "updateBalanceDisplay", "renderRecent", "list", "renderFunds", "showEditDeleteDialog", "fund", "Lcom/money/app/data/Fund;", "confirmLeaveFund", "leaveFundAndRefund", "confirmDeleteFund", "deleteFundAndRefund", "showContributionsDialog", "showAddMemberDialog", "showUpdateFundDialog", "showAmountDialog", "isDeposit", "processFundTransaction", "amount", "onDestroyView", "app_debug"})
public final class WalletFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.money.app.databinding.FragmentWalletBinding _binding;
    private boolean isExpenseMode = true;
    private boolean isBalanceVisible = true;
    private double actualBalance = 0.0;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> pickMedia = null;
    
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
    
    private final void loadAvatar() {
    }
    
    private final void setupToggle() {
    }
    
    private final void setupQuickActions() {
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