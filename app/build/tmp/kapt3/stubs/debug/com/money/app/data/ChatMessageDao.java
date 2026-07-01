package com.money.app.data;

/**
 * Interface cung cấp các phương thức truy vấn cho lịch sử Chat.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0005J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0004H\u00a7@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\n\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\u0005\u00a8\u0006\u000b\u00c0\u0006\u0003"}, d2 = {"Lcom/money/app/data/ChatMessageDao;", "", "getAllMessages", "", "Lcom/money/app/data/ChatMessage;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "", "message", "(Lcom/money/app/data/ChatMessage;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearHistory", "app_debug"})
@androidx.room.Dao()
public abstract interface ChatMessageDao {
    
    @androidx.room.Query(value = "SELECT * FROM chat_messages ORDER BY timestamp ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllMessages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.app.data.ChatMessage>> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.money.app.data.ChatMessage message, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM chat_messages")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}