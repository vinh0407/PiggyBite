package com.money.app.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.money.app.data.AppDatabase
import com.money.app.data.Budget
import com.money.app.databinding.ActivityBudgetsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBudgetsBinding
    private lateinit var appDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)
        binding.btnBack.setOnClickListener { finish() }
        binding.fabAddBudget.setOnClickListener { showAddBudgetDialog() }
    }

    private fun showAddBudgetDialog() {
        val inputCategory = EditText(this).apply { hint = "Hạng mục (Vd: Ăn uống, Mua sắm)" }
        val inputLimit = EditText(this).apply {
            hint = "Hạn mức ngân sách (Vd: 3.000.000)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputCategory)
            addView(inputLimit)
        }

        AlertDialog.Builder(this)
            .setTitle("Đặt ngân sách chi tiêu")
            .setView(container)
            .setPositiveButton("Lưu") { _, _ ->
                val category = inputCategory.text.toString().trim()
                val limit = inputLimit.text.toString().toDoubleOrNull() ?: 0.0

                if (category.isNotEmpty() && limit > 0) {
                    lifecycleScope.launch {
                        val budget = Budget(category = category, amountLimit = limit)
                        withContext(Dispatchers.IO) { appDb.budgetDao().insert(budget) }
                        Toast.makeText(this@BudgetsActivity, "Đã thiết lập ngân sách $category", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
