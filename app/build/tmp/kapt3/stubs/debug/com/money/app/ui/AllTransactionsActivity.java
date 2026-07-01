package com.money.app.ui;

/**
 * Màn hình Lịch sử Giao dịch: Hiển thị toàn bộ các khoản thu/chi đã thực hiện.
 * Cho phép người dùng:
 * - Xem danh sách chi tiết tất cả giao dịch theo thứ tự thời gian mới nhất.
 * - Xem ảnh hóa đơn đính kèm.
 * - Nhấn giữ để Xóa một giao dịch khỏi hệ thống.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/money/app/ui/AllTransactionsActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "rv", "Landroidx/recyclerview/widget/RecyclerView;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "TransactionsAdapter", "app_debug"})
public final class AllTransactionsActivity extends androidx.appcompat.app.AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView rv;
    
    public AllTransactionsActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0018B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ \u0010\t\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u000e\u001a\u00020\u000f2\u000e\u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\rH\u0016J\u0018\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0006H\u0002J\u0010\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0015\u001a\u00020\u0006H\u0002J\b\u0010\u0017\u001a\u00020\rH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/money/app/ui/AllTransactionsActivity$TransactionsAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/money/app/ui/AllTransactionsActivity$TransactionsAdapter$ViewHolder;", "Lcom/money/app/ui/AllTransactionsActivity;", "list", "", "Lcom/money/app/data/Transaction;", "<init>", "(Lcom/money/app/ui/AllTransactionsActivity;Ljava/util/List;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "", "holder", "position", "showPopupMenu", "view", "Landroid/view/View;", "transaction", "deleteTransaction", "getItemCount", "ViewHolder", "app_debug"})
    public final class TransactionsAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.money.app.ui.AllTransactionsActivity.TransactionsAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.money.app.data.Transaction> list = null;
        
        public TransactionsAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<com.money.app.data.Transaction> list) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.money.app.ui.AllTransactionsActivity.TransactionsAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.money.app.ui.AllTransactionsActivity.TransactionsAdapter.ViewHolder holder, int position) {
        }
        
        /**
         * Hiển thị Menu tùy chọn khi nhấn giữ một dòng giao dịch
         */
        private final void showPopupMenu(android.view.View view, com.money.app.data.Transaction transaction) {
        }
        
        /**
         * Xử lý xóa giao dịch khỏi database cục bộ
         */
        private final void deleteTransaction(com.money.app.data.Transaction transaction) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0019\u0010\u000b\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0019\u0010\r\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\nR\u0019\u0010\u000f\u001a\n \b*\u0004\u0018\u00010\u00030\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0019\u0010\u0012\u001a\n \b*\u0004\u0018\u00010\u00130\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015\u00a8\u0006\u0016"}, d2 = {"Lcom/money/app/ui/AllTransactionsActivity$TransactionsAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "<init>", "(Lcom/money/app/ui/AllTransactionsActivity$TransactionsAdapter;Landroid/view/View;)V", "tvTitle", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "getTvTitle", "()Landroid/widget/TextView;", "tvAmount", "getTvAmount", "tvDateTime", "getTvDateTime", "cvPhoto", "getCvPhoto", "()Landroid/view/View;", "ivPhoto", "Landroid/widget/ImageView;", "getIvPhoto", "()Landroid/widget/ImageView;", "app_debug"})
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