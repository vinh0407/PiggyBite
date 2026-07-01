package com.money.app.data;

/**
 * Lớp Entity đại diện cho một Quỹ (Fund) - có thể là quỹ tiết kiệm cá nhân hoặc quỹ chung.
 * Chứa thông tin về số tiền hiện có, mục tiêu và các thành viên tham gia đóng góp.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010$\n\u0002\b.\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u008f\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0005\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000f\u0012\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\u0012\u0012\u0014\b\u0002\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u0014\u00a2\u0006\u0004\b\u0015\u0010\u0016J\t\u00102\u001a\u00020\u0003H\u00c6\u0003J\t\u00103\u001a\u00020\u0005H\u00c6\u0003J\t\u00104\u001a\u00020\u0005H\u00c6\u0003J\t\u00105\u001a\u00020\u0005H\u00c6\u0003J\t\u00106\u001a\u00020\tH\u00c6\u0003J\t\u00107\u001a\u00020\tH\u00c6\u0003J\t\u00108\u001a\u00020\u0005H\u00c6\u0003J\t\u00109\u001a\u00020\u0003H\u00c6\u0003J\t\u0010:\u001a\u00020\u0003H\u00c6\u0003J\t\u0010;\u001a\u00020\u000fH\u00c6\u0003J\t\u0010<\u001a\u00020\u000fH\u00c6\u0003J\u000f\u0010=\u001a\b\u0012\u0004\u0012\u00020\u00050\u0012H\u00c6\u0003J\u0015\u0010>\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u0014H\u00c6\u0003J\u009d\u0001\u0010?\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\u00122\u0014\b\u0002\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u0014H\u00c6\u0001J\u0013\u0010@\u001a\u00020\u000f2\b\u0010A\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010B\u001a\u00020CH\u00d6\u0001J\t\u0010D\u001a\u00020\u0005H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001aR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001aR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 R\u001a\u0010\n\u001a\u00020\tX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\u001e\"\u0004\b\"\u0010 R\u0011\u0010\u000b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001aR\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0018R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u0018R\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010&\"\u0004\b\'\u0010(R\u001a\u0010\u0010\u001a\u00020\u000fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010&\"\u0004\b)\u0010(R \u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-R&\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\t0\u0014X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b.\u0010/\"\u0004\b0\u00101\u00a8\u0006E"}, d2 = {"Lcom/money/app/data/Fund;", "", "id", "", "syncId", "", "ownerId", "name", "currentAmount", "", "targetAmount", "icon", "createdDate", "endDate", "isPinned", "", "isShared", "members", "", "memberContributions", "", "<init>", "(JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;DDLjava/lang/String;JJZZLjava/util/List;Ljava/util/Map;)V", "getId", "()J", "getSyncId", "()Ljava/lang/String;", "getOwnerId", "getName", "getCurrentAmount", "()D", "setCurrentAmount", "(D)V", "getTargetAmount", "setTargetAmount", "getIcon", "getCreatedDate", "getEndDate", "()Z", "setPinned", "(Z)V", "setShared", "getMembers", "()Ljava/util/List;", "setMembers", "(Ljava/util/List;)V", "getMemberContributions", "()Ljava/util/Map;", "setMemberContributions", "(Ljava/util/Map;)V", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "funds", indices = {@androidx.room.Index(value = {"syncId"}, unique = true)})
public final class Fund {
    @androidx.room.PrimaryKey(autoGenerate = true)
    private final long id = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String syncId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String ownerId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String name = null;
    private double currentAmount;
    private double targetAmount;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String icon = null;
    private final long createdDate = 0L;
    private final long endDate = 0L;
    private boolean isPinned;
    private boolean isShared;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<java.lang.String> members;
    @org.jetbrains.annotations.NotNull()
    private java.util.Map<java.lang.String, java.lang.Double> memberContributions;
    
    public Fund(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String syncId, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.NotNull()
    java.lang.String name, double currentAmount, double targetAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String icon, long createdDate, long endDate, boolean isPinned, boolean isShared, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> members, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> memberContributions) {
        super();
    }
    
    public final long getId() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSyncId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getOwnerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getName() {
        return null;
    }
    
    public final double getCurrentAmount() {
        return 0.0;
    }
    
    public final void setCurrentAmount(double p0) {
    }
    
    public final double getTargetAmount() {
        return 0.0;
    }
    
    public final void setTargetAmount(double p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getIcon() {
        return null;
    }
    
    public final long getCreatedDate() {
        return 0L;
    }
    
    public final long getEndDate() {
        return 0L;
    }
    
    public final boolean isPinned() {
        return false;
    }
    
    public final void setPinned(boolean p0) {
    }
    
    public final boolean isShared() {
        return false;
    }
    
    public final void setShared(boolean p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getMembers() {
        return null;
    }
    
    public final void setMembers(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Double> getMemberContributions() {
        return null;
    }
    
    public final void setMemberContributions(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> p0) {
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final boolean component10() {
        return false;
    }
    
    public final boolean component11() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component12() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, java.lang.Double> component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    public final double component5() {
        return 0.0;
    }
    
    public final double component6() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    public final long component8() {
        return 0L;
    }
    
    public final long component9() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.money.app.data.Fund copy(long id, @org.jetbrains.annotations.NotNull()
    java.lang.String syncId, @org.jetbrains.annotations.NotNull()
    java.lang.String ownerId, @org.jetbrains.annotations.NotNull()
    java.lang.String name, double currentAmount, double targetAmount, @org.jetbrains.annotations.NotNull()
    java.lang.String icon, long createdDate, long endDate, boolean isPinned, boolean isShared, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> members, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Double> memberContributions) {
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