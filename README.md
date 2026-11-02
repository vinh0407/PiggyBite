# PiggyBite

Smart, Privacy-First Personal Finance Manager for Android.

<p align="center">
  <a href="https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk">
    <img src="https://img.shields.io/badge/Download-PiggyBite%20APK-0E85D4?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" />
  </a>
  <a href="https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.aab">
    <img src="https://img.shields.io/badge/Download-Google%20Play%20Bundle%20(AAB)-10B981?style=for-the-badge&logo=googleplay&logoColor=white" alt="Download AAB" />
  </a>
</p>

---

## Direct Downloads and Artifacts

Get the latest release binaries:
- Direct APK Package: [PiggyBite.apk](https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.apk)
- Google Play Store Bundle: [PiggyBite.aab](https://github.com/vinh0407/PiggyBite/raw/main/APK/PiggyBite.aab)
- Privacy Policy: [PRIVACY_POLICY.md](https://github.com/vinh0407/PiggyBite/blob/main/PRIVACY_POLICY.md)

Repository paths:
- `APK/PiggyBite.apk` (Signed Release APK)
- `APK/PiggyBite.aab` (Signed Release Bundle)
- Application Package ID: `com.piggybite.app`

---

## Interface Showcase

| Home Overview | Transaction History | Financial Reports |
| :---: | :---: | :---: |
| <img src="screenshots/01_home_screen.png" width="260" alt="Home Overview Screen" /> | <img src="screenshots/02_history_screen.png" width="260" alt="Transaction History Screen" /> | <img src="screenshots/03_reports_screen.png" width="260" alt="Financial Reports Screen" /> |
| Frosted glass hero card, quick actions grid, and floating navigation dock. | Income/expense metrics, real-time query filtering, and long-press refund actions. | Category distribution donut chart, 6-month cashflow bars, and day-by-day calendar. |

| Monthly Wrapped | Settings and Privacy | VietQR Camera Scanner |
| :---: | :---: | :---: |
| <img src="screenshots/04_monthly_wrapped.png" width="260" alt="Monthly Wrapped Infographic" /> | <img src="screenshots/05_settings_screen.png" width="260" alt="Settings and Data Privacy Screen" /> | <img src="screenshots/06_scan_qr_screen.png" width="260" alt="VietQR Scanner Screen" /> |
| Financial habit review, savings rate analytics, peak spending days, and export. | Biometric app lock, Excel-compatible CSV export, JSON backup, and AI training. | High-speed CameraX scanner with automated bank and account recognition. |

---

## Architectural Highlights

### 1. Pure Offline and Privacy-First Design
- Zero External Servers: All accounts, transactions, QR image assets, and preferences are stored exclusively inside the sandboxed local storage of the device.
- No Tracking or Telemetry: PiggyBite does not transmit any financial payload, device identifiers, or usage logs over the internet.
- Privacy Masking Mode: A single tap on the visibility toggle conceals financial figures behind secure dot placeholders when operating in public areas.

### 2. VietQR Standard Banking Integration
- Coverage for over 50 Vietnamese banks and digital wallets, including Vietcombank, MB Bank, Techcombank, Agribank, ACB, VPBank, TPBank, BIDV, MoMo, ZaloPay, and Viettel Money.
- High-Performance CameraX Scanner: Automatically extracts beneficiary accounts, bank codes, and transfer sums, allowing immediate handoff to corresponding installed banking applications.
- Resilient Account Number Parsing: Automatically detects target financial institutions even when keywords are abbreviated or contain typo deviations.
- Offline Personal QR Storage: Users can store their receiving QR codes locally for instant retrieval without internet access.

### 3. Biometric Security and Application Lock
- Integrated with Android Biometric framework (`androidx.biometric:biometric`).
- Offers cryptographic fingerprint recognition, face authentication, and system PIN/password fallback.
- Activates an unyielding lock screen upon application cold start and background-to-foreground transitions to safeguard financial data from unauthorized physical access.

### 4. Data Backup and Restoration
- Excel-Compatible UTF-8 CSV: Generates CSV export files injected with a Byte Order Mark (BOM) header, ensuring Vietnamese accents display seamlessly across Microsoft Excel and Google Sheets without encoding artifacts.
- Full JSON Data Packages: Encapsulates all bank accounts, balances, and historical entries into an offline backup file for device migration.
- System Share Sheet Integration: Facilitates direct saving to Google Drive, local storage, or messaging clients via standard Android Storage Access Framework (SAF) protocols.
- Seamless Import: Validates, deduplicates, and restores transactions and account balances from user-selected backup files.

### 5. Home Screen App Widget and Quick Settings Tile
- Home Screen Widget:
  - Displays total portfolio balance and today's accumulated expenses directly on the desktop or lock screen.
  - Dedicated one-touch action buttons to launch the VietQR camera scanner or display personal receiving QR codes.
  - Automatically synchronizes with the local repository in real time.
- Quick Settings Tile:
  - Adds a dedicated "Scan VietQR" tile to the Android quick settings notification shade for rapid checkout execution at payment counters.

### 6. Monthly Financial Wrapped Infographic
- Monthly habit review engine calculating net savings rate percentages against total monthly revenue.
- Highlights primary cost categories and identifies peak expenditure dates.
- Generates structured infographic summaries suitable for personal archival or social sharing.

### 7. On-Device AI Transaction Categorization
- Embedded lightweight natural language classifier tailored for Vietnamese banking transaction syntax.
- Processes incoming notifications from banking applications entirely on-device to log transactions automatically without manual entry.
- Built-in simulation and feedback interface to train heuristic parameters based on individual user confirmation.

---

## Design System and Ergonomics

- Material Design 3 Architecture: Adheres to Android Material Design 3 guidelines with a custom modern minimalist aesthetic.
- Frosted Glass Translucency: Uses subtle alpha surfaces, micro-borders, and optical elevation to deliver depth without visual clutter.
- Floating Capsule Navigation: Features an animated spring bubble active destination indicator on a floating pill dock.
- OLED Deep Dark Mode: Built on pure dark backgrounds (`#050A12`) to conserve display energy on OLED/AMOLED panels while mitigating visual fatigue.

---

## Technology Stack

| Component | Implementation |
| :--- | :--- |
| Core Language | Kotlin 2.0 (Coroutines, StateFlow, Flow operators) |
| UI Toolkit | Android Jetpack Compose, Material 3, Compose Navigation |
| Architecture Pattern | Unidirectional Data Flow (UDF), Repository Pattern |
| Biometric Framework | AndroidX Biometric 1.2.0 (`BiometricPrompt`) |
| Camera and Optical | CameraX 1.4.1, ZXing Core 3.5.3 |
| Image Processing | Coil Compose 2.7.0 |
| Storage and Export | Android Storage Access Framework (SAF), AndroidX FileProvider |
| Target Specifications | Min SDK: 24 (Android 7.0) / Target SDK: 35 (Android 15) |

---

## Building from Source

### Prerequisites
- JDK 17 or higher
- Android SDK Platform 35
- Android Studio Ladybug or later / Gradle 8.11

### Build Instructions
Clone the repository and build the release binary:

```bash
git clone https://github.com/vinh0407/PiggyBite.git
cd PiggyBite/android
./gradlew assembleRelease bundleRelease
```

The compiled release packages will be generated at:
- APK: `android/app/build/outputs/apk/release/app-release.apk`
- AAB: `android/app/build/outputs/bundle/release/app-release.aab`

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
