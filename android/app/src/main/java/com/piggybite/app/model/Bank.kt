package com.piggybite.app.model

data class Bank(
    val id: String,
    val name: String,
    val shortName: String,
    val bin: String,
    val colorHex: String,
    val scheme: String,
    val packageName: String,
    val keywords: List<String>,
    val logoUrl: String = "file:///android_asset/bank_logos/$id.png"
)

object BankDirectory {
    val ALL_BANKS = listOf(
        Bank("vcb", "Ngân hàng Ngoại thương Việt Nam", "Vietcombank", "970436", "#006837", "vcb://", "com.VCB", listOf("vcb", "vietcombank", "vietcom", "vcombank", "vietcombanh")),
        Bank("mbbank", "Ngân hàng Quân đội", "MB Bank", "970422", "#0033A0", "mbbank://", "com.mbmobile", listOf("mb", "mbbank", "mb bank", "mbb", "quandoi", "quan doi")),
        Bank("techcombank", "Ngân hàng Kỹ thương Việt Nam", "Techcombank", "970407", "#E4002B", "techcombank://", "com.techcombank.mobile", listOf("tcb", "techcombank", "techcom", "tech", "techcombanh")),
        Bank("agribank", "Ngân hàng Nông nghiệp & PTNT", "Agribank", "970405", "#800000", "agribank://", "com.vnpay.agribank", listOf("agribank", "argibank", "agri", "argi", "agr", "nongnghiep", "nong nghiep", "agribanh")),
        Bank("bidv", "Ngân hàng Đầu tư và Phát triển", "BIDV", "970418", "#00558F", "bidv://", "com.vnpay.bidv", listOf("bidv", "bi dv", "dautu")),
        Bank("vietinbank", "Ngân hàng Công thương Việt Nam", "VietinBank", "970415", "#005BAA", "vietinbank://", "com.vietinbank.ipay", listOf("vietinbank", "viettinbank", "vietin", "viettin", "ctg", "cong thuong")),
        Bank("vpbank", "Ngân hàng Việt Nam Thịnh Vượng", "VPBank", "970432", "#008853", "vpbank://", "com.vnpay.vpbank", listOf("vpbank", "vpb", "thinh vuong", "vp bank")),
        Bank("acb", "Ngân hàng Á Châu", "ACB", "970416", "#005596", "acb://", "mobile.acb.com.vn", listOf("acb", "a chau")),
        Bank("sacombank", "Ngân hàng Sài Gòn Thương Tín", "Sacombank", "970403", "#004B87", "sacombank://", "src.com.sacombank", listOf("sacombank", "stb", "sacom", "sacombanh")),
        Bank("tpbank", "Ngân hàng Tiên Phong", "TPBank", "970423", "#592D82", "tpbank://", "com.tpb.mb.gprsandroid", listOf("tpbank", "tpb", "tienphong", "tien phong", "tp bank")),
        Bank("msb", "Ngân hàng Hàng Hải", "MSB", "970426", "#EA5B0C", "msb://", "com.msb.mb", listOf("msb", "hang hai")),
        Bank("hdbank", "Ngân hàng Phát triển TP.HCM", "HDBank", "970437", "#EE1C25", "hdbank://", "com.hdbank.mb", listOf("hdbank", "hdb", "hd bank")),
        Bank("ocb", "Ngân hàng Phương Đông", "OCB", "970448", "#008852", "ocb://", "com.mplus.ocb", listOf("ocb", "phuong dong")),
        Bank("vib", "Ngân hàng Quốc tế", "VIB", "970441", "#004B87", "vib://", "com.vib.myvib", listOf("vib", "quoc te")),
        Bank("shb", "Ngân hàng Sài Gòn - Hà Nội", "SHB", "970443", "#F58220", "shb://", "com.shb.mobilebanking", listOf("shb")),
        Bank("scb", "Ngân hàng Sài Gòn", "SCB", "970429", "#005BAC", "scb://", "com.scb.mb", listOf("scb")),
        Bank("seabank", "Ngân hàng Đông Nam Á", "SeABank", "970440", "#ED1C24", "seabank://", "vn.com.seabank.mb", listOf("seabank", "seab")),
        Bank("bacabank", "Ngân hàng Bắc Á", "BacABank", "970409", "#A6192E", "bacabank://", "com.bab.mb", listOf("bacabank", "baca")),
        Bank("abbank", "Ngân hàng An Bình", "ABBANK", "970425", "#008C95", "abbank://", "com.abbank.mb", listOf("abbank")),
        Bank("namabank", "Ngân hàng Nam Á", "NamABank", "970428", "#FFD100", "namabank://", "com.namabank.mb", listOf("namabank", "nam a")),
        Bank("vietbank", "Ngân hàng Việt Nam Thương Tín", "VietBank", "970433", "#005BAA", "vietbank://", "com.vietbank.mb", listOf("vietbank")),
        Bank("kienlongbank", "Ngân hàng Kiên Long", "Kienlongbank", "970452", "#E30613", "kienlongbank://", "com.kienlongbank.mb", listOf("kienlongbank", "kienlong")),
        Bank("saigonbank", "Ngân hàng Sài Gòn Công Thương", "Saigonbank", "970400", "#005BAA", "saigonbank://", "com.saigonbank.mb", listOf("saigonbank")),
        Bank("pvcombank", "Ngân hàng Đại Chúng", "PVcomBank", "970412", "#FED100", "pvcombank://", "com.pvcombank.mb", listOf("pvcombank")),
        Bank("oceanbank", "Ngân hàng Đại Dương", "OceanBank", "970414", "#008CD2", "oceanbank://", "com.oceanbank.mb", listOf("oceanbank")),
        Bank("baovietbank", "Ngân hàng Bảo Việt", "BaoVietBank", "970438", "#0072BC", "baovietbank://", "com.baovietbank.mb", listOf("baovietbank", "baoviet")),
        Bank("shinhan", "Ngân hàng Shinhan Việt Nam", "Shinhan Bank", "970424", "#005BAA", "shinhan://", "com.shinhan.smartbanking", listOf("shinhan")),
        Bank("woori", "Ngân hàng Woori", "Woori Bank", "970457", "#0078D7", "woori://", "com.wooribank.smart", listOf("woori")),
        Bank("uob", "Ngân hàng UOB Việt Nam", "UOB", "970458", "#003366", "uob://", "com.uob.mighty", listOf("uob")),
        // Digital banks & Fintech wallets
        Bank("momo", "Ví điện tử MoMo", "MoMo", "MOMO", "#A50064", "momo://", "com.mservice.momotransfer", listOf("momo", "ví momo", "mservice")),
        Bank("zalopay", "Ví điện tử ZaloPay", "ZaloPay", "ZALOPAY", "#0068FF", "zalopay://", "vn.com.vng.zalopay", listOf("zalopay", "zalo pay")),
        Bank("viettelmoney", "Viettel Money", "Viettel Money", "VTMONEY", "#EE0033", "viettelmoney://", "com.bplus.vtpay", listOf("viettelmoney", "viettel money", "viettel pay")),
        Bank("vnpay", "Ví VNPAY", "VNPAY", "VNPAY", "#005BAA", "vnpay://", "com.vnpay.wallet", listOf("vnpay", "ví vnpay")),
        Bank("timo", "Ngân hàng số Timo", "Timo", "963388", "#7030A0", "timo://", "vn.timo.digitalbanking", listOf("timo")),
        Bank("cake", "Ngân hàng số Cake by VPBank", "Cake", "546034", "#FF007A", "cake://", "xyz.be.cake", listOf("cake", "cake by vpbank")),
        Bank("tnex", "Ngân hàng số TNEX", "TNEX", "970459", "#00D6B2", "tnex://", "msb.com.vn.tnex", listOf("tnex"))
    )

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    fun findBankByKeyword(text: String): Bank? {
        val lower = text.lowercase().trim()

        // 1. Exact or keyword match
        for (bank in ALL_BANKS) {
            for (kw in bank.keywords) {
                if (lower.contains(kw) || Regex("\\b${Regex.escape(kw)}\\b").containsMatchIn(lower)) {
                    return bank
                }
            }
        }

        // 2. Fuzzy / Typo match on each word token (e.g. "argibank", "vietcombanh")
        val words = lower.split(Regex("[^a-zA-Z0-9]+")).filter { it.length >= 3 }
        for (word in words) {
            for (bank in ALL_BANKS) {
                for (kw in bank.keywords) {
                    if (kw.length >= 3 && Math.abs(kw.length - word.length) <= 2) {
                        val distance = levenshteinDistance(word, kw)
                        if (distance <= 1 || (word.length >= 7 && distance <= 2)) {
                            return bank
                        }
                    }
                }
            }
        }

        return null
    }

    fun findBankById(id: String): Bank? {
        return ALL_BANKS.firstOrNull { it.id == id }
    }

    fun findBankByBin(bin: String): Bank? {
        return ALL_BANKS.firstOrNull { it.bin == bin }
    }
}
