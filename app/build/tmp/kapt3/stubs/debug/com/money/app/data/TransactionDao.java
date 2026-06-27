package com.money.app.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\nH\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J$\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\n2\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0010J$\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\n2\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001c\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\n2\u0006\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\b\u00a8\u0006\u0017\u00c0\u0006\u0003"}, d2 = {"Lcom/money/app/data/TransactionDao;", "", "insert", "", "transaction", "Lcom/money/app/data/Transaction;", "(Lcom/money/app/data/Transaction;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestTransaction", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllTransactions", "", "delete", "", "getExpensesInTimeRange", "startTime", "endTime", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getTransactionsInTimeRange", "searchTransactions", "query", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearAll", "app_debug"})
@androidx.room.Dao()
public abstract interface TransactionDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.money.app.data.Transaction transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatestTransaction(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.money.app.data.Transaction> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM transactions ORDER BY timestamp DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllTransactions(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.app.data.Transaction>> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.money.app.data.Transaction transaction, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE isExpense = 1 AND timestamp >= :startTime AND timestamp <= :endTime")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getExpensesInTimeRange(long startTime, long endTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.app.data.Transaction>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTransactionsInTimeRange(long startTime, long endTime, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.app.data.Transaction>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM transactions WHERE category LIKE :query OR description LIKE :query")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchTransactions(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.money.app.data.Transaction>> $completion);
    
    @androidx.room.Query(value = "DELETE FROM transactions")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}