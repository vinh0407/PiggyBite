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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.ChatMessage
import com.money.app.util.AppUtils
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
    private val GEMINI_API_KEY = "AQ.Ab8RN6JzQXDQWxS-VBYby4cQjwq6gbax7lrGZPW0EXCiGSxgKA"

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
        findViewById<TextView>(R.id.sug1).setOnClickListener {
            sendMessage((it as TextView).text.toString())
        }
        findViewById<TextView>(R.id.sug2).setOnClickListener {
            sendMessage((it as TextView).text.toString())
        }
    }

    private fun sendMessage(query: String) {
        if (query.isNotEmpty()) {
            val userMsg = ChatMessage(text = query, isUser = true, timestamp = System.currentTimeMillis())
            addMessageToUI(userMsg)
            saveMessageToDB(userMsg)
            etChat.text.clear()
            processAI(query)
            
            // Hide welcome card and show chat on first message
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
                val amt = AppUtils.parseAmount(t.amount)
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
        
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AIChatActivity)
            val all = withContext(Dispatchers.IO) { db.transactionDao().getAllTransactions() }
            
            // Logic check for Fund Creation
            if (lower.contains("tiết kiệm")) {
                val expenseMap = all.filter { it.isExpense }.groupBy { it.category }
                val totalExp = all.filter { it.isExpense }.sumOf { AppUtils.parseAmount(it.amount) }
                
                val suggestion = if (totalExp > 0) {
                    val topCat = expenseMap.maxByOrNull { it.value.sumOf { t -> AppUtils.parseAmount(t.amount) } }?.key ?: ""
                    "Dựa trên phân tích AI, bạn đang chi nhiều nhất cho '$topCat'. Nếu giảm 10% chi tiêu mục này, bạn có thể tiết kiệm thêm khoảng ${AppUtils.formatCurrency(totalExp * 0.1)} mỗi tháng đấy!"
                } else {
                    "Bạn hiện chưa có khoản chi nào, hãy tiếp tục duy trì thói quen tiết kiệm tuyệt vời này nhé!"
                }
                addMessageToUI(ChatMessage(text = suggestion, isUser = false, timestamp = System.currentTimeMillis()))
                return@launch
            }

            if (lower.contains("vũng tàu") || lower.contains("kế hoạch")) {
                val response = "Tôi thấy bạn muốn đi Vũng Tàu. Dự kiến chi phí khoảng 2.000.000đ cho 2 ngày. Bạn có muốn tôi tạo quỹ 'Du lịch Vũng Tàu' với mục tiêu này không? (Nhập 'yes' để tạo)"
                addMessageToUI(ChatMessage(text = response, isUser = false, timestamp = System.currentTimeMillis()))
                return@launch
            }

            if (lower == "yes" && chatMessages.lastOrNull()?.text?.contains("Vũng Tàu") == true) {
                val fund = com.money.app.data.Fund(
                    name = "Du lịch Vũng Tàu",
                    targetAmount = 2000000.0,
                    currentAmount = 0.0,
                    icon = "🌊",
                    createdDate = System.currentTimeMillis(),
                    endDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
                )
                withContext(Dispatchers.IO) { db.fundDao().insert(fund) }
                addMessageToUI(ChatMessage(text = "Tuyệt vời! Tôi đã tạo quỹ 'Du lịch Vũng Tàu' cho bạn rồi nhé. Chúc bạn có chuyến đi vui vẻ!", isUser = false, timestamp = System.currentTimeMillis()))
                return@launch
            }

            val localResponse = when {
                lower.contains("chi tiêu") -> {
                    val total = all.filter { it.isExpense }.sumOf { AppUtils.parseAmount(it.amount) }
                    "Tổng chi tiêu của bạn đến nay là ${AppUtils.formatCurrency(total)}. Hãy chi tiêu thông minh nhé!"
                }
                lower.contains("tiết kiệm") -> {
                    "Bạn nên dành ít nhất 20% thu nhập để tiết kiệm. Hiện tại bạn đang có những quỹ mục tiêu rất tốt!"
                }
                lower.contains("cà phê") || lower.contains("cafe") -> {
                    val cafeTotal = all.filter { it.category.contains("Cafe") }.sumOf { AppUtils.parseAmount(it.amount) }
                    "Bạn đã tiêu ${AppUtils.formatCurrency(cafeTotal)} cho Cafe. Bớt uống lại để tiết kiệm nào!"
                }
                else -> null
            }

            if (localResponse != null) {
                val aiMsg = ChatMessage(text = localResponse, isUser = false, timestamp = System.currentTimeMillis())
                addMessageToUI(aiMsg)
                saveMessageToDB(aiMsg)
            } else {
                callGeminiAPI(query)
            }
        }
    }

    private fun callGeminiAPI(prompt: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(this@AIChatActivity)
                val all = db.transactionDao().getAllTransactions()
                val totalExp = all.filter { it.isExpense }.sumOf { AppUtils.parseAmount(it.amount) }
                val totalInc = all.filter { !it.isExpense }.sumOf { AppUtils.parseAmount(it.amount) }
                
                // Context for Gemini
                val context = "Dữ liệu người dùng: Tổng chi tiêu ${AppUtils.formatCurrency(totalExp)}, Tổng thu nhập ${AppUtils.formatCurrency(totalInc)}."
                
                // Updated to gemini-3.5-flash as per provided snippet
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$GEMINI_API_KEY")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val body = """
                    {
                      "contents": [{
                        "parts":[{"text": "$context Bạn là PiggyBite AI phiên bản Flash 1.5. Hãy tư vấn tài chính ngắn gọn, thông minh, đôi khi hài hước và thân thiện cho người dùng. Nếu họ hỏi ngoài lề, hãy trả lời tự nhiên. Câu hỏi: $prompt"}]
                      }]
                    }
                """.trimIndent()

                conn.outputStream.use { it.write(body.toByteArray()) }

                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseStr)
                    val aiText = jsonResponse.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    withContext(Dispatchers.Main) {
                        val aiMsg = ChatMessage(text = aiText, isUser = false, timestamp = System.currentTimeMillis())
                        addMessageToUI(aiMsg)
                        saveMessageToDB(aiMsg)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMsg = ChatMessage(text = "Kết nối bị gián đoạn. Hãy thử lại sau nhé!", isUser = false, timestamp = System.currentTimeMillis())
                    addMessageToUI(errorMsg)
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
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tv.text = list[position].text
        }
        override fun getItemCount() = list.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tv = v.findViewById<TextView>(R.id.tvMessage)
        }
    }
}