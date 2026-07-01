package com.money.app.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.ChatMessage
import com.money.app.data.Transaction
import com.money.app.databinding.ActivityAiChatBinding
import com.money.app.util.AppUtils
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Màn hình Chat với AI Assistant: Cung cấp trợ lý tài chính thông minh dựa trên AI.
 * Các tính năng chính:
 * - Tích hợp Gemini API và ChatGPT API để trả lời câu hỏi tài chính.
 * - Sử dụng kỹ thuật RAG (Retrieval-Augmented Generation) để đưa dữ liệu thu chi thực tế của người dùng vào ngữ cảnh chat.
 * - Tự động đề xuất tạo Quỹ tiết kiệm nếu AI thấy cần thiết.
 * - Thống kê so sánh chi tiêu tuần này với tuần trước ngay trong màn hình chat.
 */
class AIChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiChatBinding
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    
    // API Keys cho dịch vụ AI (Gemini và OpenAI)
    private val GEMINI_API_KEY = "AQ.Ab8RN6LYB28S5p69FS6i0gyOO9ZWSmCDSSBGhKS_SWoGCpzVag"
    private val CHATGPT_API_KEY = "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"
    
    // Lưu tạm thông tin quỹ được AI đề xuất để tạo nhanh khi người dùng đồng ý
    private var lastProposedFundName: String? = null
    private var lastProposedAmount: Double = 0.0
    private var lastProposedEmoji: String = "💰"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        // Cài đặt danh sách tin nhắn chat
        adapter = ChatAdapter(chatMessages)
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

        setupSuggestions() // Các câu hỏi gợi ý nhanh
        loadChatHistory() // Tải lại lịch sử chat đã lưu trong máy
        calculateStats() // Tính toán hiệu quả tiết kiệm tuần qua

        binding.btnSend.setOnClickListener {
            sendMessage(binding.etChat.text.toString())
        }
    }

    // Gán sự kiện click cho các Chip câu hỏi gợi ý
    private fun setupSuggestions() {
        binding.sug1.setOnClickListener { sendMessage(binding.sug1.text.toString()) }
        binding.sug2.setOnClickListener { sendMessage(binding.sug2.text.toString()) }
        binding.sug3.setOnClickListener { sendMessage(binding.sug3.text.toString()) }
        binding.sug4.setOnClickListener { sendMessage(binding.sug4.text.toString()) }
    }

    /**
     * Gửi tin nhắn của người dùng đi và kích hoạt AI xử lý
     */
    private fun sendMessage(query: String) {
        if (query.isNotEmpty()) {
            val userMsg = ChatMessage(text = query, isUser = true, timestamp = System.currentTimeMillis())
            addMessageToUI(userMsg)
            saveMessageToDB(userMsg)
            binding.etChat.text.clear()
            processAI(query) // Gọi AI xử lý câu hỏi
            
            binding.welcomeCard.visibility = View.GONE
            binding.rvChat.visibility = View.VISIBLE
        }
    }

    /**
     * Tính toán chỉ số tiết kiệm so với tuần trước để AI có thêm thông tin phản hồi
     */
    private fun calculateStats() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AIChatActivity)
            val all = withContext(Dispatchers.IO) { db.transactionDao().getAllTransactions() }
            
            val cal = Calendar.getInstance()
            val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
            val lastWeek = currentWeek - 1
            
            var thisWeekExp = 0.0
            var lastWeekExp = 0.0
            
            all.filter { it.isExpense }.forEach { t ->
                cal.timeInMillis = t.timestamp
                val week = cal.get(Calendar.WEEK_OF_YEAR)
                val amt = t.amount
                if (week == currentWeek) thisWeekExp += amt
                else if (week == lastWeek) lastWeekExp += amt
            }
            
            // Cập nhật giao diện đánh giá (Thanh màu xanh/đỏ/vàng phía trên)
            if (lastWeekExp > 0) {
                val savingPercent = ((lastWeekExp - thisWeekExp) / lastWeekExp * 100).toInt()
                binding.pillSaving.text = "Tiết kiệm $savingPercent% so với tuần trước"
                
                when {
                    savingPercent > 50 -> {
                        binding.pillRating.text = "🏆 Xuất sắc"
                        binding.pillRating.setTextColor(Color.parseColor("#28C76F"))
                    }
                    savingPercent > 0 -> {
                        binding.pillRating.text = "👍 Tốt"
                        binding.pillRating.setTextColor(Color.parseColor("#4A5BCC"))
                    }
                    else -> {
                        binding.pillRating.text = "⚠️ Chưa tốt"
                        binding.pillRating.setTextColor(Color.parseColor("#EA5455"))
                    }
                }
            } else {
                binding.pillSaving.text = "Tuần đầu tiên trải nghiệm"
                binding.pillRating.text = "✨ Đang tích lũy"
            }
        }
    }

    // Tải lịch sử chat từ Room Database
    private fun loadChatHistory() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AIChatActivity)
            val history = withContext(Dispatchers.IO) { db.chatMessageDao().getAllMessages() }
            if (history.isNotEmpty()) {
                chatMessages.addAll(history)
                adapter.notifyDataSetChanged()
                binding.rvChat.scrollToPosition(chatMessages.size - 1)
                binding.welcomeCard.visibility = View.GONE
                binding.rvChat.visibility = View.VISIBLE
            }
        }
    }

    private fun addMessageToUI(msg: ChatMessage) {
        chatMessages.add(msg)
        adapter.notifyItemInserted(chatMessages.size - 1)
        binding.rvChat.scrollToPosition(chatMessages.size - 1)
    }

    private fun saveMessageToDB(msg: ChatMessage) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@AIChatActivity)
            db.chatMessageDao().insert(msg)
        }
    }

    /**
     * Logic trung tâm xử lý phản hồi của AI:
     * 1. Kiểm tra các câu hỏi thường gặp (Hard-coded) để trả lời nhanh.
     * 2. Xử lý việc người dùng đồng ý tạo quỹ do AI đề xuất.
     * 3. Gọi các mô hình ngôn ngữ lớn (LLM) như Gemini/ChatGPT cho các yêu cầu phức tạp.
     */
    private fun processAI(query: String) {
        val lower = query.lowercase().trim()
        
        // 1. Kiểm tra bộ câu hỏi định nghĩa sẵn (Không cần gọi API)
        val quickAnswers = mapOf(
            "ăn uống %?" to "Chi tiêu cho ăn uống thường nên chiếm từ 10% đến 20% thu nhập hàng tháng để đảm bảo tài chính lành mạnh.",
            "quỹ khẩn cấp?" to "Quỹ khẩn cấp nên đủ chi trả từ 3 đến 6 tháng chi phí sinh hoạt thiết yếu của bạn.",
            "tỷ lệ tiết kiệm?" to "Bạn nên cố gắng tiết kiệm ít nhất 20% tổng thu nhập hàng tháng theo quy tắc 50/30/20."
        )

        for ((q, a) in quickAnswers) {
            if (lower == q) {
                val aiMsg = ChatMessage(text = a, isUser = false, timestamp = System.currentTimeMillis())
                addMessageToUI(aiMsg)
                saveMessageToDB(aiMsg)
                return
            }
        }

        // 2. Xử lý khi người dùng đồng ý ("Có", "Ok") với đề xuất tạo quỹ của AI
        if ((lower == "yes" || lower == "có" || lower == "đồng ý" || lower == "ok") && lastProposedFundName != null) {
            createProposedFund()
            return
        }

        // 3. Gọi Gemini API cho các câu hỏi còn lại
        callGeminiAPI(query)
    }

    /**
     * Tự động tạo một Quỹ tiết kiệm mới dựa trên đề xuất của AI
     */
    private fun createProposedFund() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AIChatActivity)
            val fund = com.money.app.data.Fund(
                name = lastProposedFundName!!,
                targetAmount = lastProposedAmount,
                currentAmount = 0.0,
                icon = lastProposedEmoji,
                createdDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000)
            )
            withContext(Dispatchers.IO) { 
                db.fundDao().insert(fund) 
                FirebaseSyncManager(this@AIChatActivity).createFund(fund)
            }
            
            val response = "Tuyệt vời! Tôi đã tạo quỹ '$lastProposedFundName' với mục tiêu ${AppUtils.formatCurrency(lastProposedAmount, this@AIChatActivity)} cho bạn rồi nhé. Hãy cố gắng đạt được mục tiêu này! 🚀"
            val aiMsg = ChatMessage(text = response, isUser = false, timestamp = System.currentTimeMillis())
            addMessageToUI(aiMsg)
            saveMessageToDB(aiMsg)
            
            lastProposedFundName = null // Xóa trạng thái đề xuất sau khi đã thực hiện
        }
    }

    /**
     * Gọi Gemini API (Hoặc ChatGPT tùy điều kiện) để lấy phản hồi AI.
     * Sử dụng kỹ thuật RAG: Tìm kiếm dữ liệu giao dịch thực tế để cung cấp cho AI làm ngữ cảnh.
     */
    private fun callGeminiAPI(prompt: String, retryCount: Int = 1) {
        binding.progressBar?.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(this@AIChatActivity)
                
                // --- Kỹ thuật RAG: Lấy 10 giao dịch gần nhất khớp với từ khóa tìm kiếm ---
                val terms = prompt.lowercase().split(" ").filter { it.length > 2 }
                val results = mutableListOf<Transaction>()
                if (terms.isEmpty()) {
                    results.addAll(db.transactionDao().getAllTransactions().take(10))
                } else {
                    terms.forEach { results.addAll(db.transactionDao().searchTransactions(it)) }
                }
                
                // Xây dựng ngữ cảnh dữ liệu cho AI
                val context = results.distinctBy { it.id }.take(10).joinToString("\n") { 
                    "${it.date}: ${if(it.isExpense) "-" else "+"}${it.amount} [${it.category}] ${it.description}"
                }
                
                val total = db.transactionDao().getAllTransactions().sumOf { 
                    if(it.isExpense) -it.amount else it.amount
                }

                // Thiết lập prompt hệ thống (System Instructions)
                val system = """
                    Bạn là PiggyBite AI Assistant (3.5 Flash).
                    Dữ liệu thực tế của người dùng:
                    - Số dư: ${AppUtils.formatCurrency(total, this@AIChatActivity)}
                    - Lịch sử khớp: $context
                    
                    Yêu cầu:
                    1. Trả lời ngắn gọn, thân thiện bằng tiếng Việt.
                    2. Nếu đề xuất quỹ, dùng cú pháp: [FUND_ACTION: Tên|Số Tiền|Emoji]
                """.trimIndent()

                val escaped = JSONObject.quote("$system\n\nNgười dùng hỏi: $prompt")
                
                // Chọn API để gọi (Sử dụng Gemini cho các yêu cầu thông thường vì tốc độ nhanh)
                val useChatGPT = prompt.contains("quy tắc") || prompt.contains("phần trăm")
                
                val url = if (useChatGPT) URL("https://api.openai.com/v1/chat/completions") 
                          else URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent")

                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                if (useChatGPT) {
                    conn.setRequestProperty("Authorization", "Bearer $CHATGPT_API_KEY")
                } else {
                    conn.setRequestProperty("X-goog-api-key", GEMINI_API_KEY)
                }
                conn.doOutput = true
                conn.connectTimeout = 20000

                val payload = if (useChatGPT) {
                    "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": $escaped}]}"
                } else {
                    "{\"contents\": [{\"parts\":[{\"text\": $escaped}]}]}"
                }

                conn.outputStream.use { it.write(payload.toByteArray()) }

                if (conn.responseCode == 200) {
                    val raw = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(raw)
                    var text = if (useChatGPT) {
                        json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                    } else {
                        json.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                    }

                    // Xử lý nếu AI đề xuất tạo quỹ (Tách phần lệnh tạo quỹ ra khỏi văn bản hiển thị)
                    val regex = Regex("\\[FUND_ACTION: (.+?)\\|(.+?)\\|(.+?)\\]")
                    regex.find(text)?.let {
                        lastProposedFundName = it.groupValues[1]
                        lastProposedAmount = it.groupValues[2].toDoubleOrNull() ?: 0.0
                        lastProposedEmoji = it.groupValues[3]
                        text = text.replace(it.value, "").trim() + "\n\n(Tôi có thể tạo quỹ này giúp bạn, đồng ý không?)"
                    }

                    withContext(Dispatchers.Main) {
                        binding.progressBar?.visibility = View.GONE
                        val aiMsg = ChatMessage(text = text, isUser = false, timestamp = System.currentTimeMillis())
                        addMessageToUI(aiMsg)
                        saveMessageToDB(aiMsg)
                    }
                } else {
                    throw Exception("API Error")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar?.visibility = View.GONE
                    addMessageToUI(ChatMessage(text = "Hệ thống đang bận một chút, bạn hãy thử lại sau nhé!", isUser = false, timestamp = System.currentTimeMillis()))
                }
            }
        }
    }

    /**
     * Adapter cho danh sách tin nhắn chat, phân biệt giao diện người dùng và AI
     */
    inner class ChatAdapter(private val list: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val resId = if (viewType == 1) R.layout.item_chat_user else R.layout.item_chat_ai
            val v = LayoutInflater.from(parent.context).inflate(resId, parent, false)
            return ViewHolder(v)
        }
        override fun getItemViewType(position: Int) = if (list[position].isUser) 1 else 0
        override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.tv.text = list[position].text }
        override fun getItemCount() = list.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) { val tv = v.findViewById<TextView>(R.id.tvMessage) }
    }
}
