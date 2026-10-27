export interface BankInfo {
  code: string;
  shortName: string;
  name: string;
  bin: string;
  scheme: string;
  color: string;
  category: "bank" | "digital" | "wallet";
}

export const VIETNAM_BANKS: BankInfo[] = [
  // Big 4 & Commercial Banks
  { code: "VCB", shortName: "Vietcombank", name: "Ngân hàng Ngoại Thương Việt Nam", bin: "970436", scheme: "vietcombank://", color: "#00703c", category: "bank" },
  { code: "CTG", shortName: "VietinBank", name: "Ngân hàng Công Thương Việt Nam", bin: "970415", scheme: "vietinbank://", color: "#004f9e", category: "bank" },
  { code: "BID", shortName: "BIDV", name: "Ngân hàng Đầu tư & Phát triển Việt Nam", bin: "970418", scheme: "bidv://", color: "#006847", category: "bank" },
  { code: "VBA", shortName: "Agribank", name: "Ngân hàng Nông nghiệp & PTNT", bin: "970405", scheme: "agribank://", color: "#8e1838", category: "bank" },
  { code: "TCB", shortName: "Techcombank", name: "Ngân hàng Kỹ Thương Việt Nam", bin: "970407", scheme: "techcombank://", color: "#e61b23", category: "bank" },
  { code: "MB", shortName: "MBBank", name: "Ngân hàng Quân Đội", bin: "970422", scheme: "mbmobile://", color: "#163a8a", category: "bank" },
  { code: "VPB", shortName: "VPBank", name: "Ngân hàng Việt Nam Thịnh Vượng", bin: "970432", scheme: "vpbankneo://", color: "#008744", category: "bank" },
  { code: "ACB", shortName: "ACB", name: "Ngân hàng Á Châu", bin: "970416", scheme: "acb://", color: "#0066b3", category: "bank" },
  { code: "STB", shortName: "Sacombank", name: "Ngân hàng Sài Gòn Thương Tín", bin: "970403", scheme: "sacombank://", color: "#004b87", category: "bank" },
  { code: "HDB", shortName: "HDBank", name: "Ngân hàng Phát triển TP.HCM", bin: "970437", scheme: "hdbank://", color: "#d32f2f", category: "bank" },
  { code: "TPB", shortName: "TPBank", name: "Ngân hàng Tiên Phong", bin: "970423", scheme: "tpbank://", color: "#7b2c89", category: "bank" },
  { code: "VIB", shortName: "VIB", name: "Ngân hàng Quốc tế Việt Nam", bin: "970441", scheme: "vib://", color: "#005a9c", category: "bank" },
  { code: "SHB", shortName: "SHB", name: "Ngân hàng Sài Gòn - Hà Nội", bin: "970443", scheme: "shb://", color: "#f58220", category: "bank" },
  { code: "MSB", shortName: "MSB", name: "Ngân hàng Hàng Hải Việt Nam", bin: "970426", scheme: "msb://", color: "#ea5b10", category: "bank" },
  { code: "OCB", shortName: "OCB", name: "Ngân hàng Phương Đông", bin: "970448", scheme: "ocb://", color: "#008853", category: "bank" },
  { code: "SSB", shortName: "SeABank", name: "Ngân hàng Đông Nam Á", bin: "970440", scheme: "seabank://", color: "#c61d23", category: "bank" },
  { code: "LPB", shortName: "LPBank", name: "Ngân hàng Lộc Phát Việt Nam", bin: "970449", scheme: "lienvietpostbank://", color: "#ea6923", category: "bank" },
  { code: "BAB", shortName: "BacABank", name: "Ngân hàng Bắc Á", bin: "970409", scheme: "bacabank://", color: "#b8860b", category: "bank" },
  { code: "NAB", shortName: "NamABank", name: "Ngân hàng Nam Á", bin: "970428", scheme: "namabank://", color: "#eab308", category: "bank" },
  { code: "PVC", shortName: "PVcomBank", name: "Ngân hàng Đại Chúng Việt Nam", bin: "970412", scheme: "pvcombank://", color: "#0284c7", category: "bank" },
  { code: "KLB", shortName: "Kienlongbank", name: "Ngân hàng Kiên Long", bin: "970452", scheme: "kienlongbank://", color: "#16a34a", category: "bank" },
  { code: "VAB", shortName: "VietABank", name: "Ngân hàng Việt Á", bin: "970427", scheme: "vietabank://", color: "#0d9488", category: "bank" },
  { code: "BVB", shortName: "BVBank", name: "Ngân hàng Bản Việt", bin: "970454", scheme: "bvbank://", color: "#2563eb", category: "bank" },
  { code: "BAOVIET", shortName: "BaoVietBank", name: "Ngân hàng Bảo Việt", bin: "970438", scheme: "baovietbank://", color: "#0284c7", category: "bank" },
  { code: "SGB", shortName: "Saigonbank", name: "Ngân hàng Sài Gòn Công Thương", bin: "970400", scheme: "saigonbank://", color: "#004b87", category: "bank" },
  { code: "ABB", shortName: "ABBank", name: "Ngân hàng An Bình", bin: "970425", scheme: "abbank://", color: "#0284c7", category: "bank" },
  { code: "WOO", shortName: "Woori Bank", name: "Ngân hàng Woori Việt Nam", bin: "970457", scheme: "wooribank://", color: "#0284c7", category: "bank" },
  { code: "SHBVN", shortName: "Shinhan Bank", name: "Ngân hàng Shinhan Việt Nam", bin: "970424", scheme: "shinhan://", color: "#004687", category: "bank" },

  // Digital Banks & Fintech
  { code: "TIMO", shortName: "Timo", name: "Ngân hàng số Timo", bin: "963388", scheme: "timo://", color: "#7c3aed", category: "digital" },
  { code: "CAKE", shortName: "Cake by VPBank", name: "Ngân hàng số Cake", bin: "546034", scheme: "cake://", color: "#db2777", category: "digital" },
  { code: "TNEX", shortName: "TNEX", name: "Ngân hàng số TNEX", bin: "970407", scheme: "tnex://", color: "#06b6d4", category: "digital" },
  { code: "VTLMONEY", shortName: "Viettel Money", name: "Ví điện tử Viettel Money", bin: "971005", scheme: "viettelmoney://", color: "#dc2626", category: "wallet" },
  { code: "VNPTMONEY", shortName: "VNPT Money", name: "Ví điện tử VNPT Money", bin: "971011", scheme: "vnptmoney://", color: "#2563eb", category: "wallet" },
  { code: "MOMO", shortName: "Ví MoMo", name: "Ví điện tử MoMo", bin: "970400", scheme: "momo://", color: "#a50064", category: "wallet" },
  { code: "ZALOPAY", shortName: "ZaloPay", name: "Ví điện tử ZaloPay", bin: "970400", scheme: "zalopay://", color: "#0068ff", category: "wallet" },
  { code: "SHOPEEPAY", shortName: "ShopeePay", name: "Ví điện tử ShopeePay", bin: "970400", scheme: "shopeepay://", color: "#ea580c", category: "wallet" },
  { code: "CASH", shortName: "Tiền mặt", name: "Tiền mặt tại ví", bin: "000000", scheme: "", color: "#166534", category: "wallet" },
];
