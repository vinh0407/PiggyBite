package com.money.app.data;

/**
 * Lớp chuyển đổi dữ liệu để Room Database có thể lưu trữ các kiểu dữ liệu phức tạp
 * như List (Danh sách) hoặc Map (Bản đồ) dưới dạng chuỗi String.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\u0010\u0006\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\u0006H\u0007J\u0016\u0010\b\u001a\u00020\u00062\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0007J\u001c\u0010\n\u001a\u00020\u00062\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\r0\fH\u0007J\u001c\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u0007\u001a\u00020\u0006H\u0007\u00a8\u0006\u000f"}, d2 = {"Lcom/money/app/data/Converters;", "", "<init>", "()V", "fromString", "", "", "value", "fromList", "list", "fromMap", "map", "", "", "toMap", "app_debug"})
public final class Converters {
    
    public Converters() {
        super();
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> fromString(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromList(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> list) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String fromMap(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> map) {
        return null;
    }
    
    @androidx.room.TypeConverter()
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Double> toMap(@org.jetbrains.annotations.NotNull()
    java.lang.String value) {
        return null;
    }
}