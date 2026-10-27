import React, { ChangeEvent, FormEvent, useEffect, useState } from "react";
import { VIETNAM_BANKS, BankInfo } from "./banks";
import { parseBankNotificationText } from "./notificationParser";

export interface AccountItem {
  id: string;
  bankCode: string;
  bankName: string;
  accountName: string;
  accountNumber: string;
  balance: number;
  color: string;
}

export interface TransactionItem {
  id: string;
  name: string;
  cat: string;
  amount: number; // negative for expense, positive for income
  date: string;
  rawDate: string; // ISO date for filtering
  bankId: string;
  bankName: string;
  note?: string;
}

// Initial sample data stored in localStorage
const defaultAccounts: AccountItem[] = [
  { id: "acc-1", bankCode: "VCB", bankName: "Vietcombank", accountName: "Tài khoản chính", accountNumber: "1028394857", balance: 14500000, color: "#00703c" },
  { id: "acc-2", bankCode: "MOMO", bankName: "Ví MoMo", accountName: "Ví chi tiêu hàng ngày", accountNumber: "0987654321", balance: 850000, color: "#a50064" },
  { id: "acc-3", bankCode: "TCB", bankName: "Techcombank", accountName: "Lương & Dự phòng", accountNumber: "19038475869", balance: 25000000, color: "#e61b23" },
  { id: "acc-4", bankCode: "CASH", bankName: "Tiền mặt", accountName: "Tiền mặt trong ví", accountNumber: "", balance: 450000, color: "#166534" },
];

const defaultTransactions: TransactionItem[] = [
  { id: "t-1", name: "Cà phê sáng Highlands", cat: "Ăn uống", amount: -45000, date: "Hôm nay, 08:30", rawDate: new Date().toISOString(), bankId: "acc-2", bankName: "Ví MoMo", note: "Thanh toán QR" },
  { id: "t-2", name: "Siêu thị WinMart", cat: "Mua sắm", amount: -182000, date: "Hôm nay, 11:45", rawDate: new Date().toISOString(), bankId: "acc-1", bankName: "Vietcombank", note: "Thanh toán QR" },
  { id: "t-3", name: "Cơm trưa văn phòng", cat: "Ăn uống", amount: -55000, date: "Hôm qua, 12:15", rawDate: new Date(Date.now() - 86400000).toISOString(), bankId: "acc-1", bankName: "Vietcombank", note: "Chuyển khoản QR" },
];

export const formatMoney = (n: number) => {
  return new Intl.NumberFormat("vi-VN").format(Math.round(n)) + "đ";
};

export const formatMoneyFull = (n: number) => {
  return new Intl.NumberFormat("vi-VN").format(Math.round(n)) + " đ";
};

