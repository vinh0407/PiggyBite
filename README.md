# 🐷 PiggyBite - Smart Personal Finance Manager

![PiggyBite Banner](https://img.shields.io/badge/PiggyBite-Indigo%20Premium-4A5BCC?style=for-the-badge)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Realtime%20DB-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

**PiggyBite** is a high-performance personal finance management application for Android. Designed with a premium **Indigo theme**, it provides users with powerful tools to track spending, manage shared savings goals, and visualize financial health with real-time cloud synchronization.

---

## 🚀 Core Features

### 🏦 Advanced Fund & Goal Management
*   **Shared Saving Funds**: Create individual or group saving goals with animated progress tracking.
*   **Member Contributions**: Invite family or friends via email/phone. Track exactly how much each member has contributed.
*   **Smart Dissolution & Refunds**: If a shared fund is disbanded or a member leaves, the system automatically calculates and refunds their contributions back to their main wallet balance.
*   **Pinning System**: Pin your priority goals to the top for quick access.

### 🔐 Secure Authentication & Profile
*   **Dual Login Methods**: Sign in seamlessly using either **Email/Password** or **Phone Number/Password**.
*   **Persistent Sessions**: Automatic login redirects you straight to the main dashboard on app restart.
*   **Profile Management**: View and update your name, email, phone number, and password directly within the app.
*   **Balance Privacy**: Toggle balance visibility with a single tap to hide sensitive data in public.

### 📊 Real-time Data & Analytics
*   **Cloud Synchronization**: Powered by **Firebase Realtime Database** for instant sync across multiple devices.
*   **Visual Analytics**: 
    *   **Donut Charts**: Dynamic spending vs. income breakdowns by category.
    *   **Weekly Trends**: Interactive line charts showing your financial flow over the last 7 days.
*   **Transaction History**: Searchable and filterable history with detailed entry views.

### 🛠 Smart Input Tools
*   **Neural OCR Scanner**: Automatically extract amounts and categories from receipts using ML Kit Text Recognition.
*   **Voice Commands**: Add transactions hands-free using integrated Vietnamese voice recognition.
*   **CSV Import/Export**: Backup your data to standard CSV files or migrate from other spreadsheets.
*   **Integrated Map**: Quick access to an interactive financial services map via built-in WebView.

---

## 🎨 Design Language
*   **Primary Theme**: Premium Indigo (#4A5BCC) with a minimalist, clean UI.
*   **UX Focused**: Liquid Glass navigation bar and smooth transitions between fragments.
*   **Dark Mode Support**: Optimized for eye comfort in various lighting conditions.

---

## 🛠 Tech Stack
*   **Language**: Kotlin (Coroutines & Flow)
*   **Architecture**: MVVM with Clean Code principles.
*   **Database**: Room (Local) & Firebase Realtime Database (Cloud).
*   **AI OCR Engine**: Google ML Kit.
*   **Media**: CameraX for professional receipt capturing.
*   **UI Framework**: Material Design 3, ConstraintLayout, Fragments.

---

## 📂 Installation

### Prerequisites
*   Android Studio Jellyfish or newer.
*   Min SDK: 24 (Android 7.0).
*   A Firebase project with **Authentication** and **Realtime Database** enabled.

### Setup
1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/PiggyBite.git
    ```
2.  Add your `google-services.json` to the `/app` directory.
3.  Configure Firebase Realtime Database Rules:
    ```json
    {
      "rules": {
        "users": {
          "$uid": { ".write": "$uid === auth.uid", ".read": "auth != null" }
        },
        "funds": {
          "$fundId": {
            ".read": "auth != null && (!data.exists() || data.child('members').child(auth.uid).exists())",
            ".write": "auth != null && (!data.exists() || data.child('members').child(auth.uid).exists())"
          }
        }
      }
    }
    ```
4.  Sync Gradle and run the app.

---

## 📜 License
Copyright © 2024 PiggyBite Team. Distributed under the MIT License.
