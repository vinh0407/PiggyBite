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
import com.money.app.util.AppUtils
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class AIChatActivity : AppCompatActivity() {

    private lateinit var rvChat: RecyclerView
    private lateinit var etChat: EditText
    private lateinit var btnSend: ImageButton
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private val GEMINI_API_KEY = "AQ.Ab8RN6LYB28S5p69FS6i0gyOO9ZWSmCDSSBGhKS_SWoGCpzVag"
    private val CHATGPT_API_KEY = "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"
    
    private var lastProposedFundName: String? = null
    private var lastProposedAmount: Double = 0.0
    private var lastProposedEmoji: String = "💰"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_chat)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        rvChat = findViewById(R.id.rvChat)
        etChat = findViewById(R.id.etChat)
        btnSend = findViewById(R.id.btnSend)

        adapter = ChatAdapter(chatMessages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter

        setupSuggestions()
        loadChatHistory()
        calculateStats()

        btnSend.setOnClickListener {
            sendMessage(etChat.text.toString())
        }
    }

    private fun setupSuggestions() {
        findViewById<TextView>(R.id.sug1).setOnClickListener { sendMessage((it as TextView).text.toString()) }
        findViewById<TextView>(R.id.sug2).setOnClickListener { sendMessage((it as TextView).text.toString()) }
        findViewById<TextView>(R.id.sug3).setOnClickListener { sendMessage((it as TextView).text.toString()) }
        findViewById<TextView>(R.id.sug4).setOnClickListener { sendMessage((it as TextView).text.toString()) }
    }

    private fun sendMessage(query: String) {
        if (query.isNotEmpty()) {
            val userMsg = ChatMessage(text = query, isUser = true, timestamp = System.currentTimeMillis())
            addMessageToUI(userMsg)
            saveMessageToDB(userMsg)
            etChat.text.clear()
            processAI(query)
            
            findViewById<androidx.cardview.widget.CardView>(R.id.welcomeCard)?.visibility = View.GONE
            rvChat.visibility = View.VISIBLE
        }
    }

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
            
            val pillSaving = findViewById<TextView>(R.id.pillSaving)
            val pillRating = findViewById<TextView>(R.id.pillRating)
            
            if (lastWeekExp > 0) {
                val savingPercent = ((lastWeekExp - thisWeekExp) / lastWeekExp * 100).toInt()
                pillSaving.text = "Tiết kiệm $savingPercent% so với tuần trước"
                
                when {
                    savingPercent > 50 -> {
                        pillRating.text = "🏆 Xuất sắc"
                        pillRating.setTextColor(Color.parseColor("#28C76F"))
                    }
                    savingPercent > 0 -> {
                        pillRating.text = "👍 Tốt"
                        pillRating.setTextColor(Color.parseColor("#4A5BCC"))
                    }
                    else -> {
                        pillRating.text = "⚠️ Chưa tốt"
                        pillRating.setTextColor(Color.parseColor("#EA5455"))
                    }
                }
            } else {
                pillSaving.text = "Tuần đầu tiên trải nghiệm"
                pillRating.text = "✨ Đang tích lũy"
            }
        }
    }

    private fun loadChatHistory() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AIChatActivity)
            val history = withContext(Dispatchers.IO) { db.chatMessageDao().getAllMessages() }
            if (history.isNotEmpty()) {
                chatMessages.addAll(history)
                adapter.notifyDataSetChanged()
                rvChat.scrollToPosition(chatMessages.size - 1)
                findViewById<androidx.cardview.widget.CardView>(R.id.welcomeCard)?.visibility = View.GONE
                rvChat.visibility = View.VISIBLE
            }
        }
    }

    private fun addMessageToUI(msg: ChatMessage) {
        chatMessages.add(msg)
        adapter.notifyItemInserted(chatMessages.size - 1)
        rvChat.scrollToPosition(chatMessages.size - 1)
    }

    private fun saveMessageToDB(msg: ChatMessage) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@AIChatActivity)
            db.chatMessageDao().insert(msg)
        }
    }

    private fun processAI(query: String) {
        val lower = query.lowercase().trim()
        
        // 1. Check for Predefined Quick Answers (No API call)
        val quickAnswers = mapOf(
            "ăn uống %?" to "Chi tiêu cho ăn uống thường nên chiếm từ 10% đến 20% thu nhập hàng tháng để đảm bảo tài chính lành mạnh.",
            "quỹ khẩn cấp?" to "Quỹ khẩn cấp nên đủ chi trả từ 3 đến 6 tháng chi phí sinh hoạt thiết yếu của bạn.",
            "tỷ lệ tiết kiệm?" to "Bạn nên cố gắng tiết kiệm ít nhất 20% tổng thu nhập hàng tháng theo quy tắc 50/30/20.",
            "chi tiêu cho ăn uống nên chiếm bao nhiêu phần trăm thu nhập?" to "Chi tiêu cho ăn uống thường nên chiếm từ 10% đến 20% thu nhập hàng tháng.",
            "quỹ khẩn cấp nên có bao nhiêu?" to "Quỹ khẩn cấp nên đủ chi trả từ 3 đến 6 tháng chi phí sinh hoạt.",
            "tỷ lệ tiết kiệm hợp lý là bao nhiêu?" to "Nên tiết kiệm ít nhất 20% thu nhập hàng tháng."
        )

        for ((q, a) in quickAnswers) {
            if (lower == q) {
                addMessageToUI(ChatMessage(text = a, isUser = false, timestamp = System.currentTimeMillis()))
                saveMessageToDB(ChatMessage(text = a, isUser = false, timestamp = System.currentTimeMillis()))
                return
            }
        }

        // 2. Logical flow for Agreement (Fund creation)
        if ((lower == "yes" || lower == "có" || lower == "đồng ý" || lower == "ok") && lastProposedFundName != null) {
            createProposedFund()
            return
        }

        // 3. Dynamic RAG/AI for everything else
        callGeminiAPI(query)
    }

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
            
            lastProposedFundName = null
        }
    }

    private fun callGeminiAPI(prompt: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(this@AIChatActivity)
                
                // --- RAG: Search context ---
                val terms = prompt.lowercase().split(" ").filter { it.length > 2 }
                val results = mutableListOf<Transaction>()
                terms.forEach { results.addAll(db.transactionDao().searchTransactions(it)) }
                
                val context = results.distinctBy { it.id }.take(10).joinToString("\n") { 
                    "${it.date}: ${if(it.isExpense) "-" else "+"}${it.amount} [${it.category}] ${it.description}"
                }
                
                val total = db.transactionDao().getAllTransactions().sumOf { 
                    if(it.isExpense) -it.amount else it.amount
                }

                val system = """
                    Bạn là PiggyBite AI Assistant (3.5 Flash).
                    Dữ liệu thực tế của người dùng:
                    - Số dư: ${AppUtils.formatCurrency(total, this@AIChatActivity)}
                    - Lịch sử khớp: $context
                    
                    Yêu cầu:
                    1. Trả lời dựa trên số liệu nếu có thể.
                    2. Nếu đề xuất quỹ, dùng: [FUND_ACTION: Tên|Số Tiền|Emoji]
                """.trimIndent()

                val escaped = JSONObject.quote("$system\n\nNgười dùng hỏi: $prompt")
                
                // Route to ChatGPT for rules/general, Gemini for personal/RAG
                val useChatGPT = prompt.contains("quy tắc") || prompt.contains("nên") || prompt.contains("phần trăm")
                
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
                conn.connectTimeout = 8000
                conn.readTimeout = 12000

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

                    // Handle Actions
                    val regex = Regex("\\[FUND_ACTION: (.+?)\\|(.+?)\\|(.+?)\\]")
                    regex.find(text)?.let {
                        lastProposedFundName = it.groupValues[1]
                        lastProposedAmount = it.groupValues[2].toDoubleOrNull() ?: 0.0
                        lastProposedEmoji = it.groupValues[3]
                        text = text.replace(it.value, "").trim() + "\n\n(Tôi có thể tạo quỹ này giúp bạn, đồng ý không?)"
                    }

                    withContext(Dispatchers.Main) {
                        val aiMsg = ChatMessage(text = text, isUser = false, timestamp = System.currentTimeMillis())
                        addMessageToUI(aiMsg)
                        saveMessageToDB(aiMsg)
                    }
                } else {
                    val errorLog = conn.errorStream?.bufferedReader()?.use { it.readText() }
                    Log.e("AI_API", "Code ${conn.responseCode}: $errorLog")
                    throw Exception("API Error")
                }
            } catch (e: Exception) {
                Log.e("AI_CHAT", "Error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    addMessageToUI(ChatMessage(text = "Hệ thống đang bận một chút (${e.message}), bạn nhắn lại sau nhé!", isUser = false, timestamp = System.currentTimeMillis()))
                }
            }
        }
    }

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
