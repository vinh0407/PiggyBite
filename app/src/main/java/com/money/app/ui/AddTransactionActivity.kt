package com.money.app.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Transaction
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Màn hình Thêm Giao dịch: Cho phép người dùng nhập các khoản thu/chi mới.
 * Hỗ trợ các tính năng thông minh:
 * - Nhập liệu bằng giọng nói (Voice input)
 * - Quét hóa đơn (OCR)
 * - Tự động phân loại hạng mục dựa trên nội dung
 * - Bàn phím số tùy chỉnh
 */
class AddTransactionActivity : AppCompatActivity() {

    private lateinit var tvAmount: TextView
    private lateinit var etDescription: EditText
    private lateinit var cgCategories: ChipGroup
    private lateinit var btnExpense: TextView
    private lateinit var btnIncome: TextView
    private lateinit var btnSave: Button
    
    private var currentRawAmount: String = "0" // Lưu trữ số tiền dưới dạng chuỗi thô để xử lý bàn phím
    private var isExpenseMode: Boolean = true // Chế độ Chi tiêu (mặc định) hoặc Thu nhập
    private var isFromOCR: Boolean = false // Đánh dấu nếu dữ liệu đến từ tính năng quét hóa đơn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Ánh xạ các view từ layout
        tvAmount = findViewById(R.id.tvAmount)
        etDescription = findViewById(R.id.etDescription)
        cgCategories = findViewById(R.id.cgCategories)
        btnExpense = findViewById(R.id.btnExpense)
        btnIncome = findViewById(R.id.btnIncome)
        btnSave = findViewById(R.id.btnSave)

        // Nút đóng màn hình
        findViewById<View>(R.id.btnClose).setOnClickListener { finish() }

        // Chuyển đổi giữa Chi tiêu và Thu nhập
        btnExpense.setOnClickListener { switchMode(true) }
        btnIncome.setOnClickListener { switchMode(false) }

        setupKeypad() // Cài đặt bàn phím số
        updateCategories() // Hiển thị danh sách các Chip hạng mục tương ứng
        
        // Nhận dữ liệu từ Intent nếu được gọi từ tính năng Voice hoặc OCR
        val voiceText = intent.getStringExtra("EXTRA_VOICE_TEXT")
        val ocrText = intent.getStringExtra("EXTRA_OCR_TEXT")
        
        if (!voiceText.isNullOrEmpty()) {
            etDescription.setText(voiceText)
            parseVoiceText(voiceText) // Tự động trích xuất số tiền từ giọng nói
        } else if (!ocrText.isNullOrEmpty()) {
            isFromOCR = true
            etDescription.setText("Quét từ hóa đơn")
            parseOCRText(ocrText) // Tự động trích xuất số tiền từ văn bản OCR
        }
        
        // Lắng nghe thay đổi văn bản để tự động gợi ý hạng mục
        etDescription.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                autoCategorize(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        
        // Sự kiện lưu giao dịch
        btnSave.setOnClickListener { validateAndSave() }
    }

