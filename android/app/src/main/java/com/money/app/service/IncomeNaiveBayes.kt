package com.money.app.service

import java.text.Normalizer
import java.util.Locale
import kotlin.math.exp
import kotlin.math.ln

/**
 * Lõi AI phân loại thu nhập: Multinomial Naive Bayes với đặc trưng unigram + bigram.
 * Thuần Kotlin (không phụ thuộc Android) để có thể kiểm thử trên máy tính.
 *
 * Mô hình gồm 2 lớp kiến thức:
 *  - base: huấn luyện sẵn từ bộ dữ liệu đóng gói trong app (assets/ai/income_corpus.txt)
 *  - user: kiến thức tự học từ chính người dùng, được cộng thêm lên base
 */
class IncomeNaiveBayes {

    class Counts {
        val featureCounts = HashMap<String, HashMap<String, Double>>() // feature -> (label -> count)
        val labelDocs = HashMap<String, Double>()
        val labelTotals = HashMap<String, Double>()

        fun add(features: List<String>, label: String, weight: Double) {
            labelDocs[label] = (labelDocs[label] ?: 0.0) + weight
            for (f in features) {
                val m = featureCounts.getOrPut(f) { HashMap() }
                m[label] = (m[label] ?: 0.0) + weight
                labelTotals[label] = (labelTotals[label] ?: 0.0) + weight
            }
        }
    }

    data class Prediction(
        val label: String,
        val confidence: Double,
        val matched: List<String>,
        val probabilities: Map<String, Double>
    )

    val base = Counts()
    var user = Counts()
        private set

    fun resetUser() { user = Counts() }

    fun trainBase(text: String, label: String) = base.add(features(text), label, 1.0)

    fun trainUser(text: String, label: String, weight: Double) {
        val f = features(text)
        if (f.isNotEmpty()) user.add(f, label, weight)
    }

    fun predict(text: String, labels: List<String>, fallback: String): Prediction {
        val feats = features(text)
        if (feats.isEmpty()) return Prediction(fallback, 0.0, emptyList(), emptyMap())

        val vocab = (base.featureCounts.keys + user.featureCounts.keys).size.coerceAtLeast(1).toDouble()
        val totalDocs = labels.sumOf { docs(it) }.coerceAtLeast(1.0)
        val alpha = 0.5

        val scores = LinkedHashMap<String, Double>()
        for (label in labels) {
            var s = ln((docs(label) + 1.0) / (totalDocs + labels.size))
            val total = (base.labelTotals[label] ?: 0.0) + (user.labelTotals[label] ?: 0.0)
            for (f in feats) {
                s += ln((count(f, label) + alpha) / (total + alpha * vocab))
            }
            scores[label] = s
        }

        // Softmax -> xác suất
        val max = scores.values.max()
        val exps = scores.mapValues { exp(it.value - max) }
        val sum = exps.values.sum()
        val probs = exps.mapValues { it.value / sum }
        val best = probs.maxBy { it.value }

        val known = feats.filter { f -> labels.any { count(f, it) > 0.0 } }
        // Không có đặc trưng nào từng gặp -> không đủ căn cứ
        if (known.isEmpty()) return Prediction(fallback, 0.0, emptyList(), probs)

        val matched = feats.filter { count(it, best.key) > 0.0 && !it.contains(' ') }.distinct().take(4)
        return Prediction(best.key, best.value, matched, probs)
    }

    private fun docs(label: String) = (base.labelDocs[label] ?: 0.0) + (user.labelDocs[label] ?: 0.0)

    private fun count(f: String, label: String) =
        (base.featureCounts[f]?.get(label) ?: 0.0) + (user.featureCounts[f]?.get(label) ?: 0.0)

    companion object {
        // Từ không mang ý nghĩa phân loại (tên ngân hàng, đơn vị tiền, từ khóa thông báo chung)
        private val STOPWORDS = setOf(
            "tk", "vnd", "nd", "noi", "dung", "sd", "gd", "luc", "vua", "duoc", "va", "cua", "la",
            "vao", "nhe", "nha", "you", "received", "message", "for", "of", "the",
            "vietcombank", "vcb", "techcombank", "tcb", "mb", "mbbank", "bidv", "agribank", "vietinbank",
            "vpbank", "acb", "sacombank", "tpbank", "msb", "hdbank", "ocb", "vib", "shb", "scb",
            "seabank", "momo", "zalopay", "cake", "timo", "tnex", "bank", "xxx"
        )

        fun normalize(text: String): String {
            val lower = text.lowercase(Locale.ROOT).replace('đ', 'd')
            val nfd = Normalizer.normalize(lower, Normalizer.Form.NFD)
            return nfd.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        }

        fun tokenize(text: String): List<String> =
            normalize(text)
                .replace(Regex("[^a-z0-9\\s]"), " ")
                .split(Regex("\\s+"))
                .filter { it.length >= 2 && it.none(Char::isDigit) && it !in STOPWORDS }

        /** Unigram + bigram ("tra luong", "lai tiet", "tien coc"...) */
        fun features(text: String): List<String> {
            val t = tokenize(text)
            if (t.isEmpty()) return emptyList()
            return t + t.zipWithNext { a, b -> "$a $b" }
        }
    }
}
