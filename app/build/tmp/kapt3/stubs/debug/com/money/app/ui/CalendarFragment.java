package com.money.app.ui;

/**
 * Fragment Lịch (Calendar): Hiển thị giao dịch dưới dạng lịch biểu hàng tháng.
 * Tính năng chính:
 * - Xem tổng quan thu/chi theo từng ngày trên lưới lịch.
 * - Xem chi tiết danh sách giao dịch của một ngày cụ thể khi nhấn chọn.
 * - Xuất và Nhập dữ liệu giao dịch dưới định dạng CSV (Sao lưu thủ công).
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0003345B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u001a\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u001b2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\b\u0010%\u001a\u00020#H\u0016J\b\u0010&\u001a\u00020#H\u0002J\u0016\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u000f2\u0006\u0010)\u001a\u00020\u0012H\u0002J\b\u0010*\u001a\u00020#H\u0002J\u0010\u0010+\u001a\u00020#2\u0006\u0010,\u001a\u00020-H\u0002J\u0010\u0010.\u001a\u00020#2\u0006\u0010,\u001a\u00020-H\u0002J\b\u0010/\u001a\u00020#H\u0002J\u0010\u00100\u001a\u00020#2\u0006\u00101\u001a\u000202H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n \u0013*\u0004\u0018\u00010\u00120\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\n \u0013*\u0004\u0018\u00010\u00120\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0015\u001a\u0010\u0012\f\u0012\n \u0013*\u0004\u0018\u00010\u00170\u00170\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R(\u0010\u0018\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\u0017 \u0013*\n\u0012\u0004\u0012\u00020\u0017\u0018\u00010\u00190\u00190\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00066"}, d2 = {"Lcom/money/app/ui/CalendarFragment;", "Landroidx/fragment/app/Fragment;", "<init>", "()V", "rvDailyList", "Landroidx/recyclerview/widget/RecyclerView;", "rvCalendarGrid", "tvIncVal", "Landroid/widget/TextView;", "tvExpVal", "tvBalVal", "tvCalendarMonth", "tvSelectedDateLabel", "tvDailyCount", "allTransactions", "", "Lcom/money/app/data/Transaction;", "displayedMonth", "Ljava/util/Calendar;", "kotlin.jvm.PlatformType", "selectedDate", "createDocument", "Landroidx/activity/result/ActivityResultLauncher;", "", "openDocument", "", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "", "view", "onResume", "updateCalendar", "generateCalendarDays", "Lcom/money/app/ui/CalendarFragment$CalendarDay;", "month", "showExportImportDialog", "exportToCsv", "uri", "Landroid/net/Uri;", "importFromCsv", "loadMonthData", "showDailyTransactions", "date", "Ljava/util/Date;", "CalendarDay", "CalendarGridAdapter", "DailyAdapter", "app_debug"})
public final class CalendarFragment extends androidx.fragment.app.Fragment {
    private androidx.recyclerview.widget.RecyclerView rvDailyList;
    private androidx.recyclerview.widget.RecyclerView rvCalendarGrid;
    private android.widget.TextView tvIncVal;
    private android.widget.TextView tvExpVal;
    private android.widget.TextView tvBalVal;
    private android.widget.TextView tvCalendarMonth;
    private android.widget.TextView tvSelectedDateLabel;
    private android.widget.TextView tvDailyCount;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.money.app.data.Transaction> allTransactions;
    private final java.util.Calendar displayedMonth = null;
    private java.util.Calendar selectedDate;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> createDocument = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> openDocument = null;
    
    public CalendarFragment() {
        super();
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
    
    @java.lang.Override()
    public void onResume() {
    }
    
    /**
     * Cập nhật giao diện lưới lịch và các thông số tóm tắt của tháng đang xem
     */
    private final void updateCalendar() {
    }
    
    /**
     * Thuật toán tạo danh sách các ngày để hiển thị lên lưới 7x6 (42 ô)
     */
    private final java.util.List<com.money.app.ui.CalendarFragment.CalendarDay> generateCalendarDays(java.util.Calendar month) {
        return null;
    }
    
    /**
     * Hiển thị menu chọn Xuất hoặc Nhập file CSV
     */
    private final void showExportImportDialog() {
    }
    
    /**
     * Logic xuất toàn bộ giao dịch ra file CSV
     */
    private final void exportToCsv(android.net.Uri uri) {
    }
    
    /**
     * Logic nhập dữ liệu từ file CSV vào ứng dụng và đồng bộ lên Firebase
     */
    private final void importFromCsv(android.net.Uri uri) {
    }
    
    /**
     * Tải dữ liệu giao dịch từ Database cục bộ
     */
    private final void loadMonthData() {
    }
    
    /**
     * Cập nhật danh sách giao dịch bên dưới lịch cho ngày đang chọn
     */
    private final void showDailyTransactions(java.util.Date date) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u001a\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\tH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0007H\u00c6\u0003JE\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010 \u001a\u00020\u00072\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00d6\u0001J\t\u0010#\u001a\u00020$H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0011R\u001a\u0010\b\u001a\u00020\tX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001a\u0010\n\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0011\"\u0004\b\u0017\u0010\u0018\u00a8\u0006%"}, d2 = {"Lcom/money/app/ui/CalendarFragment$CalendarDay;", "", "day", "", "month", "year", "isCurrentMonth", "", "totalAmount", "", "hasTransactions", "<init>", "(IIIZDZ)V", "getDay", "()I", "getMonth", "getYear", "()Z", "getTotalAmount", "()D", "setTotalAmount", "(D)V", "getHasTransactions", "setHasTransactions", "(Z)V", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "toString", "", "app_debug"})
    public static final class CalendarDay {
        private final int day = 0;
        private final int month = 0;
        private final int year = 0;
        private final boolean isCurrentMonth = false;
        private double totalAmount;
        private boolean hasTransactions;
        
        public CalendarDay(int day, int month, int year, boolean isCurrentMonth, double totalAmount, boolean hasTransactions) {
            super();
        }
        
        public final int getDay() {
            return 0;
        }
        
        public final int getMonth() {
            return 0;
        }
        
        public final int getYear() {
            return 0;
        }
        
        public final boolean isCurrentMonth() {
            return false;
        }
        
        public final double getTotalAmount() {
            return 0.0;
        }
        
        public final void setTotalAmount(double p0) {
        }
        
        public final boolean getHasTransactions() {
            return false;
        }
        
        public final void setHasTransactions(boolean p0) {
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final int component3() {
            return 0;
        }
        
        public final boolean component4() {
            return false;
        }
        
        public final double component5() {
            return 0.0;
        }
        
        public final boolean component6() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.money.app.ui.CalendarFragment.CalendarDay copy(int day, int month, int year, boolean isCurrentMonth, double totalAmount, boolean hasTransactions) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    /**
     * Adapter cho lưới lịch
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0013B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ \u0010\t\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u000e\u001a\u00020\u000f2\u000e\u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\rH\u0016J\b\u0010\u0012\u001a\u00020\rH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/money/app/ui/CalendarFragment$CalendarGridAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/money/app/ui/CalendarFragment$CalendarGridAdapter$ViewHolder;", "Lcom/money/app/ui/CalendarFragment;", "days", "", "Lcom/money/app/ui/CalendarFragment$CalendarDay;", "<init>", "(Lcom/money/app/ui/CalendarFragment;Ljava/util/List;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "", "holder", "position", "getItemCount", "ViewHolder", "app_debug"})
    public final class CalendarGridAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.money.app.ui.CalendarFragment.CalendarGridAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.money.app.ui.CalendarFragment.CalendarDay> days = null;
        
        public CalendarGridAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<com.money.app.ui.CalendarFragment.CalendarDay> days) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.money.app.ui.CalendarFragment.CalendarGridAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.money.app.ui.CalendarFragment.CalendarGridAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0019\u0010\u000b\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0019\u0010\r\u001a\n \b*\u0004\u0018\u00010\u00030\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/money/app/ui/CalendarFragment$CalendarGridAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "<init>", "(Lcom/money/app/ui/CalendarFragment$CalendarGridAdapter;Landroid/view/View;)V", "tvDay", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "getTvDay", "()Landroid/widget/TextView;", "tvAmount", "getTvAmount", "viewSelection", "getViewSelection", "()Landroid/view/View;", "app_debug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            private final android.widget.TextView tvDay = null;
            private final android.widget.TextView tvAmount = null;
            private final android.view.View viewSelection = null;
            
            public ViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View v) {
                super(null);
            }
            
            public final android.widget.TextView getTvDay() {
                return null;
            }
            
            public final android.widget.TextView getTvAmount() {
                return null;
            }
            
            public final android.view.View getViewSelection() {
                return null;
            }
        }
    }
    
    /**
     * Adapter hiển thị từng dòng giao dịch trong ngày
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0013B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ \u0010\t\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u000e\u001a\u00020\u000f2\u000e\u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\rH\u0016J\b\u0010\u0012\u001a\u00020\rH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/money/app/ui/CalendarFragment$DailyAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/money/app/ui/CalendarFragment$DailyAdapter$ViewHolder;", "Lcom/money/app/ui/CalendarFragment;", "list", "", "Lcom/money/app/data/Transaction;", "<init>", "(Lcom/money/app/ui/CalendarFragment;Ljava/util/List;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "", "holder", "position", "getItemCount", "ViewHolder", "app_debug"})
    public final class DailyAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.money.app.ui.CalendarFragment.DailyAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.money.app.data.Transaction> list = null;
        
        public DailyAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<com.money.app.data.Transaction> list) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.money.app.ui.CalendarFragment.DailyAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.money.app.ui.CalendarFragment.DailyAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0019\u0010\u000b\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0019\u0010\r\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u000f"}, d2 = {"Lcom/money/app/ui/CalendarFragment$DailyAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "<init>", "(Lcom/money/app/ui/CalendarFragment$DailyAdapter;Landroid/view/View;)V", "tvTitle", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "getTvTitle", "()Landroid/widget/TextView;", "tvAmount", "getTvAmount", "tvDate", "getTvDate", "app_debug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            private final android.widget.TextView tvTitle = null;
            private final android.widget.TextView tvAmount = null;
            private final android.widget.TextView tvDate = null;
            
            public ViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View v) {
                super(null);
            }
            
            public final android.widget.TextView getTvTitle() {
                return null;
            }
            
            public final android.widget.TextView getTvAmount() {
                return null;
            }
            
            public final android.widget.TextView getTvDate() {
                return null;
            }
        }
    }
}