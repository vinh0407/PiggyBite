package com.piggybite.app

import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.Transaction
import com.piggybite.app.service.ExpenseClassifierAI
import com.piggybite.app.service.IncomeNaiveBayes
import com.piggybite.app.service.SmartFinanceAdvisor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class IncomeAITest {

    private val incomeCategories = listOf(
        "Lương & Thu nhập chính",
        "Kinh doanh & Bán hàng",
        "Thưởng & Hoa hồng",
        "Đầu tư & Tiền lãi",
        "Được biếu tặng & Trả nợ",
        "Thu nhập khác"
    )

    private val incomeCorpusLabels = mapOf(
        "LUONG" to "Lương & Thu nhập chính",
        "KINHDOANH" to "Kinh doanh & Bán hàng",
        "THUONG" to "Thưởng & Hoa hồng",
        "DAUTU" to "Đầu tư & Tiền lãi",
        "TANG" to "Được biếu tặng & Trả nợ",
        "KHAC" to "Thu nhập khác"
    )

    @Test
    fun testIncomeCorpusAccuracy() {
        val model = IncomeNaiveBayes()
        val corpusFile = File("src/main/assets/ai/income_corpus.txt")
        assertTrue("Corpus file must exist", corpusFile.exists())

        corpusFile.forEachLine { raw ->
            val line = raw.trim()
            if (line.isNotEmpty() && !line.startsWith("#")) {
                val sep = line.indexOf('|')
                if (sep > 0) {
                    val label = incomeCorpusLabels[line.substring(0, sep).trim()]
                    if (label != null) {
                        model.trainBase(line.substring(sep + 1), label)
                    }
                }
            }
        }

        val testCases = listOf(
            "TK 1903xxx tại Techcombank +18,000,000 VND. ND: Cty ABC chuyen khoan tien luong thang 10" to "Lương & Thu nhập chính",
            "Vietcombank: SD TK 0071xxx +8,500,000VND. ND: tra luong dot 2 thang 10" to "Lương & Thu nhập chính",
            "MB: +15,000,000VND ND: PAYROLL MONTHLY NOV" to "Lương & Thu nhập chính",
            "Ban vua nhan 350.000d tu Nguyen Van A. ND: thanh toan tien hang quan ao" to "Kinh doanh & Bán hàng",
            "MoMo: Nhan 150.000d tu khach - thanh toan ship cod don hang 556" to "Kinh doanh & Bán hàng",
            "Shopee thanh toan tien doanh thu don hang thang 10: +4,500,000d" to "Kinh doanh & Bán hàng",
            "Vietcombank: +5,000,000 VND ND: THUONG DU AN XUAT SAC QUY 3" to "Thưởng & Hoa hồng",
            "Techcombank: +2,000,000 VND ND: tien thuong kpi thang 10" to "Thưởng & Hoa hồng",
            "MB: +500,000 VND ND: hoa hong affiliate thang 9" to "Thưởng & Hoa hồng",
            "Vietcombank: +450,000 VND ND: TRA LAI TIEN GUI TIET KIEM ONLINE" to "Đầu tư & Tiền lãi",
            "TCBS chuyen tien ban co phieu: +12,000,000 VND" to "Đầu tư & Tiền lãi",
            "Nhan 2,500,000 VND ND: Chi tra co tuc dot 1 bang tien mat" to "Đầu tư & Tiền lãi",
            "MB: +500,000 VND ND: Me gui tien tieu vat nhe con" to "Được biếu tặng & Trả nợ",
            "Techcombank: +1,000,000 VND ND: Mung cuoi hai ban tram nam hanh phuc" to "Được biếu tặng & Trả nợ",
            "MoMo: Nhan 200.000d tu Tran Thi B. ND: tra tien an trua hom qua" to "Được biếu tặng & Trả nợ"
        )

        var correct = 0
        for ((text, expected) in testCases) {
            val pred = model.predict(text, incomeCategories, "Thu nhập khác")
            if (pred.label == expected) correct++
        }

        val accuracy = correct.toDouble() / testCases.size
        println("INCOME ACCURACY: ${(accuracy * 100)}% ($correct/${testCases.size})")
        assertTrue("Accuracy must be at least 90%", accuracy >= 0.90)
    }

    @Test
    fun testExpenseCorpusAccuracy() {
        val model = IncomeNaiveBayes()
        val corpusFile = File("src/main/assets/ai/expense_corpus.txt")
        assertTrue("Expense corpus file must exist", corpusFile.exists())

        val expenseLabels = mapOf(
            "ANUONG" to ExpenseClassifierAI.CAT_FOOD,
            "MUASAM" to ExpenseClassifierAI.CAT_SHOPPING,
            "DICHUYEN" to ExpenseClassifierAI.CAT_TRANSPORT,
            "HOADON" to ExpenseClassifierAI.CAT_BILLS,
            "GIAITRI" to ExpenseClassifierAI.CAT_ENTERTAINMENT,
            "SUCKHOE" to ExpenseClassifierAI.CAT_HEALTH,
            "KHAC" to ExpenseClassifierAI.CAT_OTHER
        )

        val categories = listOf(
            ExpenseClassifierAI.CAT_FOOD,
            ExpenseClassifierAI.CAT_SHOPPING,
            ExpenseClassifierAI.CAT_TRANSPORT,
            ExpenseClassifierAI.CAT_BILLS,
            ExpenseClassifierAI.CAT_ENTERTAINMENT,
            ExpenseClassifierAI.CAT_HEALTH,
            ExpenseClassifierAI.CAT_OTHER
        )

        corpusFile.forEachLine { raw ->
            val line = raw.trim()
            if (line.isNotEmpty() && !line.startsWith("#")) {
                val sep = line.indexOf('|')
                if (sep > 0) {
                    val label = expenseLabels[line.substring(0, sep).trim()]
                    if (label != null) {
                        model.trainBase(line.substring(sep + 1), label)
                    }
                }
            }
        }

        val testCases = listOf(
            "Thanh toan Highlands Coffee Dong Khoi -55,000 VND" to ExpenseClassifierAI.CAT_FOOD,
            "ShopeeFood: Com tam suon bi cha -45,000 VND" to ExpenseClassifierAI.CAT_FOOD,
            "Shopee: Thanh toan don hang quan ao thoi trang -220,000 VND" to ExpenseClassifierAI.CAT_SHOPPING,
            "Uniqlo: Thanh toan ao khoac -799,000 VND" to ExpenseClassifierAI.CAT_SHOPPING,
            "Grab: Chuyen xe GrabCar di san bay -120,000 VND" to ExpenseClassifierAI.CAT_TRANSPORT,
            "Petrolimex: Do xang A95 -90,000 VND" to ExpenseClassifierAI.CAT_TRANSPORT,
            "EVN: Hoa don tien dien sinh hoat thang 10 -650,000 VND" to ExpenseClassifierAI.CAT_BILLS,
            "Viettel: Thanh toan cuoc internet cap quang -220,000 VND" to ExpenseClassifierAI.CAT_BILLS,
            "CGV: Mua 2 ve xem phim -210,000 VND" to ExpenseClassifierAI.CAT_ENTERTAINMENT,
            "Netflix: Phi gia han hang thang -260,000 VND" to ExpenseClassifierAI.CAT_ENTERTAINMENT,
            "Pharmacity: Mua thuoc ho va vitamin C -120,000 VND" to ExpenseClassifierAI.CAT_HEALTH,
            "Long Chau: Don thuoc cam sot -150,000 VND" to ExpenseClassifierAI.CAT_HEALTH
        )

        var correct = 0
        for ((text, expected) in testCases) {
            val pred = model.predict(text, categories, ExpenseClassifierAI.CAT_OTHER)
            if (pred.label == expected) correct++
        }

        val accuracy = correct.toDouble() / testCases.size
        println("EXPENSE ACCURACY: ${(accuracy * 100)}% ($correct/${testCases.size})")
        assertTrue("Expense accuracy must be at least 90%", accuracy >= 0.90)
    }

    @Test
    fun testSmartAdvisor() {
        val accounts = listOf(
            BankAccount("1", "vcb", "Vietcombank", "123", 2_000_000L),
            BankAccount("2", "mbbank", "MB Bank", "456", 15_000_000L),
            BankAccount("3", "momo", "MoMo", "789", 150_000L)
        )

        val txs = listOf(
            Transaction("t1", 50_000L, false, "1", "Vietcombank", "Highlands Coffee", System.currentTimeMillis(), "Ăn uống"),
            Transaction("t2", 3_500_000L, false, "2", "MB Bank", "Tien nha thang 10", System.currentTimeMillis(), "Hóa đơn"),
            Transaction("t3", 260_000L, false, "2", "MB Bank", "Netflix Premium", System.currentTimeMillis(), "Giải trí")
        )

        // 1. Bank recommendation
        val rec = SmartFinanceAdvisor.recommendBankForPayment(500_000L, accounts)
        assertEquals("MB Bank", rec.recommendedAccount?.bankName)
        assertTrue(rec.isSufficient)

        // 2. Recurring items detection
        val recurring = SmartFinanceAdvisor.detectRecurringExpenses(txs)
        assertTrue("Must detect rent and Netflix", recurring.size >= 2)

        // 3. Question Answering
        val answer = SmartFinanceAdvisor.answerQuestion("Hôm nay tiêu bao nhiêu?", accounts, txs)
        assertTrue(answer.mainResponse.isNotEmpty())
    }
}
