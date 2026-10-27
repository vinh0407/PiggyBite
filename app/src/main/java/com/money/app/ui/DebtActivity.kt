package com.money.app.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.money.app.data.AppDatabase
import com.money.app.data.Debt
import com.money.app.databinding.ActivityDebtBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebtActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDebtBinding
    private lateinit var appDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)
        binding.btnBack.setOnClickListener { finish() }
        binding.fabAddDebt.setOnClickListener { showAddDebtDialog() }
    }

    private fun showAddDebtDialog() {
        val inputPerson = EditText(this).apply { hint = "Tên người liên quan (Vd: Nam, Anh Tuấn)" }
        val inputAmount = EditText(this).apply {
            hint = "Số tiền"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val rgType = RadioGroup(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            val rb1 = android.widget.RadioButton(context).apply { text = "Người khác nợ tôi"; id = 101; isChecked = true }
            val rb2 = android.widget.RadioButton(context).apply { text = "Tôi nợ người khác"; id = 102 }
            addView(rb1)
            addView(rb2)
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputPerson)
            addView(inputAmount)
            addView(rgType)
        }

        AlertDialog.Builder(this)
            .setTitle("Ghi nhận khoản nợ mới")
            .setView(container)
            .setPositiveButton("Lưu") { _, _ ->
                val person = inputPerson.text.toString().trim()
                val amount = inputAmount.text.toString().toDoubleOrNull() ?: 0.0
                val isOwedToMe = rgType.checkedRadioButtonId == 101

                if (person.isNotEmpty() && amount > 0) {
                    lifecycleScope.launch {
                        val debt = Debt(personName = person, amount = amount, isOwedToMe = isOwedToMe)
                        withContext(Dispatchers.IO) { appDb.debtDao().insert(debt) }
                        Toast.makeText(this@DebtActivity, "Đã lưu khoản nợ $person", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