export default function App() {
  // Offline-first local storage states
  const [accounts, setAccounts] = useState<AccountItem[]>(() => {
    const saved = localStorage.getItem("piggybite_accounts_v2");
    return saved ? JSON.parse(saved) : defaultAccounts;
  });

  const [transactions, setTransactions] = useState<TransactionItem[]>(() => {
    const saved = localStorage.getItem("piggybite_transactions_v2");
    return saved ? JSON.parse(saved) : defaultTransactions;
  });

  const [activeTab, setActiveTab] = useState<"home" | "history" | "reports" | "accounts" | "settings">("home");
  const [hideAmount, setHideAmount] = useState<boolean>(false);

  // Modals & Sub-flows
  const [showQRFlow, setShowQRFlow] = useState<boolean>(false);
  const [showManualAdd, setShowManualAdd] = useState<boolean>(false);
  const [showMyQR, setShowMyQR] = useState<boolean>(false);
  const [showAnalysis, setShowAnalysis] = useState<boolean>(false);
  const [showAddAccount, setShowAddAccount] = useState<boolean>(false);
  const [showNotificationSimulator, setShowNotificationSimulator] = useState<boolean>(false);

  // Live incoming notification toast banner
  const [toastNotification, setToastNotification] = useState<{
    bankName: string;
    amount: number;
    note: string;
    newBalance: number;
  } | null>(null);

  // Save to localStorage
  useEffect(() => {
    localStorage.setItem("piggybite_accounts_v2", JSON.stringify(accounts));
  }, [accounts]);

  useEffect(() => {
    localStorage.setItem("piggybite_transactions_v2", JSON.stringify(transactions));
  }, [transactions]);

  // Total balance aggregated from all accounts
  const totalBalance = accounts.reduce((sum, a) => sum + (a.balance || 0), 0);

  // Today & Month expense calculation
  const todayStr = new Date().toDateString();
  const currentMonth = new Date().getMonth();
  const currentYear = new Date().getFullYear();

  const todayExpense = Math.abs(
    transactions
      .filter((t) => t.amount < 0 && new Date(t.rawDate).toDateString() === todayStr)
      .reduce((sum, t) => sum + t.amount, 0)
  );

  const monthExpense = Math.abs(
    transactions
      .filter((t) => {
        const d = new Date(t.rawDate);
        return t.amount < 0 && d.getMonth() === currentMonth && d.getFullYear() === currentYear;
      })
      .reduce((sum, t) => sum + t.amount, 0)
  );

  // Dynamic Vietnamese date header: e.g. "Thứ Hai, 27 tháng 10"
  const getVietnameseDateHeader = () => {
    const now = new Date();
    const daysOfWeek = ["Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"];
    const dayOfWeek = daysOfWeek[now.getDay()];
    const date = now.getDate();
    const month = now.getMonth() + 1;
    return `${dayOfWeek}, ${date} tháng ${month}`;
  };

  // Handler for adding transaction manually
  const handleAddTransaction = (newT: Omit<TransactionItem, "id">) => {
    const item: TransactionItem = {
      ...newT,
      id: "tx-" + Date.now(),
    };
    setTransactions((prev) => [item, ...prev]);

    // Update account balance
    setAccounts((prev) =>
      prev.map((acc) => {
        if (acc.id === newT.bankId) {
          return { ...acc, balance: (acc.balance || 0) + newT.amount };
        }
        return acc;
      })
    );
  };

  // Automatic Money Transfer Notification Handler
  const processIncomingNotification = (notificationText: string): boolean => {
    const parsed = parseBankNotificationText(notificationText, accounts);
    if (!parsed) return false;

    // Find matching account in user's created accounts list
    let targetAccount = accounts.find(
      (a) =>
        a.bankCode.toLowerCase() === parsed.bankCode.toLowerCase() ||
        a.bankName.toLowerCase().includes(parsed.bankName.toLowerCase()) ||
        (parsed.accountNumber && a.accountNumber && a.accountNumber.includes(parsed.accountNumber))
    );

    // If no matching account yet, automatically add this bank to the user's wallet!
    if (!targetAccount) {
      const bankTemplate =
        VIETNAM_BANKS.find((b) => b.code.toLowerCase() === parsed.bankCode.toLowerCase()) ||
        VIETNAM_BANKS[0];

      targetAccount = {
        id: "acc-" + Date.now(),
        bankCode: bankTemplate.code,
        bankName: bankTemplate.shortName,
        accountName: `Tài khoản ${bankTemplate.shortName}`,
        accountNumber: parsed.accountNumber || "",
        balance: Math.max(0, parsed.amount),
        color: bankTemplate.color,
      };

      setAccounts((prev) => [...prev, targetAccount!]);
    } else {
      // Update balance of the existing bank account
      setAccounts((prev) =>
        prev.map((acc) =>
          acc.id === targetAccount!.id
            ? { ...acc, balance: (acc.balance || 0) + parsed.amount }
            : acc
        )
      );
    }

    // Add transaction to history
    const isIncome = parsed.amount > 0;
    const newTx: TransactionItem = {
      id: "tx-auto-" + Date.now(),
      name: isIncome ? `Nhận tiền ${targetAccount.bankName}` : `Chi tiền ${targetAccount.bankName}`,
      cat: isIncome ? "Thu nhập" : "Chi tiêu",
      amount: parsed.amount,
      date: "Hôm nay, " + new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      rawDate: new Date().toISOString(),
      bankId: targetAccount.id,
      bankName: targetAccount.bankName,
      note: parsed.note || parsed.sender || "Thông báo ngân hàng tự động",
    };

    setTransactions((prev) => [newTx, ...prev]);

    // Show floating toast
    setToastNotification({
      bankName: targetAccount.bankName,
      amount: parsed.amount,
      note: parsed.note || parsed.sender,
      newBalance: (targetAccount.balance || 0) + parsed.amount,
    });

    // Auto dismiss toast after 4.5 seconds
    setTimeout(() => {
      setToastNotification(null);
    }, 4500);

    return true;
  };

  // System window listener for incoming broadcasted notifications
  useEffect(() => {
    const handleNotificationEvent = (e: any) => {
      if (e?.detail?.text) {
        processIncomingNotification(e.detail.text);
      }
    };
    window.addEventListener("piggybite_notification", handleNotificationEvent);
    return () => window.removeEventListener("piggybite_notification", handleNotificationEvent);
  }, [accounts]);

  return (
    <div className="app-viewport">
      {/* Live Floating In-app Notification Banner */}
      {toastNotification && (
        <div className="live-notification-toast">
          <div className="toast-left-icon">🔔</div>
          <div className="toast-text-content">
            <div className="toast-title-row">
              <b>{toastNotification.bankName}</b>
              <span className={`toast-amt-tag ${toastNotification.amount >= 0 ? "income" : "expense"}`}>
                {toastNotification.amount >= 0 ? "+" : "−"}{formatMoney(Math.abs(toastNotification.amount))}
              </span>
            </div>
            <p className="toast-sub-msg">
              {toastNotification.amount >= 0 ? "Đã tự động cộng vào tài khoản" : "Đã tự động trừ từ tài khoản"} <b>{toastNotification.bankName}</b>.
              Số dư mới: <b>{formatMoney(toastNotification.newBalance)}</b>
            </p>
          </div>
          <button className="toast-close-btn" onClick={() => setToastNotification(null)}>✕</button>
        </div>
      )}

      {/* Dynamic Content based on activeTab */}
      <main className="content-container">
        {activeTab === "home" && (
          <HomeScreen
            dateHeader={getVietnameseDateHeader()}
            monthExpense={monthExpense}
            todayExpense={todayExpense}
            hideAmount={hideAmount}
            onToggleHide={() => setHideAmount(!hideAmount)}
            onOpenQR={() => setShowQRFlow(true)}
            onOpenManualAdd={() => setShowManualAdd(true)}
            onOpenMyQR={() => setShowMyQR(true)}
            onOpenNotificationSimulator={() => setShowNotificationSimulator(true)}
            totalBalance={totalBalance}
            accounts={accounts}
            transactions={transactions}
            onOpenAnalysis={() => setShowAnalysis(true)}
            onOpenAccounts={() => setActiveTab("accounts")}
          />
        )}

        {activeTab === "history" && (
          <HistoryScreen
            transactions={transactions}
            accounts={accounts}
            onOpenManualAdd={() => setShowManualAdd(true)}
          />
        )}

        {activeTab === "reports" && (
          <AnalysisView
            accounts={accounts}
            transactions={transactions}
            totalBalance={totalBalance}
            monthExpense={monthExpense}
            todayExpense={todayExpense}
            onClose={() => setActiveTab("home")}
            isTab
          />
        )}

        {activeTab === "accounts" && (
          <AccountsManagerScreen
            accounts={accounts}
            totalBalance={totalBalance}
            onAddAccount={() => setShowAddAccount(true)}
            onUpdateAccount={(updated) => {
              setAccounts((prev) => prev.map((a) => (a.id === updated.id ? updated : a)));
            }}
            onDeleteAccount={(id) => {
              setAccounts((prev) => prev.filter((a) => a.id !== id));
            }}
          />
        )}

        {activeTab === "settings" && (
          <SettingsScreen
            accounts={accounts}
            totalBalance={totalBalance}
            transactionsCount={transactions.length}
            onOpenNotificationSimulator={() => setShowNotificationSimulator(true)}
            onResetData={() => {
              if (window.confirm("Bạn có chắc chắn muốn đặt lại dữ liệu về mặc định?")) {
                setAccounts(defaultAccounts);
                setTransactions(defaultTransactions);
              }
            }}
          />
        )}
      </main>

      {/* Minimalist Bottom Navigation Bar matching screenshot */}
      <nav className="minimal-bottom-bar">
        <button
          className={`nav-btn ${activeTab === "home" ? "active" : ""}`}
          onClick={() => setActiveTab("home")}
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
            <polyline points="9 22 9 12 15 12 15 22" />
          </svg>
          <span>Trang chủ</span>
        </button>

        <button
          className={`nav-btn ${activeTab === "history" ? "active" : ""}`}
          onClick={() => setActiveTab("history")}
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
          <span>Lịch sử</span>
        </button>

        {/* Center floating scan QR button */}
        <button className="nav-center-qr" onClick={() => setShowQRFlow(true)} title="Quét mã QR thanh toán">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="3" y="3" width="7" height="7" rx="1.5" />
            <rect x="14" y="3" width="7" height="7" rx="1.5" />
            <rect x="14" y="14" width="7" height="7" rx="1.5" />
            <rect x="3" y="14" width="7" height="7" rx="1.5" />
          </svg>
        </button>

        <button
          className={`nav-btn ${activeTab === "reports" ? "active" : ""}`}
          onClick={() => setActiveTab("reports")}
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <line x1="18" y1="20" x2="18" y2="10" />
            <line x1="12" y1="20" x2="12" y2="4" />
            <line x1="6" y1="20" x2="6" y2="14" />
          </svg>
          <span>Báo cáo</span>
        </button>

        <button
          className={`nav-btn ${activeTab === "settings" || activeTab === "accounts" ? "active" : ""}`}
          onClick={() => setActiveTab("accounts")}
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M19 7V4a1 1 0 0 0-1-1H5a2 2 0 0 0 0 4h15a1 1 0 0 1 1 1v4h-3a2 2 0 0 0 0 4h3a1 1 0 0 0 1-1v-2a1 1 0 0 0-1-1" />
            <path d="M3 5v14a2 2 0 0 0 2 2h15a1 1 0 0 0 1-1v-4" />
          </svg>
          <span>Ví & Cài đặt</span>
        </button>
      </nav>

      {/* QR Payment Full Workflow Modal */}
      {showQRFlow && (
        <QRPaymentModal
          accounts={accounts}
          onClose={() => setShowQRFlow(false)}
          onSuccess={(transaction) => {
            handleAddTransaction(transaction);
            setShowQRFlow(false);
          }}
        />
      )}

      {/* Manual Add Transaction Modal */}
      {showManualAdd && (
        <ManualAddModal
          accounts={accounts}
          onClose={() => setShowManualAdd(false)}
          onAdd={(t) => {
            handleAddTransaction(t);
            setShowManualAdd(false);
          }}
        />
      )}

      {/* My QR Code Modal */}
      {showMyQR && (
        <MyQRModal accounts={accounts} onClose={() => setShowMyQR(false)} />
      )}

      {/* Financial Analysis Modal */}
      {showAnalysis && (
        <AnalysisView
          accounts={accounts}
          transactions={transactions}
          totalBalance={totalBalance}
          monthExpense={monthExpense}
          todayExpense={todayExpense}
          onClose={() => setShowAnalysis(false)}
        />
      )}

      {/* Add / Edit Bank Account Modal */}
      {showAddAccount && (
        <AddAccountModal
          onClose={() => setShowAddAccount(false)}
          onSave={(newAcc) => {
            setAccounts((prev) => [...prev, newAcc]);
            setShowAddAccount(false);
          }}
        />
      )}

      {/* Bank Notification Listener & Simulator Modal */}
      {showNotificationSimulator && (
        <NotificationSimulatorModal
          onClose={() => setShowNotificationSimulator(false)}
          onProcess={(text) => {
            const success = processIncomingNotification(text);
            if (success) {
              setShowNotificationSimulator(false);
            } else {
              alert("Không nhận diện được số tiền hoặc ngân hàng trong văn bản thông báo. Vui lòng kiểm tra lại nội dung!");
            }
          }}
        />
      )}
    </div>
  );
}

