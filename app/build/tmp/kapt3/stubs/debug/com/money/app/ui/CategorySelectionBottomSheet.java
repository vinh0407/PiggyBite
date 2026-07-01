package com.money.app.ui;

/**
 * BottomSheetDialogFragment: Hộp thoại lựa chọn hạng mục giao dịch (Thu nhập/Chi tiêu).
 * Tính năng chính:
 * - Hiển thị danh sách hạng mục phân loại theo nhóm (Cố định, Thiết yếu, Giải trí, Giáo dục...).
 * - Hiển thị tổng số tiền đã chi tiêu cho từng nhóm trong tháng hiện tại ngay trên tiêu đề nhóm.
 * - Cho phép người dùng tạo thêm hạng mục tùy chỉnh kèm Emoji.
 * - Hỗ trợ chọn nhiều hạng mục cùng lúc.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 D2\u00020\u0001:\u0002CDB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0016J&\u0010#\u001a\u0004\u0018\u00010\u000e2\u0006\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\'2\b\u0010(\u001a\u0004\u0018\u00010)H\u0016J\u001a\u0010*\u001a\u00020 2\u0006\u0010+\u001a\u00020\u000e2\b\u0010(\u001a\u0004\u0018\u00010)H\u0016J\b\u0010,\u001a\u00020 H\u0002J\b\u0010-\u001a\u00020 H\u0002J\b\u0010.\u001a\u00020 H\u0002J$\u0010/\u001a\u0002002\f\u00101\u001a\b\u0012\u0004\u0012\u0002020\t2\f\u00103\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002J\b\u00104\u001a\u00020 H\u0002J\u0010\u00105\u001a\u00020 2\u0006\u00106\u001a\u00020\nH\u0002J\u0010\u00107\u001a\u00020 2\u0006\u00106\u001a\u00020\nH\u0002J\u0010\u00108\u001a\u00020 2\u0006\u00109\u001a\u00020\nH\u0002J$\u0010:\u001a\u00020 2\u0006\u00106\u001a\u00020\n2\u0006\u0010;\u001a\u00020\u00072\n\b\u0002\u0010+\u001a\u0004\u0018\u00010\u000eH\u0002J\u0016\u0010<\u001a\u00020 2\f\u0010=\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002J\u000e\u0010>\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0002J\b\u0010?\u001a\u00020 H\u0002J\u001c\u0010@\u001a\u00020 2\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020 0BH\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0012X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001c\u001a\n \u001e*\u0004\u0018\u00010\u001d0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006E"}, d2 = {"Lcom/money/app/ui/CategorySelectionBottomSheet;", "Lcom/google/android/material/bottomsheet/BottomSheetDialogFragment;", "<init>", "()V", "listener", "Lcom/money/app/ui/CategorySelectionBottomSheet$OnCategorySelectedListener;", "isExpense", "", "initialSelectedCategories", "", "", "groupOther", "Lcom/google/android/material/chip/ChipGroup;", "headerOther", "Landroid/view/View;", "layoutExpense", "layoutIncome", "headerFixed", "Landroid/widget/TextView;", "headerEssential", "headerFun", "headerEducation", "headerIncomeFixed", "headerIncomeFlex", "tvCurrentMonth", "tvTitle", "df", "Ljava/text/DecimalFormat;", "displayedMonth", "Ljava/util/Calendar;", "kotlin.jvm.PlatformType", "onAttach", "", "context", "Landroid/content/Context;", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "view", "updateMonthDisplay", "setupExistingCategoryLongClicks", "calculateAndDisplayTotals", "calculateGroupTotal", "", "transactions", "Lcom/money/app/data/Transaction;", "categoryNames", "loadCustomCategories", "saveCustomCategory", "category", "deleteCustomCategory", "addCategoryChip", "text", "showDeleteConfirmation", "isBuiltIn", "restoreSelection", "selected", "findAllSelectedChips", "showCreateCategoryDialog", "showEmojiPicker", "onEmojiSelected", "Lkotlin/Function1;", "OnCategorySelectedListener", "Companion", "app_debug"})
public final class CategorySelectionBottomSheet extends com.google.android.material.bottomsheet.BottomSheetDialogFragment {
    @org.jetbrains.annotations.Nullable()
    private com.money.app.ui.CategorySelectionBottomSheet.OnCategorySelectedListener listener;
    private boolean isExpense = true;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<java.lang.String> initialSelectedCategories;
    private com.google.android.material.chip.ChipGroup groupOther;
    private android.view.View headerOther;
    private android.view.View layoutExpense;
    private android.view.View layoutIncome;
    private android.widget.TextView headerFixed;
    private android.widget.TextView headerEssential;
    private android.widget.TextView headerFun;
    private android.widget.TextView headerEducation;
    private android.widget.TextView headerIncomeFixed;
    private android.widget.TextView headerIncomeFlex;
    private android.widget.TextView tvCurrentMonth;
    private android.widget.TextView tvTitle;
    @org.jetbrains.annotations.NotNull()
    private final java.text.DecimalFormat df = null;
    private final java.util.Calendar displayedMonth = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.money.app.ui.CategorySelectionBottomSheet.Companion Companion = null;
    
    public CategorySelectionBottomSheet() {
        super();
    }
    
    @java.lang.Override()
    public void onAttach(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
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
    
    private final void updateMonthDisplay() {
    }
    
    /**
     * Tự động duyệt qua tất cả các Chip có sẵn trong Layout để gán sự kiện nhấn giữ
     */
    private final void setupExistingCategoryLongClicks() {
    }
    
    /**
     * Truy vấn Database Room để tính tổng số tiền của từng nhóm hạng mục trong tháng
     */
    private final void calculateAndDisplayTotals() {
    }
    
    private final double calculateGroupTotal(java.util.List<com.money.app.data.Transaction> transactions, java.util.List<java.lang.String> categoryNames) {
        return 0.0;
    }
    
    /**
     * Tải các hạng mục tùy chỉnh từ SharedPreferences
     */
    private final void loadCustomCategories() {
    }
    
    private final void saveCustomCategory(java.lang.String category) {
    }
    
    private final void deleteCustomCategory(java.lang.String category) {
    }
    
    private final void addCategoryChip(java.lang.String text) {
    }
    
    private final void showDeleteConfirmation(java.lang.String category, boolean isBuiltIn, android.view.View view) {
    }
    
    private final void restoreSelection(java.util.List<java.lang.String> selected) {
    }
    
    private final java.util.List<java.lang.String> findAllSelectedChips() {
        return null;
    }
    
    /**
     * Hiển thị hộp thoại tạo hạng mục mới kèm bộ chọn Emoji
     */
    private final void showCreateCategoryDialog() {
    }
    
    private final void showEmojiPicker(kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onEmojiSelected) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a8\u0006\u000b"}, d2 = {"Lcom/money/app/ui/CategorySelectionBottomSheet$Companion;", "", "<init>", "()V", "newInstance", "Lcom/money/app/ui/CategorySelectionBottomSheet;", "isExpense", "", "selected", "Ljava/util/ArrayList;", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.money.app.ui.CategorySelectionBottomSheet newInstance(boolean isExpense, @org.jetbrains.annotations.NotNull()
        java.util.ArrayList<java.lang.String> selected) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H&\u00a8\u0006\u0007\u00c0\u0006\u0003"}, d2 = {"Lcom/money/app/ui/CategorySelectionBottomSheet$OnCategorySelectedListener;", "", "onCategorySelected", "", "categories", "", "", "app_debug"})
    public static abstract interface OnCategorySelectedListener {
        
        public abstract void onCategorySelected(@org.jetbrains.annotations.NotNull()
        java.util.List<java.lang.String> categories);
    }
}