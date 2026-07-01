package com.money.app.ui;

/**
 * Màn hình Thống kê: Cung cấp báo cáo chi tiết về thu nhập và chi tiêu.
 * Hỗ trợ lọc theo các khoảng thời gian: Tuần, Tháng, Năm hoặc Tất cả.
 * Hiển thị các thẻ tóm tắt và danh sách giao dịch hoặc dữ liệu được gom nhóm.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0003%&\'B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0014J\b\u0010\u0011\u001a\u00020\u000eH\u0002J\b\u0010\u0012\u001a\u00020\u000eH\u0002J\u0010\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\nH\u0002J\b\u0010\u0015\u001a\u00020\u000eH\u0002J\u001c\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0017H\u0002J$\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00180\u00172\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00172\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\u0018\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001f\u001a\u00020\u001dH\u0002J\u0014\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\u001d\u0012\u0004\u0012\u00020\u001d0!H\u0002J\u0010\u0010\"\u001a\u00020\u000e2\u0006\u0010#\u001a\u00020$H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/money/app/ui/StatisticsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/money/app/databinding/ActivityStatisticsBinding;", "currentCalendar", "Ljava/util/Calendar;", "kotlin.jvm.PlatformType", "selectedTabId", "", "isExpenseMode", "", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupSummaryCards", "updateModeToggleUI", "navigateDate", "direction", "loadData", "aggregateByWeek", "", "Lcom/money/app/ui/StatisticsActivity$AggregateItem;", "transactions", "Lcom/money/app/data/Transaction;", "aggregateByMonth", "start", "", "updateDateRangeText", "end", "getTimeRange", "Lkotlin/Pair;", "showFullImage", "path", "", "AggregateItem", "AggregateAdapter", "StatsListAdapter", "app_debug"})
public final class StatisticsActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.money.app.databinding.ActivityStatisticsBinding binding;
    private java.util.Calendar currentCalendar;
    private int selectedTabId = com.money.app.R.id.rbWeek;
    private boolean isExpenseMode = true;
    
    public StatisticsActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    /**
     * Thiết lập tiêu đề và icon cho 3 thẻ tóm tắt: Chi tiêu, Thu nhập và Số dư
     */
    private final void setupSummaryCards() {
    }
    
    /**
     * Cập nhật giao diện của nút gạt Chi tiêu/Thu nhập
     */
    private final void updateModeToggleUI() {
    }
    
    /**
     * Thay đổi thời gian đang xem dựa trên hướng (tiến/lùi) và Tab đang chọn
     */
    private final void navigateDate(int direction) {
    }
    
    /**
     * Tải dữ liệu từ database, tính toán các chỉ số tóm tắt và cập nhật RecyclerView
     */
    private final void loadData() {
    }
    
    /**
     * Gom nhóm các giao dịch theo từng tuần trong tháng để xem báo cáo tổng quát theo tuần
     */
    private final java.util.List<com.money.app.ui.StatisticsActivity.AggregateItem> aggregateByWeek(java.util.List<com.money.app.data.Transaction> transactions) {
        return null;
    }
    
    /**
     * Gom nhóm các giao dịch theo từng tháng trong năm
     */
    private final java.util.List<com.money.app.ui.StatisticsActivity.AggregateItem> aggregateByMonth(java.util.List<com.money.app.data.Transaction> transactions, long start) {
        return null;
    }
    
    /**
     * Định dạng và hiển thị văn bản khoảng thời gian đang được xem
     */
    private final void updateDateRangeText(long start, long end) {
    }
    
    /**
     * Tính toán thời điểm bắt đầu và kết thúc (timestamp) dựa trên lựa chọn Tab và currentCalendar
     */
    private final kotlin.Pair<java.lang.Long, java.lang.Long> getTimeRange() {
        return null;
    }
    
    /**
     * Hiển thị ảnh hóa đơn ở chế độ toàn màn hình trong một Dialog
     */
    private final void showFullImage(java.lang.String path) {
    }
    
    /**
     * Adapter cho báo cáo tổng hợp (theo tuần/tháng)
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u001aB1\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\n\u00a2\u0006\u0004\b\r\u0010\u000eJ \u0010\u0011\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0016J \u0010\u0016\u001a\u00020\f2\u000e\u0010\u0017\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0018\u001a\u00020\u0015H\u0016J\b\u0010\u0019\u001a\u00020\u0015H\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001b"}, d2 = {"Lcom/money/app/ui/StatisticsActivity$AggregateAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/money/app/ui/StatisticsActivity$AggregateAdapter$ViewHolder;", "Lcom/money/app/ui/StatisticsActivity;", "items", "", "Lcom/money/app/ui/StatisticsActivity$AggregateItem;", "typeLabel", "", "onClick", "Lkotlin/Function1;", "", "", "<init>", "(Lcom/money/app/ui/StatisticsActivity;Ljava/util/List;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "getOnClick", "()Lkotlin/jvm/functions/Function1;", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "holder", "position", "getItemCount", "ViewHolder", "app_debug"})
    public final class AggregateAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.money.app.ui.StatisticsActivity.AggregateAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.money.app.ui.StatisticsActivity.AggregateItem> items = null;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String typeLabel = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<java.lang.Long, kotlin.Unit> onClick = null;
        
        public AggregateAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<com.money.app.ui.StatisticsActivity.AggregateItem> items, @org.jetbrains.annotations.NotNull()
        java.lang.String typeLabel, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onClick) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function1<java.lang.Long, kotlin.Unit> getOnClick() {
            return null;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.money.app.ui.StatisticsActivity.AggregateAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.money.app.ui.StatisticsActivity.AggregateAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0019\u0010\u000b\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0019\u0010\r\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u000f"}, d2 = {"Lcom/money/app/ui/StatisticsActivity$AggregateAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "<init>", "(Lcom/money/app/ui/StatisticsActivity$AggregateAdapter;Landroid/view/View;)V", "tvTitle", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "getTvTitle", "()Landroid/widget/TextView;", "tvAmount", "getTvAmount", "tvDateTime", "getTvDateTime", "app_debug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            private final android.widget.TextView tvTitle = null;
            private final android.widget.TextView tvAmount = null;
            private final android.widget.TextView tvDateTime = null;
            
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
            
            public final android.widget.TextView getTvDateTime() {
                return null;
            }
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003J\'\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001a"}, d2 = {"Lcom/money/app/ui/StatisticsActivity$AggregateItem;", "", "label", "", "amount", "", "timestamp", "", "<init>", "(Ljava/lang/String;DJ)V", "getLabel", "()Ljava/lang/String;", "getAmount", "()D", "getTimestamp", "()J", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class AggregateItem {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String label = null;
        private final double amount = 0.0;
        private final long timestamp = 0L;
        
        public AggregateItem(@org.jetbrains.annotations.NotNull()
        java.lang.String label, double amount, long timestamp) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getLabel() {
            return null;
        }
        
        public final double getAmount() {
            return 0.0;
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        public final double component2() {
            return 0.0;
        }
        
        public final long component3() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.money.app.ui.StatisticsActivity.AggregateItem copy(@org.jetbrains.annotations.NotNull()
        java.lang.String label, double amount, long timestamp) {
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
     * Adapter cho danh sách chi tiết các giao dịch
     */
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0013B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ \u0010\t\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u000e\u001a\u00020\u000f2\u000e\u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\rH\u0016J\b\u0010\u0012\u001a\u00020\rH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/money/app/ui/StatisticsActivity$StatsListAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/money/app/ui/StatisticsActivity$StatsListAdapter$ViewHolder;", "Lcom/money/app/ui/StatisticsActivity;", "list", "", "Lcom/money/app/data/Transaction;", "<init>", "(Lcom/money/app/ui/StatisticsActivity;Ljava/util/List;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "", "holder", "position", "getItemCount", "ViewHolder", "app_debug"})
    public final class StatsListAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.money.app.ui.StatisticsActivity.StatsListAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.money.app.data.Transaction> list = null;
        
        public StatsListAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<com.money.app.data.Transaction> list) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.money.app.ui.StatisticsActivity.StatsListAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.money.app.ui.StatisticsActivity.StatsListAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0019\u0010\u000b\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0019\u0010\r\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\nR\u0019\u0010\u000f\u001a\n \b*\u0004\u0018\u00010\u00030\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0019\u0010\u0012\u001a\n \b*\u0004\u0018\u00010\u00130\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006\u0016"}, d2 = {"Lcom/money/app/ui/StatisticsActivity$StatsListAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "<init>", "(Lcom/money/app/ui/StatisticsActivity$StatsListAdapter;Landroid/view/View;)V", "tvTitle", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "getTvTitle", "()Landroid/widget/TextView;", "tvAmount", "getTvAmount", "tvDateTime", "getTvDateTime", "cvPhoto", "getCvPhoto", "()Landroid/view/View;", "ivPhoto", "Landroid/widget/ImageView;", "getIvPhoto", "()Landroid/widget/ImageView;", "app_debug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            private final android.widget.TextView tvTitle = null;
            private final android.widget.TextView tvAmount = null;
            private final android.widget.TextView tvDateTime = null;
            private final android.view.View cvPhoto = null;
            private final android.widget.ImageView ivPhoto = null;
            
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
            
            public final android.widget.TextView getTvDateTime() {
                return null;
            }
            
            public final android.view.View getCvPhoto() {
                return null;
            }
            
            public final android.widget.ImageView getIvPhoto() {
                return null;
            }
        }
    }
}