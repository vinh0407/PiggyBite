package com.money.app.service

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class AIClassificationResult(
    val category: String,
    val confidence: Float, // 0.0 to 1.0
    val matchedKeywords: List<String>
)

/**
 * AI phân loại thu nhập chạy hoàn toàn trên máy (offline).
 *
 * 1. Huấn luyện sẵn: khi khởi động, mô hình học từ bộ dữ liệu đóng gói trong APK
 *    (assets/ai/income_corpus.txt) -> chính xác ngay từ lần đầu dùng.
 * 2. Tự học: mỗi giao dịch thu nhập người dùng nhập tay (độ tin cậy cao) và mỗi thông báo
 *    mà AI phân loại chắc chắn sẽ được học thêm. Kiến thức này lưu riêng (filesDir/ai_user_model.json)
 *    nên khi cập nhật app với bộ dữ liệu mới, phần đã học của người dùng không bị mất.
 */
class IncomeClassifierAI private constructor(private val context: Context) {

    private val gson = Gson()
    private val model = IncomeNaiveBayes()
    private val userFile = File(context.filesDir, "ai_user_model.json")
    private val lock = Any()

    val categories = listOf(
        CAT_SALARY,
        CAT_BUSINESS,
        CAT_BONUS,
        CAT_INVEST,
        CAT_GIFT,
        CAT_OTHER
    )

    private val corpusLabels = mapOf(
        "LUONG" to CAT_SALARY,
        "KINHDOANH" to CAT_BUSINESS,
        "THUONG" to CAT_BONUS,
        "DAUTU" to CAT_INVEST,
        "TANG" to CAT_GIFT,
        "KHAC" to CAT_OTHER
    )

    /** Lịch sử tự học (để có thể lưu/khôi phục lại chính xác). */
    private val userSamples = mutableListOf<UserSample>()

    private data class UserSample(val text: String, val category: String, val weight: Double)

    init {
        trainFromBundledCorpus()
        loadUserKnowledge()
    }

    private fun trainFromBundledCorpus() {
        try {
            context.assets.open("ai/income_corpus.txt").bufferedReader(Charsets.UTF_8).useLines { lines ->
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
            // Giữ tối đa 2000 mẫu gần nhất để file luôn nhỏ gọn
            val toSave = userSamples.takeLast(MAX_USER_SAMPLES)
            userFile.writeText(gson.toJson(toSave))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Tự học từ một ví dụ. [category] có thể là tên danh mục của AI hoặc tên ngắn
     * từ màn hình nhập tay ("Lương", "Thưởng", "Kinh doanh", "Đầu tư", "Được cho/tặng", "Khác").
     */
    fun train(text: String, category: String, weight: Double = 2.0) {
        if (text.isBlank() || category.isBlank()) return
        val resolved = resolveCategory(category) ?: return
        synchronized(lock) {
            userSamples.add(UserSample(text, resolved, weight))
            model.trainUser(text, resolved, weight)
            saveUserKnowledge()
        }
    }

    fun classifyIncome(rawText: String): AIClassificationResult {
        val p = synchronized(lock) { model.predict(rawText, categories, CAT_OTHER) }
        return AIClassificationResult(
            category = p.label,
            confidence = p.confidence.toFloat(),
            matchedKeywords = p.matched
        )
    }

    /** Xóa phần tự học, quay về mô hình huấn luyện sẵn. */
    fun resetUserKnowledge() {
        synchronized(lock) {
            userSamples.clear()
            model.resetUser()
            userFile.delete()
        }
    }

    private fun resolveCategory(name: String): String? {
        if (name in categories) return name
        val n = IncomeNaiveBayes.normalize(name)
        return when {
            n.contains("luong") || n.contains("thu nhap chinh") -> CAT_SALARY
            n.contains("kinh doanh") || n.contains("ban hang") -> CAT_BUSINESS
            n.contains("thuong") || n.contains("hoa hong") -> CAT_BONUS
            n.contains("dau tu") || n.contains("lai") -> CAT_INVEST
            n.contains("tang") || n.contains("cho") || n.contains("bieu") || n.contains("tra no") -> CAT_GIFT
            n.contains("khac") -> CAT_OTHER
            else -> null
        }
    }

    companion object {
        const val CAT_SALARY = "Lương & Thu nhập chính"
        const val CAT_BUSINESS = "Kinh doanh & Bán hàng"
        const val CAT_BONUS = "Thưởng & Hoa hồng"
        const val CAT_INVEST = "Đầu tư & Tiền lãi"
        const val CAT_GIFT = "Được biếu tặng & Trả nợ"
        const val CAT_OTHER = "Thu nhập khác"

        /** Chỉ tự học từ thông báo khi AI đủ chắc chắn, tránh tự củng cố dự đoán sai. */
        const val AUTO_LEARN_THRESHOLD = 0.80f
        private const val MAX_USER_SAMPLES = 2000

        @Volatile
        private var instance: IncomeClassifierAI? = null

        fun getInstance(context: Context): IncomeClassifierAI {
            return instance ?: synchronized(this) {
                instance ?: IncomeClassifierAI(context.applicationContext).also { instance = it }
            }
        }
    }
}