    /**
     * Logic xử lý chuỗi: Phân tích giọng nói tiếng Việt thành số tiền và mô tả.
     * Ví dụ: "Ăn phở hai mươi lăm ngàn" -> Mô tả: "Ăn phở", Số tiền: 25000
     */
    private fun parseVoiceText(text: String) {
        val lower = text.lowercase().replace("-", " ").trim()
        val words = lower.split(" ").filter { it.isNotBlank() }
        
        if (words.isEmpty()) return

        var tempAmount = 0L
        var description = text
        
        // Quy đổi từ tiếng Việt sang số
        fun vnToNum(word: String): Long {
            return when (word) {
                "một" -> 1; "hai" -> 2; "ba" -> 3; "bốn" -> 4; "năm" -> 5
                "sáu" -> 6; "bảy" -> 7; "tám" -> 8; "chín" -> 9; "mười" -> 10
                "chục" -> 10; "trăm" -> 100; "ngàn", "nghìn" -> 1000
                "triệu" -> 1000000; "k" -> 1000; "lít" -> 100000; "củ" -> 1000000
                else -> 0L
            }
        }

        var amountFound = false
        var currentMultiplier = 1000L // Mặc định đơn vị là ngàn đồng
        
        // Duyệt ngược từ cuối chuỗi để tìm các từ chỉ số lượng
        for (i in words.indices.reversed()) {
            val word = words[i]
            val num = word.toLongOrNull()
            
            if (num != null) {
                tempAmount += num * currentMultiplier
                amountFound = true
                description = words.subList(0, i).joinToString(" ")
            } else {
                val vnVal = vnToNum(word)
                if (vnVal >= 10) {
                    currentMultiplier = if (word == "chục" || word == "mươi") 10000L else vnVal
                } else if (vnVal > 0) {
                    tempAmount += vnVal * currentMultiplier
                    amountFound = true
                    description = words.subList(0, i).joinToString(" ")
                } else if (amountFound) break
            }
        }

        // Xử lý trường hợp đặc biệt "năm chục" hoặc "năm mươi"
        if (lower.endsWith("năm chục") || lower.endsWith("năm mươi")) {
            tempAmount = 50000L
            description = lower.replace("năm chục", "").replace("năm mươi", "").trim()
        }

        if (tempAmount > 0) {
            currentRawAmount = tempAmount.toString()
            val amountDouble = currentRawAmount.toDoubleOrNull() ?: 0.0
            tvAmount.text = java.text.DecimalFormat("#,###").format(amountDouble)
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_blue))
            btnSave.setTextColor(android.graphics.Color.WHITE)
        }
        
        etDescription.setText(description.replaceFirstChar { it.uppercase() })
        autoCategorize(description)
    }

    /**
     * Logic trích xuất số tiền lớn nhất từ văn bản OCR (thường là tổng tiền hóa đơn)
     */
    private fun parseOCRText(text: String) {
        val lines = text.split("\n")
        var foundAmount = 0L
        lines.forEach { line ->
            // Làm sạch chuỗi: xóa dấu chấm, phẩy và ký hiệu tiền tệ
            val clean = line.replace(".", "").replace(",", "").replace("đ", "").trim()
            val num = clean.toLongOrNull()
            if (num != null && num > foundAmount) {
                foundAmount = num
            }
        }
        if (foundAmount > 0) {
            currentRawAmount = foundAmount.toString()
            val amountDouble = currentRawAmount.toDoubleOrNull() ?: 0.0
            tvAmount.text = java.text.DecimalFormat("#,###").format(amountDouble)
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_blue))
            btnSave.setTextColor(android.graphics.Color.WHITE)
        }
        autoCategorize(text)
    }

    /**
     * Tự động chọn Chip hạng mục dựa trên các từ khóa có trong mô tả
     */
    private fun autoCategorize(desc: String) {
        val lowerDesc = desc.lowercase().trim()
        if (lowerDesc.isEmpty()) return

        // Bản đồ từ khóa cho từng hạng mục
        val categoryMap = if (isExpenseMode) {
            mapOf(
                "🏠 Thuê nhà" to listOf("trọ", "nhà", "phòng", "cọc", "điện", "nước", "khách sạn", "homestay"),
                "🍱 Ăn uống" to listOf("cơm", "cháo", "bún", "phở", "gà", "vịt", "heo", "bò", "mắm", "chả", "bánh mì", "xôi", "bánh", "kfc", "lotte", "mcdonald", "jollibee", "ăn", "uống", "hủ tiếu", "mì"),
                "🚗 Di chuyển" to listOf("xe", "grab", "máy bay", "về quê", "xăng", "dầu", "lốp", "be", "xanh sm", "bus", "taxi", "đò", "phà"),
                "☕ Cafe" to listOf("cafe", "cà phê", "highlands", "starbucks", "phúc long", "trà sữa", "chill", "the coffee house", "bar", "pub"),
                "💊 Sức khỏe" to listOf("thuốc", "bệnh", "khám", "vitamin", "gym", "spa", "bệnh viện", "nha khoa", "thuốc men", "đau ốm", "bệnh tật"),
                "🎮 Giải trí" to listOf("phim", "game", "karaoke", "du lịch")
            )
        } else {
            mapOf(
                "💰 Lương" to listOf("lương", "salary", "sếp thưởng", "thu nhập", "hoa hồng", "khen thưởng"),
                "💼 Freelance" to listOf("freelance", "dự án", "bán đồ", "shopee", "tiktok", "bo", "tip"),
                "🎁 Quà tặng" to listOf("mẹ cho", "ba cho", "biếu", "tặng", "lì xì", "biếu tặng"),
                "📈 Đầu tư" to listOf("bitcoin", "crypto", "vàng", "chứng khoán", "lãi", "lợi nhuận", "trúng số")
            )
        }

        var foundCat: String? = null
        for ((cat, keywords) in categoryMap) {
            if (keywords.any { lowerDesc.contains(it) }) {
                foundCat = cat
                break
            }
        }
        
        val targetCat = foundCat ?: (if (isFromOCR) "🛍️ Mua sắm" else "❓ Khác")
        
        // Duyệt qua các Chip để tìm và đánh dấu hạng mục phù hợp
        for (i in 0 until cgCategories.childCount) {
            val chip = cgCategories.getChildAt(i) as Chip
            if (chip.text.toString().trim() == targetCat.trim()) {
                chip.isChecked = true
                break
            }
        }
    }

    /**
     * Chuyển đổi giao diện giữa chế độ Chi tiêu và Thu nhập
     */
    private fun switchMode(isExpense: Boolean) {
        isExpenseMode = isExpense
        if (isExpense) {
            btnExpense.setBackgroundResource(R.drawable.bg_pill_white)
            btnExpense.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            btnIncome.background = null
            btnIncome.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnSave.text = "Lưu Chi tiêu"
        } else {
            btnIncome.setBackgroundResource(R.drawable.bg_pill_white)
            btnIncome.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            btnExpense.background = null
            btnExpense.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnSave.text = "Lưu Thu nhập"
        }
        updateCategories()
        autoCategorize(etDescription.text.toString())
    }

    /**
     * Tạo bàn phím số 0-9 và nút Xóa thủ công bằng GridLayout
     */
    private fun setupKeypad() {
        val grid = findViewById<GridLayout>(R.id.keypad)
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "000", "0", "DEL")
        
        keys.forEach { key ->
            val button = Button(this, null, 0, com.google.android.material.R.style.Widget_MaterialComponents_Button_TextButton)
            button.text = key
            button.textSize = 20f
            button.setTextColor(ContextCompat.getColor(this, R.color.text_main))
            button.setOnClickListener { handleKey(key) }
            
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            button.layoutParams = params
            
            grid.addView(button)
        }
    }

    /**
     * Xử lý sự kiện khi nhấn các phím trên bàn phím số
     */
    private fun handleKey(key: String) {
        if (key == "DEL") {
            if (currentRawAmount.length > 1) {
                currentRawAmount = currentRawAmount.dropLast(1)
            } else {
                currentRawAmount = "0"
            }
        } else {
            if (currentRawAmount == "0") {
                currentRawAmount = key
            } else {
                currentRawAmount += key
            }
        }
        
        val amountDouble = currentRawAmount.toDoubleOrNull() ?: 0.0
        tvAmount.text = java.text.DecimalFormat("#,###").format(amountDouble)
        
        // Cập nhật trạng thái nút Lưu (chỉ cho phép lưu khi số tiền > 0)
        if (currentRawAmount != "0") {
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_blue))
            btnSave.setTextColor(android.graphics.Color.WHITE)
        } else {
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.grey_light))
            btnSave.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
        }
    }

    /**
     * Làm mới danh sách các hạng mục (Chip) khi thay đổi chế độ Thu/Chi
     */
    private fun updateCategories() {
        cgCategories.removeAllViews()
        val cats = if (isExpenseMode) {
            listOf("🏠 Thuê nhà", "🍱 Ăn uống", "🚗 Di chuyển", "☕ Cafe", "💊 Sức khỏe", "🛍️ Mua sắm", "📚 Học tập", "🎮 Giải trí", "❓ Khác")
        } else {
            listOf("💰 Lương", "💼 Freelance", "🎁 Quà tặng", "📈 Đầu tư", "🏦 Tiết kiệm", "❓ Khác")
        }
        
        cats.forEach { cat ->
            val chip = Chip(this)
            chip.text = cat
            chip.isCheckable = true
            chip.chipBackgroundColor = ContextCompat.getColorStateList(this, R.color.white)
            cgCategories.addView(chip)
        }
    }

    /**
     * Kiểm tra dữ liệu và lưu giao dịch vào cơ sở dữ liệu Room và đồng bộ lên Firebase
     */
    private fun validateAndSave() {
        if (currentRawAmount == "0") return
        
        val selectedChipId = cgCategories.checkedChipId
        val category = if (selectedChipId != -1) {
            cgCategories.findViewById<Chip>(selectedChipId).text.toString()
        } else "❓ Khác"

        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(this@AddTransactionActivity)
                val trans = Transaction(
                    amount = currentRawAmount.toDoubleOrNull() ?: 0.0,
                    category = category,
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    description = etDescription.text.toString(),
                    isExpense = isExpenseMode,
                    timestamp = System.currentTimeMillis()
                )
                
                // Lưu vào Room DB cục bộ
                db.transactionDao().insert(trans)
                
                // Đồng bộ lên Firebase Realtime Database
                val syncManager = FirebaseSyncManager(this@AddTransactionActivity)
                syncManager.saveTransaction(trans)

                finish() // Đóng màn hình sau khi lưu thành công
            } catch (e: Exception) {
                Toast.makeText(this@AddTransactionActivity, "Lỗi khi lưu giao dịch: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
