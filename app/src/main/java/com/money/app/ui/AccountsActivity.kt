package com.money.app.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.money.app.data.Account
import com.money.app.data.AppDatabase
import com.money.app.databinding.ActivityAccountsBinding
import com.money.app.util.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountsBinding
    private lateinit var appDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)
        binding.btnBack.setOnClickListener { finish() }
        binding.fabAddAccount.setOnClickListener { showAddAccountDialog() }

        loadAccountsData()
    }

    private fun loadAccountsData() {
        lifecycleScope.launch {
            val accounts = withContext(Dispatchers.IO) { appDb.accountDao().getAllAccounts() }

            var assets = 0.0
            var liabilities = 0.0

            accounts.forEach { acc ->
                if (acc.type == "CREDIT_CARD" || acc.type == "LOAN") {
                    liabilities += acc.balance
                } else {
                    assets += acc.balance
                }
            }

            val netWorth = assets - liabilities

            binding.tvTotalAssets.text = "+${AppUtils.formatCurrency(assets, this@AccountsActivity)}"
            binding.tvTotalLiabilities.text = "-${AppUtils.formatCurrency(liabilities, this@AccountsActivity)}"
            binding.tvNetWorthValue.text = AppUtils.formatCurrency(netWorth, this@AccountsActivity)
        }
    }

    private fun showAddAccountDialog() {
        val inputName = EditText(this).apply { hint = "Tên tài khoản (Vd: Vietcombank, Ví MoMo)" }
        val inputBalance = EditText(this).apply {
            hint = "Số dư hiện tại"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
            addView(inputName)
            addView(inputBalance)
        }

        AlertDialog.Builder(this)
            .setTitle("Tạo tài khoản mới")
            .setView(container)
            .setPositiveButton("Tạo") { _, _ ->
                val name = inputName.text.toString().trim()
                val balance = inputBalance.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        val newAccount = Account(
                            name = name,
                            balance = balance,
                            type = "BANK"
                        )
                        withContext(Dispatchers.IO) { appDb.accountDao().insert(newAccount) }
                        Toast.makeText(this@AccountsActivity, "Đã tạo tài khoản $name", Toast.LENGTH_SHORT).show()
                        loadAccountsData()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
