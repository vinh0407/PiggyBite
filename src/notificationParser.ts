import { VIETNAM_BANKS, BankInfo } from "./banks";
import { AccountItem } from "./App";

export interface ParsedBankNotification {
  bankCode: string;
  bankName: string;
  amount: number; // positive for income, negative for expense
  note: string;
  sender: string;
  accountNumber?: string;
  rawText: string;
}

export function parseBankNotificationText(
  text: string,
  userAccounts: AccountItem[]
): ParsedBankNotification | null {
  if (!text || text.trim().length < 5) return null;

  const normalized = text.replace(/[\r\n]+/g, " ");

  // 1. Detect Bank
  let detectedBank: BankInfo | null = null;
  const lowerText = normalized.toLowerCase();

  for (const b of VIETNAM_BANKS) {
    const codeMatch = new RegExp(`\\b${b.code.toLowerCase()}\\b`, "i").test(lowerText);
    const shortNameMatch = lowerText.includes(b.shortName.toLowerCase());
    const nameMatch = lowerText.includes(b.name.toLowerCase());

    if (codeMatch || shortNameMatch || nameMatch) {
      detectedBank = b;
      break;
    }
  }

  // Fallback bank if not directly detected by name
  if (!detectedBank) {
    if (lowerText.includes("vcb") || lowerText.includes("vietcom")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "VCB") || null;
    } else if (lowerText.includes("tcb") || lowerText.includes("techcom")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "TCB") || null;
    } else if (lowerText.includes("mb bank") || lowerText.includes("mbbank") || lowerText.includes("mb")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "MB") || null;
    } else if (lowerText.includes("momo")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "MOMO") || null;
    } else if (lowerText.includes("zalopay") || lowerText.includes("zalo pay")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "ZALOPAY") || null;
    } else if (lowerText.includes("vietinbank") || lowerText.includes("ctg")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "CTG") || null;
    } else if (lowerText.includes("bidv") || lowerText.includes("bid")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "BID") || null;
    } else if (lowerText.includes("agribank") || lowerText.includes("vba")) {
      detectedBank = VIETNAM_BANKS.find((b) => b.code === "VBA") || null;
    }
  }

  // 2. Detect Amount & Sign (Income vs Expense)
  // Check if text indicates income or expense
  const isIncome =
    normalized.includes("+") ||
    /nhận được|nhan duoc|nhận tiền|nhan tien|cộng|cong|tăng|tang|credit|\bcr\b/i.test(normalized);

  const isExpense =
    (!isIncome && normalized.includes("-")) ||
    /thanh toán|thanh toan|chuyển đi|chuyen di|trừ tiền|tru tien|giảm|giam|debit|\bdr\b/i.test(normalized);

  // Regex patterns to capture amounts like +500,000VND, 150.000d, 2,500,000, 1000000
  let matchedAmount = 0;

  // Pattern 1: with explicit + or - sign, e.g. +500,000 or -150.000
  const signedMatch = normalized.match(/([+-])\s*([0-9]{1,3}(?:[.,][0-9]{3})+|[0-9]{4,9})/);
  if (signedMatch) {
    const rawNum = signedMatch[2].replace(/[.,]/g, "");
    matchedAmount = parseFloat(rawNum) || 0;
    if (signedMatch[1] === "-") {
      matchedAmount = -matchedAmount;
    }
  } else {
    // Pattern 2: "nhan duoc 150.000d" or "so tien: 500,000 VND"
    const keywordMatch = normalized.match(
      /(?:nhận được|nhan duoc|số tiền|so tien|gd|giao dịch|sodu|số dư)\s*:?\s*([0-9]{1,3}(?:[.,][0-9]{3})+|[0-9]{4,9})/i
    );
    if (keywordMatch) {
      const rawNum = keywordMatch[1].replace(/[.,]/g, "");
      matchedAmount = parseFloat(rawNum) || 0;
      if (isExpense) matchedAmount = -matchedAmount;
    } else {
      // Pattern 3: any currency amount followed by VND, đ, d, dong
      const currencyMatch = normalized.match(
        /([0-9]{1,3}(?:[.,][0-9]{3})+|[0-9]{4,9})\s*(?:vnd|vnđ|đ|d|dong|đồng)/i
      );
      if (currencyMatch) {
        const rawNum = currencyMatch[1].replace(/[.,]/g, "");
        matchedAmount = parseFloat(rawNum) || 0;
        if (isExpense) matchedAmount = -matchedAmount;
      }
    }
  }

  if (matchedAmount === 0) return null;

  // If detected positive income
  if (isIncome && matchedAmount < 0) {
    matchedAmount = Math.abs(matchedAmount);
  }

  // 3. Extract Note / Sender
  let note = "Nhận tiền chuyển khoản";
  let sender = "";

  const ndMatch = normalized.match(/(?:nd|nội dung|noi dung|ref|lời nhắn|loi nhan)\s*[:.]\s*([^|;]+)/i);
  if (ndMatch) {
    note = ndMatch[1].trim();
  }

  const tuMatch = normalized.match(/(?:từ|tu)\s+(?:tk\s+)?([A-Z0-9\s]{3,30})/i);
  if (tuMatch) {
    sender = tuMatch[1].trim();
  }

  // 4. Extract Account Number (if any)
  let accountNumber = "";
  const accMatch = normalized.match(/(?:tk|số tk|so tk|stk)\s*[:.]?\s*([0-9]{6,16})/i);
  if (accMatch) {
    accountNumber = accMatch[1];
  }

  const finalBankName = detectedBank?.shortName || userAccounts[0]?.bankName || "Vietcombank";
  const finalBankCode = detectedBank?.code || userAccounts[0]?.bankCode || "VCB";

  return {
    bankCode: finalBankCode,
    bankName: finalBankName,
    amount: matchedAmount,
    note: note,
    sender: sender,
    accountNumber: accountNumber,
    rawText: normalized,
  };
}
