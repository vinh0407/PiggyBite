# PiggyBite

Smart Personal Finance and Wealth Manager for Android.

PiggyBite is a modern personal finance application built with native Android Jetpack Compose and Material Design 3. The application emphasizes complete data privacy, on-device intelligence, and native VietQR banking integration for users in Vietnam.

---

## Direct Downloads and Production Artifacts

Download and install the prebuilt Android package directly:
- [Download PiggyBite APK](https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk)
- [Download PiggyBite Google Play Bundle AAB](https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite-release.aab)
- [Privacy Policy](https://github.com/vinh0407/PiggyBite/blob/main/PRIVACY_POLICY.md)

Repository binary location:
- `APK/PiggyBite.apk` (Signed Release APK)
- `APK/PiggyBite-release.aab` (Signed Release Bundle for Google Play Store)
- Package Name: `com.piggybite.app`

---

## Overview

PiggyBite provides a streamlined, privacy-first interface for tracking income, expenses, and banking accounts. Unlike cloud-dependent finance tools, PiggyBite processes transactions and financial analysis locally on the user device without transmitting sensitive banking information to external servers.

The application starts with a clean slate (zero accounts and zero mock transactions) so users can configure their financial records from scratch according to their personal setup.

---

## Key Features

### VietQR Banking and Account Management
- Over 50 supported banks and digital wallets across Vietnam, including Vietcombank, MB Bank, Techcombank, ACB, VPBank, TPBank, BIDV, MoMo, ZaloPay, and ShopeePay.
- Official bank branding with offline badge fallback when network connectivity is unavailable.
- Permanent personal QR code photo storage: users can attach their personal receive-money QR code to any bank account, accessible offline at any time.
- Real-time balance aggregation across all configured accounts with visual breakdown percentages.

### On-Device Offline AI Classification
- Natural language classification engine for Vietnamese transaction descriptions, identifying categories such as Salary, Bonus, Dining, Coffee, Shopping, Transportation, Utilities, Health, and Education.
- Fully offline execution: no external network requests or third-party AI APIs required for transaction categorization.
- Adaptive heuristic rules that align with user input patterns over time.

### Smart Financial Advisory and Budgeting
- Automated detection of recurring monthly fixed commitments, such as rent, utility subscriptions, insurance, and internet fees.
- Real-time net cash flow computation (Total Income minus Total Expenses).
- Intelligent budget recommendations based on the 50/30/20 framework:
  - 50 percent for essential needs (housing, food, utilities).
  - 30 percent for flexible lifestyle expenses (dining, entertainment, shopping).
  - 20 percent for savings and emergency reserves.

### Deep OLED Dark Mode
- High-contrast dark theme optimized for low-light environments and battery conservation on OLED and AMOLED displays.
- Subdued color schemes: navy dark backgrounds, softened income green, and muted expense crimson engineered to prevent eye strain while meeting WCAG AAA contrast standards.
- One-tap instant toggle between Light and Dark interface themes.

### Privacy-First Architecture
- One-touch balance masking mode that obscures financial numbers with dot placeholders in public spaces.
- Pure local persistence: all account records, category mappings, and transaction logs are encrypted and stored solely within internal application storage.
- Clean slate design: no hardcoded sample accounts or placeholder transactions are injected upon initial startup.

---

## Technical Specifications

| Category | Technology |
| :--- | :--- |
| Core Language | Kotlin 2.0 (Coroutines, StateFlow, Serialization) |
| UI Framework | Android Jetpack Compose, Material Design 3 |
| Image Loading | Coil Compose |
| QR Code Generation | ZXing Core |
| Persistence | Encrypted SharedPreferences, Internal Storage |
| Min SDK | Android 7.0 (API Level 24) |
| Target SDK | Android 15 (API Level 35) |
| Build Tool | Gradle 8.11 with Android Gradle Plugin 8.7.2 |
| Web Scaffold | Vite 8, React 19, Tailwind CSS v4, TypeScript 5.7 |

---

## Repository Structure

```
PiggyBite/
├── android/                         # Native Android Application (Jetpack Compose)
│   ├── app/
│   │   ├── build.gradle             # App-level build configuration and dependencies
│   │   └── src/main/java/com/piggybite/app/
│   │       ├── data/                # DataRepository and SharedPreferences management
│   │       ├── model/               # Data classes: Bank, BankAccount, Transaction
│   │       ├── service/             # Offline AI classifier and Smart Finance Advisor
│   │       └── ui/
│   │           ├── components/      # UI components: WalletSummaryCard, TransactionList
│   │           ├── dialogs/         # Dialogs: AddAccount, MyQr, QrPayment
│   │           ├── screens/         # Screens: Home, Wallet, Analytics
│   │           └── theme/           # Color palettes, Dark Mode tokens, typography
│   ├── build.gradle                 # Project-level Gradle build script
│   ├── settings.gradle              # Module settings
│   └── gradlew.bat                  # Gradle wrapper script
├── APK/                             # Prebuilt release and debug binaries
│   ├── PiggyBite.apk
│   └── PiggyBite-Kotlin.apk
├── src/                             # Web interface scaffold (React and Tailwind CSS)
├── package.json                     # Web tooling configuration
└── README.md                        # Documentation
```

---

## Installation and Deployment

### Method 1: Install Prebuilt APK

Direct Download:
- [Download PiggyBite APK](https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk)

File Path in Repository:
- `APK/PiggyBite.apk`

Transfer the file to any Android device running Android 7.0 or newer, enable installation from unknown sources in device settings, and complete the installation prompt.

### Method 2: Build from Source

#### Prerequisites
- Android Studio Ladybug (2024.2) or later.
- Java Development Kit (JDK) 17 or JDK 21 (bundled JBR recommended).
- Android SDK Platform 35 and Build Tools installed.

#### Build Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/vinh0407/PiggyBite.git
   cd PiggyBite
   ```

2. Navigate to the Android project directory:
   ```bash
   cd android
   ```

3. Compile the debug APK using the Gradle wrapper:
   - On Windows:
     ```powershell
     .\gradlew.bat assembleDebug
     ```
   - On macOS or Linux:
     ```bash
     ./gradlew assembleDebug
     ```

4. The generated APK package is located at:
   `android/app/build/outputs/apk/debug/app-debug.apk`

---

## Verification and Testing

To verify the test suite:

```bash
cd android
./gradlew test
```

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.

Developed and maintained by Ung Do The Vinh (vinh0407).