// ==========================================
// 1. HOME SCREEN (EXACT MATCH TO SCREENSHOT)
// ==========================================
function HomeScreen({
  dateHeader,
  monthExpense,
  todayExpense,
  hideAmount,
  onToggleHide,
  onOpenQR,
  onOpenManualAdd,
  onOpenMyQR,
  onOpenNotificationSimulator,
  totalBalance,
  accounts,
  transactions,
  onOpenAnalysis,
  onOpenAccounts,
}: {
  dateHeader: string;
  monthExpense: number;
  todayExpense: number;
  hideAmount: boolean;
  onToggleHide: () => void;
  onOpenQR: () => void;
  onOpenManualAdd: () => void;
  onOpenMyQR: () => void;
  onOpenNotificationSimulator: () => void;
  totalBalance: number;
  accounts: AccountItem[];
  transactions: TransactionItem[];
  onOpenAnalysis: () => void;
  onOpenAccounts: () => void;
}) {
  const currentMonthNum = new Date().getMonth() + 1;

  return (
    <div className="home-screen">
      {/* Top Bar with Green Arch Icon and Notification Icon */}
      <header className="home-top-bar">
        <div className="brand-arch-icon" aria-label="PiggyBite Logo">
          <svg width="28" height="32" viewBox="0 0 28 32" fill="none">
            <path
              d="M5 30V14C5 8.47715 9.47715 4 15 4C20.5228 4 25 8.47715 25 14V30"
              stroke="#1b4332"
              strokeWidth="5.5"
              strokeLinecap="round"
            />
          </svg>
        </div>

        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          {/* APK Download Button */}
          <a
            href="https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk"
            download="PiggyBite.apk"
            className="top-apk-btn"
            title="Tải tệp cài đặt PiggyBite APK cho Android"
          >
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
              <polyline points="7 10 12 15 17 10" />
              <line x1="12" y1="15" x2="12" y2="3" />
            </svg>
            <span>Tải APK</span>
          </a>

          {/* Bell button: Opens notification listener / test simulator */}
          <button
            className="top-bell-btn"
            onClick={onOpenNotificationSimulator}
            aria-label="Thông báo biến động số dư"
            title="Tự động cập nhật từ thông báo chuyển tiền"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
              <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
            </svg>
            <span className="bell-active-dot" />
          </button>
        </div>
      </header>

      {/* Large Date Title */}
      <h1 className="home-date-title">{dateHeader}</h1>

      {/* Main Expense Card (Card 1 in Screenshot) */}
      <section className="main-expense-card">
        <div className="card-top-row">
          <span className="card-top-label">
            Đã chi tháng {currentMonthNum} · theo sổ PiggyBite
          </span>
          <button className="eye-toggle-btn" onClick={onToggleHide} aria-label="Ẩn/Hiện số tiền">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              {hideAmount ? (
                <>
                  <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                  <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                  <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                  <line x1="2" y1="2" x2="22" y2="22" />
                </>
              ) : (
                <>
                  <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                  <circle cx="12" cy="12" r="3" />
                </>
              )}
            </svg>
          </button>
        </div>

        {/* Big Expense Number */}
        <div className="big-expense-number">
          {hideAmount ? "••••••" : formatMoney(monthExpense)}
        </div>

        <div className="card-divider" />

        {/* Two stats columns */}
        <div className="card-columns-row">
          <div className="stat-col">
            <span className="stat-label">Hôm nay</span>
            <b className="stat-val">{hideAmount ? "••••" : formatMoney(todayExpense)}</b>
          </div>
          <div className="stat-col">
            <span className="stat-label">Tháng này</span>
            <b className="stat-val">{hideAmount ? "••••" : formatMoney(monthExpense)}</b>
          </div>
        </div>
      </section>

      {/* Main Action Buttons Matching Screenshot */}
      <div className="home-action-buttons">
        {/* Full width Green QR Button */}
        <button className="primary-qr-button" onClick={onOpenQR}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="3" y="3" width="18" height="18" rx="4" />
            <circle cx="12" cy="12" r="3" />
          </svg>
          <span>Quét QR thanh toán</span>
        </button>

        {/* Two sub buttons row */}
        <div className="sub-actions-row">
          <button className="sub-action-btn" onClick={onOpenManualAdd}>
            <span className="plus-icon">+</span>
            <span>Ghi tay</span>
          </button>
          <button className="sub-action-btn" onClick={onOpenMyQR}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <rect x="3" y="3" width="7" height="7" rx="1.5" />
              <rect x="14" y="3" width="7" height="7" rx="1.5" />
              <rect x="14" y="14" width="7" height="7" rx="1.5" />
              <rect x="3" y="14" width="7" height="7" rx="1.5" />
            </svg>
            <span>QR của tôi</span>
          </button>
        </div>
      </div>

      {/* Multi-bank & Digital Wallet Summary Card ("Tổng hợp toàn bộ số tiền") */}
      <section className="wallet-summary-card">
        <div className="wallet-card-header">
          <div>
            <span className="wallet-eyebrow">TỔNG TÀI SẢN KHẢ DỤNG</span>
            <h2 className="wallet-total-amount">
              {hideAmount ? "••••••••" : formatMoneyFull(totalBalance)}
            </h2>
          </div>
          <button className="wallet-analysis-btn" onClick={onOpenAnalysis}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <line x1="18" y1="20" x2="18" y2="10" />
              <line x1="12" y1="20" x2="12" y2="4" />
              <line x1="6" y1="20" x2="6" y2="14" />
            </svg>
            <span>Phân tích chi tiêu</span>
          </button>
        </div>

        {/* Bank badges horizontal list */}
        <div className="bank-chips-scroll">
          {accounts.map((acc) => (
            <div key={acc.id} className="bank-chip" onClick={onOpenAccounts}>
              <span className="bank-chip-indicator" style={{ background: acc.color || "#00703c" }} />
              <div className="bank-chip-info">
                <b>{acc.bankName}</b>
                <small>{hideAmount ? "••••" : formatMoney(acc.balance)}</small>
              </div>
            </div>
          ))}
          <button className="bank-chip add-chip" onClick={onOpenAccounts}>
            <span>+ Quản lý ví</span>
          </button>
        </div>
      </section>

      {/* Auto-sync notification helper pill */}
      <div className="auto-notify-pill" onClick={onOpenNotificationSimulator}>
        <span className="notify-pulse-dot" />
        <span className="notify-pill-text">
          Tự động nhận thông báo chuyển tiền & cộng vào ví tương ứng
        </span>
        <span className="notify-pill-arrow">→</span>
      </div>

      {/* Recent Transactions List or Empty State matching screenshot */}
      <section className="transactions-container-card">
        {transactions.length === 0 ? (
          <div className="empty-state-box">
            <div className="empty-state-icon" />
            <h3 className="empty-state-title">Chưa có khoản nào</h3>
            <p className="empty-state-sub">Quét QR khi thanh toán để bắt đầu.</p>
          </div>
        ) : (
          <div className="recent-transactions-list">
            <div className="recent-tx-head">
              <span>Giao dịch gần đây</span>
              <small>{transactions.length} khoản</small>
            </div>
            {transactions.slice(0, 5).map((t) => (
              <div key={t.id} className="tx-item-row">
                <div className="tx-item-left">
                  <div className={`tx-cat-badge ${t.amount < 0 ? "expense" : "income"}`}>
                    {t.amount < 0 ? "−" : "+"}
                  </div>
                  <div>
                    <b className="tx-name">{t.name}</b>
                    <small className="tx-meta">{t.bankName} · {t.date}</small>
                  </div>
                </div>
                <div className={`tx-amount ${t.amount < 0 ? "expense" : "income"}`}>
                  {t.amount < 0 ? "−" : "+"}{formatMoney(Math.abs(t.amount))}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

// ==========================================
// 2. QR PAYMENT & MONEY TRANSFER WORKFLOW
// ==========================================
function QRPaymentModal({
  accounts,
  onClose,
  onSuccess,
}: {
  accounts: AccountItem[];
  onClose: () => void;
  onSuccess: (t: Omit<TransactionItem, "id">) => void;
}) {
  const [step, setStep] = useState<"scan" | "enter_amount" | "choose_bank" | "complete">("scan");

  // Receiver Info
  const [receiverName, setReceiverName] = useState<string>("CỬA HÀNG HIGHLANDS COFFEE");
  const [receiverBank, setReceiverBank] = useState<string>("Vietcombank");
  const [receiverBin, setReceiverBin] = useState<string>("970436");
  const [receiverAccount, setReceiverAccount] = useState<string>("1028394857");
  const [amountStr, setAmountStr] = useState<string>("45000");
  const [content, setContent] = useState<string>("Thanh toan hoa don");

  // Selected Bank to pay from
  const [selectedAccount, setSelectedAccount] = useState<AccountItem | null>(accounts[0] || null);

  const numAmount = parseFloat(amountStr.replace(/[^0-9]/g, "")) || 0;

  // Quick preset merchants
  const presets = [
    { name: "Highlands Coffee", bank: "Vietcombank", bin: "970436", acc: "1028394857", amount: "45000", content: "Thanh toan ca phe" },
    { name: "Cơm Tấm Phúc Lộc Thọ", bank: "MBBank", bin: "970422", acc: "0988776655", amount: "65000", content: "Com trua van phong" },
    { name: "WinMart Vincom", bank: "Techcombank", bin: "970407", acc: "19033445566", amount: "185000", content: "Sieu thi thuc pham" },
    { name: "Nhà thuốc An Khang", bank: "ACB", bin: "970416", acc: "20495867", amount: "120000", content: "Mua thuoc y te" },
  ];

  const handleSelectPreset = (p: typeof presets[0]) => {
    setReceiverName(p.name);
    setReceiverBank(p.bank);
    setReceiverBin(p.bin);
    setReceiverAccount(p.acc);
    setAmountStr(p.amount);
    setContent(p.content);
    setStep("enter_amount");
  };

  const handleConfirmTransfer = () => {
    if (!selectedAccount) {
      alert("Vui lòng chọn ngân hàng thanh toán!");
      return;
    }
    if (selectedAccount.balance < numAmount) {
      if (!window.confirm("Số dư tài khoản này nhỏ hơn số tiền chuyển. Bạn vẫn muốn tiếp tục trừ tiền?")) {
        return;
      }
    }

    // Deduct & Record transaction
    onSuccess({
      name: `Chuyển tiền: ${receiverName}`,
      cat: "Ăn uống",
      amount: -numAmount,
      date: "Hôm nay, " + new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      rawDate: new Date().toISOString(),
      bankId: selectedAccount.id,
      bankName: selectedAccount.bankName,
      note: content,
    });

    // Deep link to banking app
    const bankInfo = VIETNAM_BANKS.find((b) => b.shortName.toLowerCase() === selectedAccount.bankName.toLowerCase());
    const deepLink = bankInfo?.scheme || "vcb://";

    try {
      window.open(deepLink, "_blank");
    } catch {
      // fallback
    }

    setStep("complete");
  };

  return (
    <div className="modal-backdrop-full" onClick={onClose}>
      <div className="qr-sheet-card" onClick={(e) => e.stopPropagation()}>
        {/* Step 1: Scan QR */}
        {step === "scan" && (
          <div className="sheet-body">
            <div className="sheet-top-header">
              <h3>Quét mã QR thanh toán</h3>
              <button className="sheet-close" onClick={onClose}>✕</button>
            </div>

            <div className="qr-scanner-viewfinder">
              <div className="viewfinder-frame">
                <span className="corner tl" />
                <span className="corner tr" />
                <span className="corner bl" />
                <span className="corner br" />
                <div className="scanner-laser-line" />
              </div>
              <p className="scanner-hint">Hướng camera về phía mã VietQR để thanh toán</p>
            </div>

            <div className="quick-presets-section">
              <span className="preset-label">HOẶC CHỌN MẪU HÓA ĐƠN ĐỂ TEST:</span>
              <div className="presets-list">
                {presets.map((p) => (
                  <button key={p.name} className="preset-item-btn" onClick={() => handleSelectPreset(p)}>
                    <div>
                      <b>{p.name}</b>
                      <small>{p.bank} · {p.acc}</small>
                    </div>
                    <strong>{formatMoney(Number(p.amount))}</strong>
                  </button>
                ))}
              </div>
            </div>

            <button
              className="sheet-action-btn secondary"
              onClick={() => {
                setReceiverName("NGƯỜI NHẬN MẪU");
                setStep("enter_amount");
              }}
            >
              Nhập thông tin người nhận thủ công
            </button>
          </div>
        )}

        {/* Step 2: Nhập số tiền chuyển */}
        {step === "enter_amount" && (
          <div className="sheet-body">
            <div className="sheet-top-header">
              <button className="sheet-back-btn" onClick={() => setStep("scan")}>←</button>
              <h3>Nhập số tiền chuyển</h3>
              <button className="sheet-close" onClick={onClose}>✕</button>
            </div>

            <div className="receiver-preview-box">
              <div className="receiver-avatar">
                {receiverName.slice(0, 2).toUpperCase()}
              </div>
              <div className="receiver-details">
                <b>{receiverName}</b>
                <small>{receiverBank} · STK: {receiverAccount}</small>
              </div>
            </div>

            <div className="transfer-amount-field">
              <label>SỐ TIỀN THANH TOÁN</label>
              <div className="amount-input-wrap">
                <input
                  type="text"
                  inputMode="numeric"
                  autoFocus
                  placeholder="0"
                  value={amountStr}
                  onChange={(e) => setAmountStr(e.target.value.replace(/[^0-9]/g, ""))}
                />
                <span className="currency-unit">đ</span>
              </div>
            </div>

            <div className="amount-pills-row">
              {["20000", "50000", "100000", "200000", "500000"].map((amt) => (
                <button
                  key={amt}
                  className="amt-pill-btn"
                  onClick={() => setAmountStr(amt)}
                >
                  +{formatMoney(Number(amt))}
                </button>
              ))}
            </div>

            <div className="input-group">
              <label>Lời nhắn chuyển tiền</label>
              <input
                type="text"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="Ví dụ: Thanh toan tien nuoc"
              />
            </div>

            <button
              className="sheet-action-btn primary"
              disabled={numAmount <= 0}
              onClick={() => setStep("choose_bank")}
            >
              Tiếp tục chọn ngân hàng chuyển tiền ({formatMoney(numAmount)})
            </button>
          </div>
        )}

        {/* Step 3: Chọn ngân hàng để trừ tiền kèm số dư */}
        {step === "choose_bank" && (
          <div className="sheet-body">
            <div className="sheet-top-header">
              <button className="sheet-back-btn" onClick={() => setStep("enter_amount")}>←</button>
              <h3>Chọn ngân hàng thanh toán</h3>
              <button className="sheet-close" onClick={onClose}>✕</button>
            </div>

            <div className="payment-summary-bar">
              <span>Số tiền cần chuyển:</span>
              <b>{formatMoney(numAmount)}</b>
            </div>

            <p className="section-note">
              Chọn tài khoản bạn muốn trừ tiền trong PiggyBite và mở ứng dụng ngân hàng:
            </p>

            <div className="accounts-selection-list">
              {accounts.map((acc) => {
                const isSelected = selectedAccount?.id === acc.id;
                const isLowBalance = acc.balance < numAmount;

                return (
                  <div
                    key={acc.id}
                    className={`account-select-card ${isSelected ? "selected" : ""}`}
                    onClick={() => setSelectedAccount(acc)}
                  >
                    <div className="acc-sel-left">
                      <span className="acc-bank-badge" style={{ background: acc.color }}>
                        {acc.bankCode}
                      </span>
                      <div>
                        <b>{acc.bankName}</b>
                        <small>{acc.accountName} {acc.accountNumber ? `· *${acc.accountNumber.slice(-4)}` : ""}</small>
                      </div>
                    </div>

                    <div className="acc-sel-right">
                      <b className="acc-balance-display">{formatMoney(acc.balance)}</b>
                      {isLowBalance && <span className="low-bal-warning">Số dư thấp</span>}
                    </div>
                  </div>
                );
              })}
            </div>

            <button
              className="sheet-action-btn primary"
              onClick={handleConfirmTransfer}
            >
              Xác nhận trừ tiền & Mở app {selectedAccount?.bankName || "Ngân hàng"}
            </button>
          </div>
        )}

        {/* Step 4: Completion */}
        {step === "complete" && (
          <div className="sheet-body complete-state">
            <div className="success-icon-badge">✓</div>
            <h2>Đã trừ tiền thành công!</h2>
            <p>
              Đã ghi nhận trừ <b>{formatMoney(numAmount)}</b> từ tài khoản <b>{selectedAccount?.bankName}</b> trên sổ PiggyBite.
            </p>

            <div className="open-bank-guide-box">
              <p>Ứng dụng ngân hàng <b>{selectedAccount?.bankName}</b> đang được khởi chạy để bạn hoàn tất xác thực sinh trắc học / OTP.</p>
            </div>

            <div className="complete-actions">
              <button
                className="sheet-action-btn primary"
                onClick={() => {
                  const bankInfo = VIETNAM_BANKS.find(
                    (b) => b.shortName.toLowerCase() === selectedAccount?.bankName.toLowerCase()
                  );
                  window.open(bankInfo?.scheme || "vcb://", "_blank");
                }}
              >
                Mở lại app {selectedAccount?.bankName}
              </button>
              <button className="sheet-action-btn secondary" onClick={onClose}>
                Quay về trang chủ
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// ==========================================
// 3. MANUAL TRANSACTION ADD MODAL
// ==========================================
function ManualAddModal({
  accounts,
  onClose,
  onAdd,
}: {
  accounts: AccountItem[];
  onClose: () => void;
  onAdd: (t: Omit<TransactionItem, "id">) => void;
}) {
  const [kind, setKind] = useState<"expense" | "income">("expense");
  const [name, setName] = useState<string>("");
  const [amountStr, setAmountStr] = useState<string>("");
  const [category, setCategory] = useState<string>("Ăn uống");
  const [selectedBankId, setSelectedBankId] = useState<string>(accounts[0]?.id || "");

  const categories = ["Ăn uống", "Mua sắm", "Di chuyển", "Nhà cửa", "Giải trí", "Sức khỏe", "Lương", "Khác"];

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const rawVal = parseFloat(amountStr.replace(/[^0-9]/g, "")) || 0;
    if (rawVal <= 0) {
      alert("Vui lòng nhập số tiền hợp lệ!");
      return;
    }

    const selectedAcc = accounts.find((a) => a.id === selectedBankId) || accounts[0];

    onAdd({
      name: name || (kind === "expense" ? "Khoản chi mới" : "Khoản thu mới"),
      cat: category,
      amount: kind === "expense" ? -rawVal : rawVal,
      date: "Hôm nay, " + new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      rawDate: new Date().toISOString(),
      bankId: selectedAcc.id,
      bankName: selectedAcc.bankName,
    });
  };

  return (
    <div className="modal-backdrop-full" onClick={onClose}>
      <div className="qr-sheet-card" onClick={(e) => e.stopPropagation()}>
        <div className="sheet-body">
          <div className="sheet-top-header">
            <h3>Ghi chép giao dịch thủ công</h3>
            <button className="sheet-close" onClick={onClose}>✕</button>
          </div>

          <div className="kind-switch-bar">
            <button
              className={kind === "expense" ? "active expense" : ""}
              onClick={() => setKind("expense")}
            >
              Khoản chi
            </button>
            <button
              className={kind === "income" ? "active income" : ""}
              onClick={() => setKind("income")}
            >
              Khoản thu
            </button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="transfer-amount-field">
              <label>SỐ TIỀN</label>
              <div className="amount-input-wrap">
                <input
                  type="text"
                  inputMode="numeric"
                  autoFocus
                  placeholder="0"
                  value={amountStr}
                  onChange={(e) => setAmountStr(e.target.value.replace(/[^0-9]/g, ""))}
                />
                <span className="currency-unit">đ</span>
              </div>
            </div>

            <div className="input-group">
              <label>Tên khoản thu chi</label>
              <input
                type="text"
                placeholder="Ví dụ: Cà phê, Ăn trưa, Đổ xăng..."
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>

            <div className="input-group">
              <label>Chọn ví / Ngân hàng thanh toán</label>
              <select
                value={selectedBankId}
                onChange={(e) => setSelectedBankId(e.target.value)}
                className="select-field"
              >
                {accounts.map((acc) => (
                  <option key={acc.id} value={acc.id}>
                    {acc.bankName} - Số dư: {formatMoney(acc.balance)}
                  </option>
                ))}
              </select>
            </div>

            <div className="input-group">
              <label>Hạng mục</label>
              <div className="categories-pill-grid">
                {categories.map((c) => (
                  <button
                    type="button"
                    key={c}
                    className={`cat-pill ${category === c ? "active" : ""}`}
                    onClick={() => setCategory(c)}
                  >
                    {c}
                  </button>
                ))}
              </div>
            </div>

            <button type="submit" className="sheet-action-btn primary" style={{ marginTop: "20px" }}>
              Lưu vào sổ PiggyBite
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

// ==========================================
// 4. MY QR CODE MODAL
// ==========================================
function MyQRModal({ accounts, onClose }: { accounts: AccountItem[]; onClose: () => void }) {
  const [selectedAccId, setSelectedAccId] = useState<string>(accounts[0]?.id || "");
  const selectedAcc = accounts.find((a) => a.id === selectedAccId) || accounts[0];

  const bankInfo = VIETNAM_BANKS.find((b) => b.shortName.toLowerCase() === selectedAcc?.bankName.toLowerCase());
  const bin = bankInfo?.bin || "970436";
  const accNo = selectedAcc?.accountNumber || "1028394857";

  const vietQrUrl = `https://img.vietqr.io/image/${bin}-${accNo}-compact2.png?accountName=${encodeURIComponent(selectedAcc?.accountName || "NGUYEN VAN A")}`;

  return (
    <div className="modal-backdrop-full" onClick={onClose}>
      <div className="qr-sheet-card" onClick={(e) => e.stopPropagation()}>
        <div className="sheet-body text-center">
          <div className="sheet-top-header">
            <h3>Mã QR nhận tiền của tôi</h3>
            <button className="sheet-close" onClick={onClose}>✕</button>
          </div>

          <div className="input-group" style={{ textAlign: "left" }}>
            <label>Chọn tài khoản nhận tiền:</label>
            <select
              value={selectedAccId}
              onChange={(e) => setSelectedAccId(e.target.value)}
              className="select-field"
            >
              {accounts.map((acc) => (
                <option key={acc.id} value={acc.id}>
                  {acc.bankName} - {acc.accountNumber || "Ví điện tử"}
                </option>
              ))}
            </select>
          </div>

          <div className="my-qr-box">
            <img src={vietQrUrl} alt="VietQR" className="qr-image-display" />
            <b className="qr-owner-name">{selectedAcc?.accountName}</b>
            <span className="qr-bank-details">{selectedAcc?.bankName} · {accNo}</span>
          </div>

          <p className="qr-note-sub">Mã VietQR chuẩn NAPAS 247 nhận tiền tức thì từ mọi ngân hàng.</p>
        </div>
      </div>
    </div>
  );
}

// ==========================================
// 5. EXPENSE ANALYSIS & SUMMARY VIEW
// ==========================================
function AnalysisView({
  accounts,
  transactions,
  totalBalance,
  monthExpense,
  todayExpense,
  onClose,
  isTab = false,
}: {
  accounts: AccountItem[];
  transactions: TransactionItem[];
  totalBalance: number;
  monthExpense: number;
  todayExpense: number;
  onClose: () => void;
  isTab?: boolean;
}) {
  const categoryMap: Record<string, number> = {};
  transactions
    .filter((t) => t.amount < 0)
    .forEach((t) => {
      categoryMap[t.cat] = (categoryMap[t.cat] || 0) + Math.abs(t.amount);
    });

  const totalSpent = Object.values(categoryMap).reduce((a, b) => a + b, 0) || 1;

  const bankSpentMap: Record<string, number> = {};
  transactions
    .filter((t) => t.amount < 0)
    .forEach((t) => {
      bankSpentMap[t.bankName] = (bankSpentMap[t.bankName] || 0) + Math.abs(t.amount);
    });

  const content = (
    <div className="analysis-page-content">
      {!isTab && (
        <div className="sheet-top-header" style={{ marginBottom: "16px" }}>
          <h3>Tổng hợp & Phân tích chi tiêu</h3>
          <button className="sheet-close" onClick={onClose}>✕</button>
        </div>
      )}

      {isTab && <h1 className="screen-main-title">Phân tích tài chính</h1>}

      <div className="metrics-cards-row">
        <div className="metric-box">
          <span>Chi tiêu hôm nay</span>
          <b>{formatMoney(todayExpense)}</b>
        </div>
        <div className="metric-box">
          <span>Chi tiêu tháng này</span>
          <b>{formatMoney(monthExpense)}</b>
        </div>
        <div className="metric-box">
          <span>Tổng số dư ví</span>
          <b>{formatMoney(totalBalance)}</b>
        </div>
      </div>

      <div className="analysis-card-section">
        <h4>Chi tiêu theo hạng mục</h4>
        <div className="breakdown-list">
          {Object.entries(categoryMap).map(([cat, amt]) => {
            const pct = Math.round((amt / totalSpent) * 100);
            return (
              <div key={cat} className="breakdown-item">
                <div className="breakdown-label-row">
                  <span>{cat}</span>
                  <b>{formatMoney(amt)} ({pct}%)</b>
                </div>
                <div className="breakdown-track">
                  <div className="breakdown-fill" style={{ width: `${pct}%`, background: "#1b4332" }} />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <div className="analysis-card-section">
        <h4>Chi tiêu qua từng tài khoản ngân hàng</h4>
        <div className="breakdown-list">
          {Object.entries(bankSpentMap).map(([bank, amt]) => {
            const pct = Math.round((amt / totalSpent) * 100);
            return (
              <div key={bank} className="breakdown-item">
                <div className="breakdown-label-row">
                  <span>{bank}</span>
                  <b>{formatMoney(amt)} ({pct}%)</b>
                </div>
                <div className="breakdown-track">
                  <div className="breakdown-fill" style={{ width: `${pct}%`, background: "#486e58" }} />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <div className="ai-advice-card">
        <div className="ai-advice-header">
          <span>💡</span>
          <b>Nhận xét & Lời khuyên tài chính</b>
        </div>
        <p>
          Hạng mục ăn uống và mua sắm chiếm phần lớn chi tiêu của bạn trong tháng. Bạn đang giữ tỷ lệ dự phòng tốt với tổng số dư {formatMoney(totalBalance)}. Hãy duy trì thói quen quét QR và nhận biến động số dư tự động để quản lý tài chính chuẩn xác nhất!
        </p>
      </div>
    </div>
  );

  if (isTab) {
    return <div className="tab-screen-wrap">{content}</div>;
  }

  return (
    <div className="modal-backdrop-full" onClick={onClose}>
      <div className="qr-sheet-card" onClick={(e) => e.stopPropagation()}>
        {content}
      </div>
    </div>
  );
}

// ==========================================
// 6. ACCOUNTS & WALLET MANAGER
// ==========================================
function AccountsManagerScreen({
  accounts,
  totalBalance,
  onAddAccount,
  onUpdateAccount,
  onDeleteAccount,
}: {
  accounts: AccountItem[];
  totalBalance: number;
  onAddAccount: () => void;
  onUpdateAccount: (a: AccountItem) => void;
  onDeleteAccount: (id: string) => void;
}) {
  const [editingAcc, setEditingAcc] = useState<AccountItem | null>(null);
  const [editBalance, setEditBalance] = useState<string>("");

  const handleStartEdit = (acc: AccountItem) => {
    setEditingAcc(acc);
    setEditBalance(acc.balance.toString());
  };

  const handleSaveEdit = (e: FormEvent) => {
    e.preventDefault();
    if (!editingAcc) return;
    const newBal = parseFloat(editBalance.replace(/[^0-9]/g, "")) || 0;
    onUpdateAccount({
      ...editingAcc,
      balance: newBal,
    });
    setEditingAcc(null);
  };

  return (
    <div className="accounts-screen-wrap">
      <div className="screen-header-row">
        <div>
          <span className="screen-eyebrow">QUẢN LÝ VÍ & NGÂN HÀNG</span>
          <h1 className="screen-main-title">Tài khoản thanh toán</h1>
        </div>
        <button className="add-bank-btn" onClick={onAddAccount}>
          + Thêm ngân hàng
        </button>
      </div>

      <div className="total-wallet-banner">
        <span>Tổng hợp toàn bộ số tiền</span>
        <h2>{formatMoneyFull(totalBalance)}</h2>
        <small>{accounts.length} tài khoản & ví đang liên kết</small>
      </div>

      <div className="accounts-full-list">
        {accounts.map((acc) => (
          <div key={acc.id} className="account-row-card">
            <div className="acc-row-main">
              <span className="acc-bank-badge-lg" style={{ background: acc.color }}>
                {acc.bankCode}
              </span>
              <div className="acc-info-block">
                <b>{acc.bankName}</b>
                <span className="acc-name-text">{acc.accountName}</span>
                {acc.accountNumber && <small className="acc-num-text">STK: {acc.accountNumber}</small>}
              </div>
            </div>

            <div className="acc-balance-actions">
              <b className="acc-amount-val">{formatMoneyFull(acc.balance)}</b>
              <div className="acc-btn-group">
                <button className="edit-acc-btn" onClick={() => handleStartEdit(acc)}>
                  Sửa số dư
                </button>
                <button
                  className="del-acc-btn"
                  onClick={() => {
                    if (window.confirm(`Xóa tài khoản ${acc.bankName}?`)) {
                      onDeleteAccount(acc.id);
                    }
                  }}
                >
                  Xóa
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {editingAcc && (
        <div className="modal-backdrop-full" onClick={() => setEditingAcc(null)}>
          <div className="qr-sheet-card" onClick={(e) => e.stopPropagation()}>
            <div className="sheet-body">
              <div className="sheet-top-header">
                <h3>Cập nhật số dư {editingAcc.bankName}</h3>
                <button className="sheet-close" onClick={() => setEditingAcc(null)}>✕</button>
              </div>

              <form onSubmit={handleSaveEdit}>
                <div className="transfer-amount-field">
                  <label>SỐ DƯ HIỆN TẠI TRONG NGÂN HÀNG</label>
                  <div className="amount-input-wrap">
                    <input
                      type="text"
                      inputMode="numeric"
                      autoFocus
                      value={editBalance}
                      onChange={(e) => setEditBalance(e.target.value.replace(/[^0-9]/g, ""))}
                    />
                    <span className="currency-unit">đ</span>
                  </div>
                </div>

                <button type="submit" className="sheet-action-btn primary" style={{ marginTop: "20px" }}>
                  Lưu số dư mới
                </button>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

// ==========================================
// 7. ADD BANK ACCOUNT MODAL (36 VN BANKS)
// ==========================================
function AddAccountModal({
  onClose,
  onSave,
}: {
  onClose: () => void;
  onSave: (acc: AccountItem) => void;
}) {
  const [search, setSearch] = useState("");
  const [selectedBank, setSelectedBank] = useState<BankInfo>(VIETNAM_BANKS[0]);
  const [accountName, setAccountName] = useState("Tài khoản thanh toán");
  const [accountNumber, setAccountNumber] = useState("");
  const [balanceStr, setBalanceStr] = useState("1000000");

  const filteredBanks = VIETNAM_BANKS.filter(
    (b) =>
      b.shortName.toLowerCase().includes(search.toLowerCase()) ||
      b.name.toLowerCase().includes(search.toLowerCase()) ||
      b.code.toLowerCase().includes(search.toLowerCase())
  );

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    const balance = parseFloat(balanceStr.replace(/[^0-9]/g, "")) || 0;

    onSave({
      id: "acc-" + Date.now(),
      bankCode: selectedBank.code,
      bankName: selectedBank.shortName,
      accountName: accountName || selectedBank.shortName,
      accountNumber: accountNumber,
      balance: balance,
      color: selectedBank.color,
    });
  };

  return (
    <div className="modal-backdrop-full" onClick={onClose}>
      <div className="qr-sheet-card" onClick={(e) => e.stopPropagation()}>
        <div className="sheet-body">
          <div className="sheet-top-header">
            <h3>Thêm tài khoản ngân hàng / Ví</h3>
            <button className="sheet-close" onClick={onClose}>✕</button>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <label>Tìm kiếm & Chọn ngân hàng Việt Nam</label>
              <input
                type="text"
                placeholder="Nhập tên ngân hàng (VCB, MoMo, MB, Techcombank...)"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="search-bank-input"
              />
              <div className="banks-dropdown-list">
                {filteredBanks.slice(0, 10).map((b) => (
                  <button
                    type="button"
                    key={b.code}
                    className={`bank-pick-item ${selectedBank.code === b.code ? "active" : ""}`}
                    onClick={() => {
                      setSelectedBank(b);
                      setAccountName(`Tài khoản ${b.shortName}`);
                    }}
                  >
                    <span className="bank-pick-badge" style={{ background: b.color }}>
                      {b.code}
                    </span>
                    <div className="bank-pick-text">
                      <b>{b.shortName}</b>
                      <small>{b.name}</small>
                    </div>
                  </button>
                ))}
              </div>
            </div>

            <div className="transfer-amount-field">
              <label>SỐ DƯ HIỆN TẠI (NHẬP ĐỂ TỔNG HỢP TOÀN BỘ SỐ TIỀN)</label>
              <div className="amount-input-wrap">
                <input
                  type="text"
                  inputMode="numeric"
                  value={balanceStr}
                  onChange={(e) => setBalanceStr(e.target.value.replace(/[^0-9]/g, ""))}
                  placeholder="0"
                  required
                />
                <span className="currency-unit">đ</span>
              </div>
            </div>

            <div className="input-group">
              <label>Tên gợi nhớ tài khoản</label>
              <input
                type="text"
                value={accountName}
                onChange={(e) => setAccountName(e.target.value)}
                placeholder="Ví dụ: Tài khoản nhận lương, Ví ăn uống..."
                required
              />
            </div>

            <div className="input-group">
              <label>Số tài khoản / Số thẻ (Tùy chọn)</label>
              <input
                type="text"
                value={accountNumber}
                onChange={(e) => setAccountNumber(e.target.value)}
                placeholder="Ví dụ: 1029384756"
              />
            </div>

            <button type="submit" className="sheet-action-btn primary" style={{ marginTop: "20px" }}>
              Xác nhận thêm vào ví
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

// ==========================================
// 8. TRANSACTION HISTORY SCREEN
// ==========================================
function HistoryScreen({
  transactions,
  accounts,
  onOpenManualAdd,
}: {
  transactions: TransactionItem[];
  accounts: AccountItem[];
  onOpenManualAdd: () => void;
}) {
  return (
    <div className="history-screen-wrap">
      <div className="screen-header-row">
        <div>
          <span className="screen-eyebrow">SỔ GIAO DỊCH</span>
          <h1 className="screen-main-title">Lịch sử thu chi</h1>
        </div>
        <button className="add-bank-btn" onClick={onOpenManualAdd}>
          + Ghi giao dịch
        </button>
      </div>

      <div className="history-list-card">
        {transactions.length === 0 ? (
          <div className="empty-state-box">
            <h3 className="empty-state-title">Chưa có giao dịch</h3>
            <p className="empty-state-sub">Mọi khoản chi tiêu qua QR hoặc nhận thông báo ngân hàng sẽ xuất hiện tại đây.</p>
          </div>
        ) : (
          transactions.map((t) => (
            <div key={t.id} className="tx-item-row">
              <div className="tx-item-left">
                <div className={`tx-cat-badge ${t.amount < 0 ? "expense" : "income"}`}>
                  {t.amount < 0 ? "−" : "+"}
                </div>
                <div>
                  <b className="tx-name">{t.name}</b>
                  <small className="tx-meta">{t.bankName} · {t.cat} · {t.date}</small>
                </div>
              </div>
              <div className={`tx-amount ${t.amount < 0 ? "expense" : "income"}`}>
                {t.amount < 0 ? "−" : "+"}{formatMoney(Math.abs(t.amount))}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

// ==========================================
// 9. BANK NOTIFICATION LISTENER & SIMULATOR
// ==========================================
function NotificationSimulatorModal({
  onClose,
  onProcess,
}: {
  onClose: () => void;
  onProcess: (text: string) => void;
}) {
  const [customText, setCustomText] = useState("");

  const sampleNotifications = [
    {
      bank: "Vietcombank",
      text: "VCB: 27/10/26 10:15 | TK 1028394857 | +500,000 VND | Ref: NGUYEN VAN A chuyen tien mua hang | So du: 15,000,000 VND",
    },
    {
      bank: "Ví MoMo",
      text: "MoMo: Bạn vừa nhận được 150.000đ từ Trần Thị Mai với lời nhắn 'Tien ca phe an trua'. Số dư ví: 1.000.000đ",
    },
    {
      bank: "Techcombank",
      text: "Techcombank: +2,500,000 VND vao TK 19038475869 luc 09:30. ND: Cong ty ABC thanh toan tien thuong thang 10.",
    },
    {
      bank: "MBBank",
      text: "MB Bank: TK 0988776655 +1,200,000VND luc 14:20. ND: Le Van B chuyen khoan tien phong",
    },
    {
      bank: "Agribank",
      text: "Agribank: TK 970405xxx +800,000VND ngay 27/10/2026. ND: Tra tien vay ban be",
    },
  ];

  return (
    <div className="modal-backdrop-full" onClick={onClose}>
      <div className="qr-sheet-card" onClick={(e) => e.stopPropagation()}>
        <div className="sheet-body">
          <div className="sheet-top-header">
            <div>
              <h3>Tự động nhận thông báo ngân hàng</h3>
              <small style={{ color: "var(--text-muted)", fontSize: "12px" }}>
                Tự động nhận diện và cộng tiền vào ngân hàng tương ứng
              </small>
            </div>
            <button className="sheet-close" onClick={onClose}>✕</button>
          </div>

          <div className="notify-status-box">
            <span className="notify-pulse-dot" />
            <div>
              <b>Trình lắng nghe thông báo: Đang sẵn sàng</b>
              <p>Mỗi khi điện thoại nhận SMS hoặc thông báo từ app ngân hàng, PiggyBite sẽ tự động cộng/trừ tiền.</p>
            </div>
          </div>

          <div className="quick-presets-section" style={{ marginTop: "16px" }}>
            <span className="preset-label">MẪU THÔNG BÁO CHUYỂN TIỀN THỰC TẾ (NHẤP ĐỂ THỬ):</span>
            <div className="presets-list">
              {sampleNotifications.map((s) => (
                <button
                  key={s.bank}
                  className="preset-item-btn notify-sample-btn"
                  onClick={() => onProcess(s.text)}
                >
                  <div>
                    <b>{s.bank}</b>
                    <small style={{ fontSize: "11px", color: "var(--text-muted)" }}>{s.text}</small>
                  </div>
                  <span className="test-now-badge">Thử nhận</span>
                </button>
              ))}
            </div>
          </div>

          <div className="input-group" style={{ marginTop: "16px" }}>
            <label>Hoặc dán nội dung thông báo / tin nhắn SMS ngân hàng của bạn:</label>
            <textarea
              className="notify-textarea"
              rows={3}
              placeholder="Dán thông báo biến động số dư ngân hàng vào đây..."
              value={customText}
              onChange={(e) => setCustomText(e.target.value)}
            />
            <button
              className="sheet-action-btn primary"
              style={{ marginTop: "10px" }}
              disabled={!customText.trim()}
              onClick={() => onProcess(customText)}
            >
              Tự động xử lý & Cập nhật vào ngân hàng
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

// ==========================================
// 10. SETTINGS SCREEN
// ==========================================
function SettingsScreen({
  accounts,
  totalBalance,
  transactionsCount,
  onOpenNotificationSimulator,
  onResetData,
}: {
  accounts: AccountItem[];
  totalBalance: number;
  transactionsCount: number;
  onOpenNotificationSimulator: () => void;
  onResetData: () => void;
}) {
  return (
    <div className="settings-screen-wrap">
      <h1 className="screen-main-title">Cài đặt ứng dụng</h1>

      <div className="settings-card-box">
        <div className="setting-row" onClick={onOpenNotificationSimulator} style={{ cursor: "pointer" }}>
          <div>
            <b>Tự động cập nhật từ thông báo</b>
            <small>Tự động nhận diện tin nhắn SMS & thông báo ngân hàng</small>
          </div>
          <span className="status-badge-active">Đang bật</span>
        </div>

        <div className="setting-row">
          <div>
            <b>Chế độ hoạt động</b>
            <small>Hoàn toàn Offline, dữ liệu lưu cục bộ trên thiết bị của bạn</small>
          </div>
          <span className="status-badge-active">Offline</span>
        </div>

        <div className="setting-row">
          <div>
            <b>Tổng số tài khoản ngân hàng</b>
            <small>{accounts.length} tài khoản liên kết</small>
          </div>
          <b>{formatMoneyFull(totalBalance)}</b>
        </div>

        <div className="setting-row">
          <div>
            <b>Số lượng giao dịch đã ghi</b>
            <small>Được lưu an toàn trong bộ nhớ máy</small>
          </div>
          <b>{transactionsCount} khoản</b>
        </div>
      </div>

      <div className="settings-card-box" style={{ marginTop: "16px" }}>
        <a
          href="https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk"
          download="PiggyBite.apk"
          className="setting-row"
          style={{ textDecoration: "none", color: "inherit", cursor: "pointer" }}
        >
          <div>
            <b>Tải tệp cài đặt PiggyBite APK</b>
            <small>Cài đặt trực tiếp bản Native Jetpack Compose cho Android (21 MB)</small>
          </div>
          <span className="apk-download-pill">Tải APK ↓</span>
        </a>
      </div>

      <div className="settings-danger-box">
        <button className="reset-data-btn" onClick={onResetData}>
          Đặt lại dữ liệu mẫu ban đầu
        </button>
      </div>

      <p className="app-version-tag">PiggyBite Minimalist Edition v2.0</p>
    </div>
  );
}
