package com.money.app.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.money.app.data.AppDatabase
import com.money.app.data.Fund
import com.money.app.data.Transaction
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FirebaseSyncManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    private val appDb = AppDatabase.getDatabase(context)

    private fun encodeEmail(email: String): String {
        return email.replace(".", "_dot_")
    }

    suspend fun syncTransactions() {
        val userId = auth.currentUser?.uid ?: return
        try {
            // 1. Check for pending refunds
            checkPendingRefunds(userId)

            // 2. Sync transactions
            val snapshot = db.child("users").child(userId).child("transactions").get().await()
            val localTransactions = appDb.transactionDao().getAllTransactions()

            snapshot.children.forEach { child ->
                val syncId = child.key ?: return@forEach
                val amount = child.child("amount").value as? String ?: "0"
                val timestamp = child.child("timestamp").value as? Long ?: 0L
                val category = child.child("category").value as? String ?: ""

                val existingBySyncId = localTransactions.find { it.syncId == syncId }
                
                if (existingBySyncId == null) {
                    val matchByContent = localTransactions.find { 
                        it.amount == amount && it.timestamp == timestamp && it.category == category 
                    }

                    if (matchByContent != null) {
                        val updated = matchByContent.copy(syncId = syncId)
                        appDb.transactionDao().insert(updated)
                    } else {
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun checkPendingRefunds(userId: String) {
        try {
            val refundSnapshot = db.child("pending_refunds").child(userId).get().await()
            if (refundSnapshot.exists()) {
                refundSnapshot.children.forEach { child ->
                    val fundName = child.child("fundName").value as? String ?: "Quỹ cũ"
                    val amount = child.child("amount").value as? Number ?: 0.0
                    
                    if (amount.toDouble() > 0) {
                        val trans = Transaction(
                            amount = amount.toLong().toString(),
                            category = "Hoàn tiền quỹ",
                            description = "Hoàn tiền từ quỹ $fundName bị giải thể",
                            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                            isExpense = false,
                            timestamp = System.currentTimeMillis()
                        )
                        appDb.transactionDao().insert(trans)
                        saveTransaction(trans)
                    }
                }
                db.child("pending_refunds").child(userId).removeValue().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncFunds() {
        val userId = auth.currentUser?.uid ?: return
        try {
            val userFundsSnapshot = db.child("users").child(userId).child("my_funds").get().await()
            val localFunds = appDb.fundDao().getAllFunds()
            
            val remoteSyncIds = userFundsSnapshot.children.mapNotNull { it.key }
            
            // 1. Delete local funds that are no longer in remote my_funds
            localFunds.forEach { local ->
                if (!remoteSyncIds.contains(local.syncId)) {
                    appDb.fundDao().delete(local)
                }
            }

            // 2. Add or Update funds
            userFundsSnapshot.children.forEach { child ->
                val syncId = child.key ?: return@forEach
                val fundSnapshot = db.child("funds").child(syncId).get().await()
                
                // If fund was deleted from the global 'funds' table, remove it from user's 'my_funds' as well
                if (!fundSnapshot.exists()) {
                    db.child("users").child(userId).child("my_funds").child(syncId).removeValue().await()
                    val localMatch = localFunds.find { it.syncId == syncId }
                    if (localMatch != null) appDb.fundDao().delete(localMatch)
                    return@forEach
                }

                val name = fundSnapshot.child("name").value as? String ?: ""
                
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
                    name = name,
                    currentAmount = (fundSnapshot.child("currentAmount").value as? Number)?.toDouble() ?: 0.0,
                    targetAmount = (fundSnapshot.child("targetAmount").value as? Number)?.toDouble() ?: 0.0,
                    icon = fundSnapshot.child("icon").value as? String ?: "ic_fund",
                    createdDate = fundSnapshot.child("createdDate").value as? Long ?: System.currentTimeMillis(),
                    endDate = fundSnapshot.child("endDate").value as? Long ?: 0L,
                    isPinned = fundSnapshot.child("isPinned").value as? Boolean ?: false,
                    isShared = members.size > 1,
                    members = members,
                    memberContributions = contributions
                )
                
                appDb.fundDao().insert(fund) // REPLACE strategy will update if exists
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
            e.printStackTrace()
        }
    }

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
            
            val membersMap = mutableMapOf<String, Boolean>()
            fund.members.ifEmpty { listOf(userId) }.forEach { membersMap[it] = true }
            fundData["members"] = membersMap

            val contributionsMap = fund.memberContributions.toMutableMap()
            if (contributionsMap.isEmpty() && fund.currentAmount > 0) {
                contributionsMap[userId] = fund.currentAmount
            }
            fundData["contributions"] = contributionsMap

            db.child("funds").child(fund.syncId).setValue(fundData).await()
            
            // Link to all members
            fund.members.ifEmpty { listOf(userId) }.forEach { memberId ->
                db.child("users").child(memberId).child("my_funds").child(fund.syncId).setValue(true).await()
            }
            
            fund.syncId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun shareFund(fundId: String, familyMemberEmail: String) {
        try {
            val usersSnapshot = db.child("users").get().await()
            var memberUid: String? = null
            
            for (userSnap in usersSnapshot.children) {
                val email = userSnap.child("profile/email").value as? String
                val phone = userSnap.child("profile/phone").value as? String
                // Search by email OR phone
                if (email?.lowercase() == familyMemberEmail.lowercase() || phone == familyMemberEmail) {
                    memberUid = userSnap.key
                    break
                }
            }
            
            if (memberUid != null) {
                db.child("funds").child(fundId).child("members").child(memberUid).setValue(true).await()
                db.child("users").child(memberUid).child("my_funds").child(fundId).setValue(true).await()
            } else {
                // Pending invitation
                val encodedEmail = encodeEmail(familyMemberEmail.lowercase())
                db.child("pending_invitations").child(encodedEmail).child(fundId).setValue(true).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun checkPendingInvitations(emailOrPhone: String, userId: String) {
        try {
            val encodedEmail = encodeEmail(emailOrPhone.lowercase())
            // Check email invitations
            val pendingSnapshot = db.child("pending_invitations").child(encodedEmail).get().await()
            if (pendingSnapshot.exists()) {
                pendingSnapshot.children.forEach { child ->
                    val fundId = child.key ?: return@forEach
                    db.child("funds").child(fundId).child("members").child(userId).setValue(true).await()
                    db.child("users").child(userId).child("my_funds").child(fundId).setValue(true).await()
                }
                db.child("pending_invitations").child(encodedEmail).removeValue().await()
            }
            // If phone number used as identifier, it will be checked here too if encoded correctly
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun leaveFund(fund: Fund) {
        val userId = auth.currentUser?.uid ?: return
        try {
            // 1. Create a pending refund for this specific user
            val myContrib = fund.memberContributions[userId] ?: 0.0
            if (myContrib > 0) {
                val refundData = hashMapOf(
                    "fundName" to fund.name,
                    "amount" to myContrib,
                    "timestamp" to System.currentTimeMillis()
                )
                db.child("pending_refunds").child(userId).child(fund.syncId).setValue(refundData).await()
            }

            // 2. Remove user from fund members
            db.child("funds").child(fund.syncId).child("members").child(userId).removeValue().await()
            
            // 3. Update contributions list (remove this user's entry)
            db.child("funds").child(fund.syncId).child("contributions").child(userId).removeValue().await()

            // 4. Remove fund from user's my_funds
            db.child("users").child(userId).child("my_funds").child(fund.syncId).removeValue().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteFundAndRefund(fund: Fund) {
        val currentUserId = auth.currentUser?.uid ?: return
        try {
            // 1. Create pending refunds for all contributing members
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

            // 2. Remove fund link from all members
            fund.members.forEach { memberId ->
                db.child("users").child(memberId).child("my_funds").child(fund.syncId).removeValue().await()
            }

            // 3. Delete fund itself
            db.child("funds").child(fund.syncId).removeValue().await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun uploadAllLocalData() {
        val userId = auth.currentUser?.uid ?: return
        try {
            val localTransactions = appDb.transactionDao().getAllTransactions()
            localTransactions.forEach { saveTransaction(it) }

            val localFunds = appDb.fundDao().getAllFunds()
            localFunds.forEach { createFund(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearLocalData() {
        try {
            appDb.transactionDao().clearAll()
            appDb.fundDao().clearAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
