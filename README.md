# 🐷 PiggyBite: The Future of Intelligent Personal Finance

![PiggyBite Premium Banner](https://img.shields.io/badge/PiggyBite-Indigo%20Premium-4A5BCC?style=for-the-badge&logo=appveyor)
![Android SDK](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Gemini 3.5 Flash](https://img.shields.io/badge/AI-Gemini%203.5%20Flash-blue?style=for-the-badge&logo=google-cloud&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20MVVM-orange?style=for-the-badge)

## 🌟 Overview
**PiggyBite** is not just another expense tracker. It is a high-performance, AI-driven financial ecosystem designed to bridge the gap between simple bookkeeping and professional wealth management. Built with a "Privacy-First" and "Intelligence-Led" philosophy, PiggyBite leverages Google's latest **Gemini 3.5 Flash** models to provide users with a level of insight previously reserved for private bankers.

Encapsulated in a stunning **Indigo Premium** design language, PiggyBite offers a seamless, animated, and highly intuitive user experience that makes managing money feel less like a chore and more like a strategy game for success.

---

## 🚀 The AI Revolution in Your Pocket

### 1. Gemini 3.5 Flash Integration (Advanced Logic)
Unlike traditional apps that provide static tips, PiggyBite features a fully context-aware AI Chatbot.
*   **Deep Contextual Analysis**: The AI doesn't just chat; it analyzes your entire transaction history, current balances, and fund progress before responding.
*   **Actionable Commands**: Users can issue natural language commands such as *"Plan a trip to Vung Tau"* or *"How can I save 5 million this month?"*. The AI calculates the necessary daily budget, creates a new Fund automatically (upon 'Yes' confirmation), and sets the target goals.
*   **Financial Therapy**: Feeling stressed about spending? PiggyBite AI acts as a mentor, offering humorous yet firm advice on cutting down non-essential expenses like high-frequency cafe visits.

### 2. Neural OCR Bill Scanning
*   **Real-time Edge Processing**: Using Google's ML Kit, PiggyBite processes bills locally on your device for maximum speed and security.
*   **Autonomous Categorization**: The scanner identifies keywords (e.g., "KFC", "Grab", "Pharmacy") and automatically maps them to the correct financial category.
*   **Smart Amount Extraction**: Our proprietary algorithm filters out tax IDs, phone numbers, and dates to find the true transaction total, pre-filling the entry form for one-tap saving.

### 3. Voice-to-Ledger (Vietnamese Natural Language)
*   **8-Second Fast Entry**: Designed for users on the go. Simply press the mic and say: *"Nay ăn bún bò hết năm chục ngàn"*.
*   **NLP Parser**: The system breaks down the sentence into `Amount: 50,000`, `Category: Dining`, and `Note: Bún bò` instantly.

---

## 🎨 Design Philosophy: The Indigo Identity

PiggyBite follows a strict **Premium Indigo (#4A5BCC)** aesthetic, characterized by:
*   **Liquid Glass Navigation**: A floating, translucent 3-tab navigation bar with directional slide animations. Switching from 'Calendar' to 'Home' feels organic, with the UI elements sliding in response to the user's focus.
*   **Directional Animations**: Fragments don't just "appear." They slide from left or right based on their relative position in the navigation hierarchy, maintaining the user's spatial awareness.
*   **The "Privacy Eye"**: Located in the header, users can instantly mask their sensitive total balance and chat statistics with a single tap, perfect for using the app in public spaces.

---

## 📊 Comprehensive Financial Analytics

### Donut Analytics (Category Distribution)
*   Animated, clockwise-drawing pie charts.
*   Dynamic color schemes: Indigo palettes for expenses and Vibrant Green for income.
*   Auto-generated legends with percentage breakdowns and total values.

### Weekly Flow (The Pulsar Line Chart)
*   A Mon-Sun comparative line chart showing the flow of money.
*   Interactive tooltips: Long-press any node to see the exact transaction volume for that day.
*   Visualizes your financial "momentum"—are you accelerating your savings or your spending?

---

## 🏦 Strategic Fund Management

PiggyBite treats saving as a primary objective through its **Premium Funds** module:
*   **Automated Accounting**: Depositing into a fund is treated as an internal "Expense" from the main wallet, while withdrawing is recorded as "Income." This ensures your total net worth is always balanced.
*   **Pinning System**: Use the heart icon to ghim (pin) critical goals (like "Emergency Fund" or "House Downpayment") to the top of the dashboard.
*   **Visual Progress**: High-fidelity progress bars with real-time percentage updates keep you motivated.

---

## 🛠 Technical Implementation & Stack

### Core Architecture
*   **Language**: 100% Kotlin with Coroutines for asynchronous database and AI operations.
*   **Database**: **Room Persistence Library (v6)**. Utilizing a complex schema for Transactions, Funds, and Chat History with automated migrations.
*   **Camera Engine**: **CameraX** with custom `SurfaceProvider` control, enabling a "Focused Mask View" where only the central frame is active, saving battery and improving focus accuracy.

### Security & Data Integrity
*   **Offline-First**: All transaction data is stored locally. PiggyBite only connects to the internet for AI processing and backup.
*   **CSV Lifecycle**:
    *   **Export**: Generates a standard `date,type,amount,category,note` CSV file.
    *   **Import**: Smart-parsing engine that handles date format conversions (YYYY-MM-DD to local display) and prevents duplicate entries.
    *   **Storage**: Direct integration with the Android Downloads folder using Scoped Storage APIs.

---

## 📂 CSV Structure Reference
For developers or users migrating data, PiggyBite follows the International Finance Exchange format:
| Column | Format | Example |
| :--- | :--- | :--- |
| **date** | YYYY-MM-DD | 2025-06-19 |
| **type** | expense / income | expense |
| **amount** | Numeric | 55000 |
| **category** | String | Ăn uống |
| **note** | String | Cơm gà xối mỡ |

---

## 🚀 Installation & Setup

1.  **Clone & Sync**:
    ```bash
    git clone https://github.com/yourprofile/PiggyBite.git
    ```
2.  **API Configuration**:
    *   Locate `AIChatActivity.kt`.
    *   Replace `GEMINI_API_KEY` with your Google AI Studio key.
3.  **Requirements**:
    *   Android Studio Iguana | 2023.2.1 or newer.
    *   Target SDK: 34.
    *   Minimum SDK: 21.

---

## 📈 Roadmap & Future Vision
- [ ] **Phase 1**: Predictive spending alerts using local machine learning.
- [ ] **Phase 2**: Multi-currency support with real-time exchange rates.
- [ ] **Phase 3**: Shared family wallets with cloud synchronization.

---

## ⚖️ License & Credits
Copyright © 2024 PiggyBite Development Team.
Licensed under the **MIT License**. Use of the Gemini API is subject to Google's Generative AI Terms of Service.

*PiggyBite: Empowering your wealth through the lens of Intelligence.*
