package com.money.app.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\u0018\u00002\u00020\u0001:\u0001&B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014J\b\u0010\u001a\u001a\u00020\u0017H\u0002J\u0010\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u001c\u001a\u00020\u0010H\u0002J\b\u0010\u001d\u001a\u00020\u0017H\u0002J\b\u0010\u001e\u001a\u00020\u0017H\u0002J\u0010\u0010\u001f\u001a\u00020\u00172\u0006\u0010 \u001a\u00020\fH\u0002J\u0010\u0010!\u001a\u00020\u00172\u0006\u0010 \u001a\u00020\fH\u0002J\u0010\u0010\"\u001a\u00020\u00172\u0006\u0010\u001c\u001a\u00020\u0010H\u0002J\b\u0010#\u001a\u00020\u0017H\u0002J\u0010\u0010$\u001a\u00020\u00172\u0006\u0010%\u001a\u00020\u0010H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0012\u0010\r\u001a\u00060\u000eR\u00020\u0000X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0010X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/money/app/ui/AIChatActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "rvChat", "Landroidx/recyclerview/widget/RecyclerView;", "etChat", "Landroid/widget/EditText;", "btnSend", "Landroid/widget/ImageButton;", "chatMessages", "", "Lcom/money/app/data/ChatMessage;", "adapter", "Lcom/money/app/ui/AIChatActivity$ChatAdapter;", "GEMINI_API_KEY", "", "CHATGPT_API_KEY", "lastProposedFundName", "lastProposedAmount", "", "lastProposedEmoji", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupSuggestions", "sendMessage", "query", "calculateStats", "loadChatHistory", "addMessageToUI", "msg", "saveMessageToDB", "processAI", "createProposedFund", "callGeminiAPI", "prompt", "ChatAdapter", "app_debug"})
public final class AIChatActivity extends androidx.appcompat.app.AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView rvChat;
    private android.widget.EditText etChat;
    private android.widget.ImageButton btnSend;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.money.app.data.ChatMessage> chatMessages = null;
    private com.money.app.ui.AIChatActivity.ChatAdapter adapter;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String GEMINI_API_KEY = "AQ.Ab8RN6LYB28S5p69FS6i0gyOO9ZWSmCDSSBGhKS_SWoGCpzVag";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String CHATGPT_API_KEY = "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";
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastProposedFundName;
    private double lastProposedAmount = 0.0;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String lastProposedEmoji = "\ud83d\udcb0";
    
    public AIChatActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupSuggestions() {
    }
    
    private final void sendMessage(java.lang.String query) {
    }
    
    private final void calculateStats() {
    }
    
    private final void loadChatHistory() {
    }
    
    private final void addMessageToUI(com.money.app.data.ChatMessage msg) {
    }
    
    private final void saveMessageToDB(com.money.app.data.ChatMessage msg) {
    }
    
    private final void processAI(java.lang.String query) {
    }
    
    private final void createProposedFund() {
    }
    
    private final void callGeminiAPI(java.lang.String prompt) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0086\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0014B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ \u0010\t\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0010\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\rH\u0016J \u0010\u0010\u001a\u00020\u00112\u000e\u0010\u0012\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u000f\u001a\u00020\rH\u0016J\b\u0010\u0013\u001a\u00020\rH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/money/app/ui/AIChatActivity$ChatAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/money/app/ui/AIChatActivity$ChatAdapter$ViewHolder;", "Lcom/money/app/ui/AIChatActivity;", "list", "", "Lcom/money/app/data/ChatMessage;", "<init>", "(Lcom/money/app/ui/AIChatActivity;Ljava/util/List;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "getItemViewType", "position", "onBindViewHolder", "", "holder", "getItemCount", "ViewHolder", "app_debug"})
    public final class ChatAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.money.app.ui.AIChatActivity.ChatAdapter.ViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private final java.util.List<com.money.app.data.ChatMessage> list = null;
        
        public ChatAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<com.money.app.data.ChatMessage> list) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.money.app.ui.AIChatActivity.ChatAdapter.ViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public int getItemViewType(int position) {
            return 0;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.money.app.ui.AIChatActivity.ChatAdapter.ViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0019\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lcom/money/app/ui/AIChatActivity$ChatAdapter$ViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "v", "Landroid/view/View;", "<init>", "(Lcom/money/app/ui/AIChatActivity$ChatAdapter;Landroid/view/View;)V", "tv", "Landroid/widget/TextView;", "kotlin.jvm.PlatformType", "getTv", "()Landroid/widget/TextView;", "app_debug"})
        public final class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            private final android.widget.TextView tv = null;
            
            public ViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View v) {
                super(null);
            }
            
            public final android.widget.TextView getTv() {
                return null;
            }
        }
    }
}