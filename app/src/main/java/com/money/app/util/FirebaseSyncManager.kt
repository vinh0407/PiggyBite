package com.money.app.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.money.app.data.AppDatabase
import com.money.app.data.Fund
import com.money.app.data.Transaction
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

/**
 * Quản lý Đồng bộ hóa Firebase (Sync Manager): Thành phần trung tâm điều phối dòng chảy dữ liệu
 * giữa cơ sở dữ liệu SQLite cục bộ (Room) và Firebase Realtime Database.
 * 
 * Các chức năng chính:
 * - Đồng bộ hóa giao dịch: Tải dữ liệu từ mây về máy và ngược lại.
 * - Quản lý Quỹ chung: Đồng bộ trạng thái quỹ, thành viên và số tiền đóng góp.
 * - Xử lý lời mời: Tự động kết nối người dùng với quỹ chung khi họ được mời qua Email/SĐT.
 * - Quy trình hoàn tiền: Xử lý việc hoàn lại tiền vào ví khi người dùng rời quỹ hoặc quỹ bị xóa.
 */
class FirebaseSyncManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    private val appDb = AppDatabase.getDatabase(context)

    /**
     * Mã hóa Email để sử dụng làm Key trong Firebase (Thay dấu '.' bằng '_dot_')
     */
    private fun encodeEmail(email: String): String {
        return email.replace(".", "_dot_")
    }

    /**
     * Đồng bộ hóa các giao dịch từ Firebase về máy.
     */
    suspend fun syncTransactions() {
        val userId = auth.currentUser?.uid ?: return
        try {
            // 1. Kiểm tra xem có khoản hoàn tiền nào đang chờ (Vd: từ quỹ bị giải thể)
            checkPendingRefunds(userId)

            // 2. Tải danh sách giao dịch từ Firebase
            val snapshot = db.child("users").child(userId).child("transactions").get().await()
            val localTransactions = appDb.transactionDao().getAllTransactions()

            snapshot.children.forEach { child ->
                val syncId = child.key ?: return@forEach
                val amount = (child.child("amount").value as? Number)?.toDouble() ?: 0.0
                val timestamp = child.child("timestamp").value as? Long ?: 0L
                val category = child.child("category").value as? String ?: ""

                val existingBySyncId = localTransactions.find { it.syncId == syncId }
                
                if (existingBySyncId == null) {
                    // Nếu chưa có trên máy, thêm mới vào Room Database
                    val newTrans = Transaction(
                        syncId = syncId,
                        userId = userId,
                        amount = amount,
                        category = category,
                        date = child.child("date").value as? String ?: "",
                        description = child.child("description").value as? String ?: "",
                        isExpense = child.child("isExpense").value as? Boolean ?: true,
                        timestamp = timestamp
                    )
                    appDb.transactionDao().insert(newTrans)
                }
            }
        } catch (e: Exception) {
            Log.e("Sync", "Lỗi đồng bộ giao dịch: ${e.message}")
        }
    }

    /**
     * Kiểm tra các khoản "Hoàn tiền" trong nút pending_refunds trên Firebase.
     * Thường xảy ra khi một quỹ chung bị xóa, số tiền đóng góp sẽ được trả về ví chính.
     */
    private suspend fun checkPendingRefunds(userId: String) {
        try {
            val refundSnapshot = db.child("pending_refunds").child(userId).get().await()
            if (refundSnapshot.exists()) {
                refundSnapshot.children.forEach { child ->
                    val fundName = child.child("fundName").value as? String ?: "Quỹ cũ"
                    val amount = child.child("amount").value as? Number ?: 0.0
                    
                    if (amount.toDouble() > 0) {
                        val trans = Transaction(
                            amount = amount.toDouble(),
                            category = "Hoàn tiền quỹ",
                            description = "Hoàn tiền từ quỹ $fundName bị giải thể",
                            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                            isExpense = false, // Là một khoản thu nhập
                            timestamp = System.currentTimeMillis()
                        )
                        appDb.transactionDao().insert(trans)
                        saveTransaction(trans) // Lưu lại bản ghi này lên Firebase
                    }
                }
                // Xóa các thông báo hoàn tiền sau khi đã xử lý xong
                db.child("pending_refunds").child(userId).removeValue().await()
            }
        } catch (e: Exception) {}
    }

    /**
     * Đồng bộ hóa danh sách Quỹ (Funds).
     */
    suspend fun syncFunds() {
        val userId = auth.currentUser?.uid ?: return
        try {
            val userFundsSnapshot = db.child("users").child(userId).child("my_funds").get().await()
            val localFunds = appDb.fundDao().getAllFunds()
            
            val remoteSyncIds = userFundsSnapshot.children.mapNotNull { it.key }
            
            // 1. Xóa các quỹ trên máy không còn tồn tại trên mây
            localFunds.forEach { local ->
                if (!remoteSyncIds.contains(local.syncId)) {
                    appDb.fundDao().delete(local)
                }
            }

            // 2. Tải dữ liệu chi tiết của từng quỹ từ nút gốc 'funds'
            userFundsSnapshot.children.forEach { child ->
                val syncId = child.key ?: return@forEach
                val fundSnapshot = db.child("funds").child(syncId).get().await()
                
                if (!fundSnapshot.exists()) {
                    db.child("users").child(userId).child("my_funds").child(syncId).removeValue().await()
                    return@forEach
                }

                // Chuyển đổi dữ liệu Firebase sang đối tượng Fund trong Kotlin
                val members = mutableListOf<String>()
                fundSnapshot.child("members").children.forEach { memberChild ->
                    memberChild.key?.let { members.add(it) }
                }

                val contributions = mutableMapOf<String, Double>()
                fundSnapshot.child("contributions").children.forEach { contribChild ->
                    contributions[contribChild.key!!] = (contribChild.value as? Number)?.toDouble() ?: 0.0
                }

                val fund = Fund(
                    syncId = syncId,
                    ownerId = fundSnapshot.child("ownerId").value as? String ?: "",
                    name = fundSnapshot.child("name").value as? String ?: "",
                    currentAmount = (fundSnapshot.child("currentAmount").value as? Number)?.toDouble() ?: 0.0,
                    targetAmount = (fundSnapshot.child("targetAmount").value as? Number)?.toDouble() ?: 0.0,
                    icon = fundSnapshot.child("icon").value as? String ?: "🏦",
                    createdDate = fundSnapshot.child("createdDate").value as? Long ?: System.currentTimeMillis(),
                    endDate = fundSnapshot.child("endDate").value as? Long ?: 0L,
                    isPinned = fundSnapshot.child("isPinned").value as? Boolean ?: false,
                    isShared = members.size > 1,
                    members = members,
                    memberContributions = contributions
                )
                
                appDb.fundDao().insert(fund)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Lưu một giao dịch mới lên Firebase.
     */
    suspend fun saveTransaction(transaction: Transaction) {
        val userId = auth.currentUser?.uid ?: return
        try {
            val transData = hashMapOf(
                "amount" to transaction.amount,
                "category" to transaction.category,
                "date" to transaction.date,
                "description" to transaction.description,
                "isExpense" to transaction.isExpense,
                "timestamp" to transaction.timestamp
            )
            db.child("users").child(userId).child("transactions")
                .child(transaction.syncId).setValue(transData).await()
        } catch (e: Exception) {
            Log.e("Sync", "Không thể lưu giao dịch lên Firebase: ${e.message}")
        }
    }

    /**
     * Tạo một quỹ mới trên Firebase và liên kết nó với người tạo.
     */
    suspend fun createFund(fund: Fund): String? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val fundData = mutableMapOf<String, Any>(
                "ownerId" to fund.ownerId.ifEmpty { userId },
                "name" to fund.name,
                "currentAmount" to fund.currentAmount,
                "targetAmount" to fund.targetAmount,
                "icon" to fund.icon,
                "createdDate" to fund.createdDate,
                "endDate" to fund.endDate,
                "isPinned" to fund.isPinned
            )
            
            // Xử lý danh sách thành viên dưới dạng Map để tiết kiệm dung lượng
            val membersMap = mutableMapOf<String, Boolean>()
            fund.members.ifEmpty { listOf(userId) }.forEach { membersMap[it] = true }
            fundData["members"] = membersMap

            val contributionsMap = fund.memberContributions.toMutableMap()
            if (contributionsMap.isEmpty() && fund.currentAmount > 0) {
                contributionsMap[userId] = fund.currentAmount
            }
            fundData["contributions"] = contributionsMap

            db.child("funds").child(fund.syncId).setValue(fundData).await()
            
            // Ghi nhận quỹ này thuộc quyền sở hữu/quản lý của các thành viên
            fund.members.ifEmpty { listOf(userId) }.forEach { memberId ->
                db.child("users").child(memberId).child("my_funds").child(fund.syncId).setValue(true).await()
            }
            
            fund.syncId
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Mời thành viên khác vào quỹ chung thông qua Email hoặc Số điện thoại.
     */
    suspend fun shareFund(fundId: String, familyMemberEmail: String) {
        try {
            val usersSnapshot = db.child("users").get().await()
            var memberUid: String? = null
            
            // Tìm kiếm User ID của người được mời trong toàn bộ danh sách users
            for (userSnap in usersSnapshot.children) {
                val email = userSnap.child("profile/email").value as? String
                val phone = userSnap.child("profile/phone").value as? String
                if (email?.lowercase() == familyMemberEmail.lowercase() || phone == familyMemberEmail) {
                    memberUid = userSnap.key
                    break
                }
            }
            
            if (memberUid != null) {
                // Nếu người dùng đã có tài khoản, thêm họ vào quỹ ngay lập tức
                db.child("funds").child(fundId).child("members").child(memberUid).setValue(true).await()
                db.child("users").child(memberUid).child("my_funds").child(fundId).setValue(true).await()
            } else {
                // Nếu chưa có tài khoản, lưu vào danh sách chờ (Pending Invitation)
                val encodedEmail = encodeEmail(familyMemberEmail.lowercase())
                db.child("pending_invitations").child(encodedEmail).child(fundId).setValue(true).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Kiểm tra các lời mời tham gia quỹ chung đang chờ xử lý khi người dùng đăng nhập/đăng ký.
     */
    suspend fun checkPendingInvitations(emailOrPhone: String, userId: String) {
        try {
            val encodedEmail = encodeEmail(emailOrPhone.lowercase())
            val pendingSnapshot = db.child("pending_invitations").child(encodedEmail).get().await()
            if (pendingSnapshot.exists()) {
                pendingSnapshot.children.forEach { child ->
                    val fundId = child.key ?: return@forEach
                    db.child("funds").child(fundId).child("members").child(userId).setValue(true).await()
                    db.child("users").child(userId).child("my_funds").child(fundId).setValue(true).await()
                }
                db.child("pending_invitations").child(encodedEmail).removeValue().await()
            }
        } catch (e: Exception) {}
    }

    /**
     * Rời khỏi một quỹ chung và nhận lại số tiền đã đóng góp.
     */
    suspend fun leaveFund(fund: Fund) {
        val userId = auth.currentUser?.uid ?: return
        try {
            val myContrib = fund.memberContributions[userId] ?: 0.0
            if (myContrib > 0) {
                val refundData = hashMapOf(
                    "fundName" to fund.name,
                    "amount" to myContrib,
                    "timestamp" to System.currentTimeMillis()
                )
                db.child("pending_refunds").child(userId).child(fund.syncId).setValue(refundData).await()
            }
            db.child("funds").child(fund.syncId).child("members").child(userId).removeValue().await()
            db.child("funds").child(fund.syncId).child("contributions").child(userId).removeValue().await()
            db.child("users").child(userId).child("my_funds").child(fund.syncId).removeValue().await()
        } catch (e: Exception) {}
    }

    /**
     * Giải thể quỹ (Chỉ chủ quỹ thực hiện) và hoàn tiền cho toàn bộ thành viên.
     */
    suspend fun deleteFundAndRefund(fund: Fund) {
        try {
            fund.memberContributions.forEach { (memberId, amount) ->
                if (amount > 0) {
                    val refundData = hashMapOf(
                        "fundName" to fund.name,
                        "amount" to amount,
                        "timestamp" to System.currentTimeMillis()
                    )
                    db.child("pending_refunds").child(memberId).child(fund.syncId).setValue(refundData).await()
                }
            }
            fund.members.forEach { memberId ->
                db.child("users").child(memberId).child("my_funds").child(fund.syncId).removeValue().await()
            }
            db.child("funds").child(fund.syncId).removeValue().await()
        } catch (e: Exception) {}
    }

    /**
     * Đẩy toàn bộ dữ liệu từ máy lên mây (thường dùng trước khi đăng xuất).
     */
    suspend fun uploadAllLocalData() {
        try {
            val localTransactions = appDb.transactionDao().getAllTransactions()
            localTransactions.forEach { saveTransaction(it) }

            val localFunds = appDb.fundDao().getAllFunds()
            localFunds.forEach { createFund(it) }
        } catch (e: Exception) {}
    }

    /**
     * Xóa sạch dữ liệu cục bộ trên máy.
     */
    suspend fun clearLocalData() {
        try {
            appDb.transactionDao().clearAll()
            appDb.fundDao().clearAll()
        } catch (e: Exception) {}
    }
}
