package com.money.app.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.money.app.data.AppDatabase
import com.money.app.data.Investment
import com.money.app.databinding.ActivityInvestmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvestmentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvestmentBinding
    private lateinit var appDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvestmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)
        binding.btnBack.setOnClickListener { finish() }
        binding.fabAddInvestment.setOnClickListener { showAddInvestmentDialog() }
    }

    private fun showAddInvestmentDialog() {
        val inputName = EditText(this).apply { hint = "Mã/Tên tài sản (Vd: VNM, BTC, Vàng SJC)" }
        val inputQuantity = EditText(this).apply {
            hint = "Số lượng"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val inputPrice = EditText(this).apply {
            hint = "Giá mua trung bình"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputName)
            addView(inputQuantity)
            addView(inputPrice)
        }

        AlertDialog.Builder(this)
            .setTitle("Thêm tài sản đầu tư")
            .setView(container)
            .setPositiveButton("Thêm") { _, _ ->
                val name = inputName.text.toString().trim()
                val qty = inputQuantity.text.toString().toDoubleOrNull() ?: 0.0
                val price = inputPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty() && qty > 0) {
                    lifecycleScope.launch {
                        val inv = Investment(
                            assetName = name,
                            quantity = qty,
                            averageCost = price,
                            currentValue = qty * price
                        )
                        withContext(Dispatchers.IO) { appDb.investmentDao().insert(inv) }
                        Toast.makeText(this@InvestmentActivity, "Đã thêm tài sản $name", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
