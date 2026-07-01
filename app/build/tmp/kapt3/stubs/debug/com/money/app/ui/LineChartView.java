package com.money.app.ui;

/**
 * Custom View vẽ biểu đồ đường (Line Chart).
 * Hiển thị biến động Thu nhập và Chi tiêu trong 7 ngày của tuần hiện tại.
 * Tính năng đặc biệt: Nhấn giữ lên biểu đồ để xem chi tiết số tiền của từng ngày qua Tooltip.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\u0016\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u001b\u001a\u00020\u000fJ\u0010\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\u001eH\u0014JH\u0010\u001f\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010 \u001a\u00020\u00152\u0006\u0010!\u001a\u00020\u00152\u0006\u0010\"\u001a\u00020\u00152\u0006\u0010#\u001a\u00020\u00152\u0006\u0010$\u001a\u00020\u00152\u0006\u0010%\u001a\u00020\u00152\u0006\u0010&\u001a\u00020\u0015H\u0002J\u0010\u0010\'\u001a\u00020\u00172\u0006\u0010(\u001a\u00020)H\u0016R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/money/app/ui/LineChartView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "defStyleAttr", "", "<init>", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "spendPaint", "Landroid/graphics/Paint;", "incomePaint", "textPaint", "spendData", "", "incomeData", "labels", "", "", "touchX", "", "isLongPress", "", "setData", "", "spends", "incomes", "onDraw", "canvas", "Landroid/graphics/Canvas;", "drawTooltip", "tx", "w", "stepX", "padding", "maxVal", "chartH", "h", "onTouchEvent", "event", "Landroid/view/MotionEvent;", "app_debug"})
public final class LineChartView extends android.view.View {
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint spendPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint incomePaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint textPaint = null;
    @org.jetbrains.annotations.NotNull()
    private float[] spendData;
    @org.jetbrains.annotations.NotNull()
    private float[] incomeData;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> labels = null;
    private float touchX = -1.0F;
    private boolean isLongPress = false;
    
    @kotlin.jvm.JvmOverloads()
    public LineChartView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null);
    }
    
    @kotlin.jvm.JvmOverloads()
    public LineChartView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null);
    }
    
    @kotlin.jvm.JvmOverloads()
    public LineChartView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs, int defStyleAttr) {
        super(null);
    }
    
    /**
     * Cập nhật dữ liệu mới cho biểu đồ.
     */
    public final void setData(@org.jetbrains.annotations.NotNull()
    float[] spends, @org.jetbrains.annotations.NotNull()
    float[] incomes) {
    }
    
    @java.lang.Override()
    protected void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    /**
     * Vẽ khung thông tin (Tooltip) khi người dùng nhấn giữ vào biểu đồ.
     */
    private final void drawTooltip(android.graphics.Canvas canvas, float tx, float w, float stepX, float padding, float maxVal, float chartH, float h) {
    }
    
    /**
     * Xử lý sự kiện chạm (Touch): Phát hiện nhấn giữ.
     */
    @java.lang.Override()
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent event) {
        return false;
    }
}