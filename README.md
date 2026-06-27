# 🐷 PiggyBite - Premium Finance Manager

![PiggyBite Banner](https://img.shields.io/badge/PiggyBite-Indigo%20Premium-4A5BCC?style=for-the-badge)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)

**PiggyBite** is a modern, premium finance management application designed for high-efficiency asset tracking. Featuring a sleek **Indigo-themed** interface and advanced data-driven features, PiggyBite transforms how you manage your money.

---

## ✨ Key Features

### 🧠 Smart AI Integration
*   **Gemini 3.5 Flash Chat**: A sophisticated financial assistant that offers personalized advice, spending analysis, and even automated goal planning (e.g., "Plan a trip to Vung Tau").
*   **Neural OCR Scanner**: Capture bills or import gallery images to automatically extract amounts and categorize spending in real-time.
*   **Instant Categorization**: Local AI engine that learns your habits and suggests categories based on keywords.

### 📊 Advanced Analytics
*   **Animated Donut Charts**: Visualize spending vs. income with smooth, clockwise animations.
*   **Weekly Flow Charts**: Track your financial trends day-by-day with interactive tooltips.
*   **Tabbed Statistics**: Switch seamlessly between Weekly, Monthly, and Yearly views.

### 🏦 Fund & Goal Management
*   **Dynamic Funds**: Create saving goals with animated progress bars.
*   **Pinning System**: Use the "Heart" icon to pin your most important goals to the top.
*   **Direct Wallet Integration**: Depositing into a fund automatically records an expense, and withdrawing reflects as income.

### 📁 Data Management
*   **CSV Backup & Restore**: Export your entire financial history to a standard CSV format (`date,type,amount,category,note`) or import data from external spreadsheets.

---

## 🎨 Design Language
*   **Primary Theme**: Premium Indigo (#4A5BCC) with a Liquid Glass footer navigation.
*   **Minimalist Interface**: Focused on white-space and clean typography for a clutter-free experience.
*   **Privacy First**: "Eye" toggle in the header to hide sensitive balance information.

---

## 🛠 Tech Stack
*   **Language**: Kotlin
*   **Database**: Room v7 (SQL Persistence)
*   **UI Framework**: Jetpack (ConstraintLayout, Material 3, Fragments)
*   **AI OCR Engine**: ML Kit Text Recognition (OCR)
*   **Camera**: CameraX (Professional lifecycle-aware control)

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Iguana or newer.
*   Min SDK: 21 (Android 5.0).

### Installation
1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/PiggyBite.git
    ```
2.  Open the project in Android Studio.
3.  Sync Gradle and run the `:app` module on your device.

---

## 📂 CSV Format for Import
To import data correctly, ensure your CSV follows this header and structure:
```csv
date,type,amount,category,note
2026-06-21,expense,50000,Ăn uống,Phở sáng
2026-06-21,income,15000000,Lương,Lương tháng 6
```

---

## 📜 License
Copyright © 2024 PiggyBite Team. Distributed under the MIT License.
