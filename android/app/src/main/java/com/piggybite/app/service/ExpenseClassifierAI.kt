package com.piggybite.app.service

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * AI phân loại Chi tiêu chạy 100% offline trên máy (On-Device ML).
 * Sử dụng Naive Bayes unigram+bigram, nạp sẵn bộ ngữ liệu chi tiêu thực tế
 * và tự học thêm khi người dùng nhập/chỉnh sửa giao dịch chi tiêu.
 */
class ExpenseClassifierAI private constructor(private val context: Context) {

    private val gson = Gson()
    private val model = IncomeNaiveBayes() // Tái sử dụng engine Bayes tối ưu
    private val userFile = File(context.filesDir, "ai_expense_user_model.json")
    private val lock = Any()

    val categories = listOf(
        CAT_FOOD,
        CAT_SHOPPING,
        CAT_TRANSPORT,
        CAT_BILLS,
        CAT_ENTERTAINMENT,
        CAT_HEALTH,
        CAT_OTHER
    )

    private val corpusLabels = mapOf(
        "ANUONG" to CAT_FOOD,
        "MUASAM" to CAT_SHOPPING,
        "DICHUYEN" to CAT_TRANSPORT,
        "HOADON" to CAT_BILLS,
        "GIAITRI" to CAT_ENTERTAINMENT,
        "SUCKHOE" to CAT_HEALTH,
        "KHAC" to CAT_OTHER
    )

    private val userSamples = mutableListOf<UserSample>()
    private data class UserSample(val text: String, val category: String, val weight: Double)

    init {
        trainFromBundledCorpus()
        loadUserKnowledge()
    }

    private fun trainFromBundledCorpus() {
        try {
            context.assets.open("ai/expense_corpus.txt").bufferedReader(Charsets.UTF_8).useLines { lines ->
                lines.forEach { raw ->
                    val line = raw.trim()
                    if (line.isEmpty() || line.startsWith("#")) return@forEach
                    val sep = line.indexOf('|')
                    if (sep <= 0) return@forEach
                    val label = corpusLabels[line.substring(0, sep).trim()] ?: return@forEach
                    model.trainBase(line.substring(sep + 1), label)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadUserKnowledge() {
        if (!userFile.exists()) return
        try {
            val type = object : TypeToken<List<UserSample>>() {}.type
            val list: List<UserSample> = gson.fromJson(userFile.readText(), type) ?: emptyList()
            list.forEach { s ->
                if (s.category in categories) {
                    userSamples.add(s)
                    model.trainUser(s.text, s.category, s.weight)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveUserKnowledge() {
        try {
            val toSave = userSamples.takeLast(2000)
            userFile.writeText(gson.toJson(toSave))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun train(text: String, category: String, weight: Double = 2.0) {
        if (text.isBlank() || category.isBlank()) return
        val resolved = resolveCategory(category) ?: return
        synchronized(lock) {
            userSamples.add(UserSample(text, resolved, weight))
            model.trainUser(text, resolved, weight)
            saveUserKnowledge()
        }
    }

    fun classifyExpense(rawText: String): AIClassificationResult {
        val p = synchronized(lock) { model.predict(rawText, categories, CAT_OTHER) }
        return AIClassificationResult(
            category = p.label,
            confidence = p.confidence.toFloat(),
            matchedKeywords = p.matched
        )
    }

    private fun resolveCategory(name: String): String? {
        if (name in categories) return name
        val n = IncomeNaiveBayes.normalize(name)
        return when {
            n.contains("an uong") || n.contains("cafe") || n.contains("ca phe") || n.contains("tra sua") || n.contains("an trua") -> CAT_FOOD
            n.contains("mua sam") || n.contains("quan ao") || n.contains("shopee") || n.contains("lazada") -> CAT_SHOPPING
            n.contains("di chuyen") || n.contains("xang") || n.contains("grab") || n.contains("be") || n.contains("xe") -> CAT_TRANSPORT
            n.contains("hoa don") || n.contains("dien") || n.contains("nuoc") || n.contains("internet") || n.contains("tien nha") -> CAT_BILLS
            n.contains("giai tri") || n.contains("xem phim") || n.contains("cgv") || n.contains("netflix") || n.contains("game") -> CAT_ENTERTAINMENT
            n.contains("suc khoe") || n.contains("thuoc") || n.contains("kham") || n.contains("benh") || n.contains("y te") -> CAT_HEALTH
            n.contains("khac") -> CAT_OTHER
            else -> null
        }
    }

    companion object {
        const val CAT_FOOD = "Ăn uống & Cà phê"
        const val CAT_SHOPPING = "Mua sắm & Đồ dùng"
        const val CAT_TRANSPORT = "Di chuyển & Xăng xe"
        const val CAT_BILLS = "Hóa đơn & Tiện ích"
        const val CAT_ENTERTAINMENT = "Giải trí & Sở thích"
        const val CAT_HEALTH = "Sức khỏe & Y tế"
        const val CAT_OTHER = "Chi tiêu khác"

        const val AUTO_LEARN_THRESHOLD = 0.80f

        @Volatile
        private var instance: ExpenseClassifierAI? = null

        fun getInstance(context: Context): ExpenseClassifierAI {
            return instance ?: synchronized(this) {
                instance ?: ExpenseClassifierAI(context.applicationContext).also { instance = it }
            }
        }
    }
}
