# Privacy Policy for PiggyBite

Last updated: October 30, 2026

PiggyBite ("we", "our", or "the application") is committed to protecting your privacy. This Privacy Policy explains how information is handled within the PiggyBite Android application (package name: `com.piggybite.app`).

## 1. Core Principle: Zero-Knowledge and Local-First

PiggyBite is designed with a strict privacy-first architecture:
- All personal financial data, bank account balances, transaction records, and category allocations are processed and stored solely on your local device.
- We do not operate external database servers that collect, transmit, store, or sell your personal or financial information.
- No user account registration or personal identity submission is required to use PiggyBite.

## 2. Permissions and Usage

The application requests specific Android device permissions strictly for local on-device utility:

### A. Camera (`android.permission.CAMERA`)
- **Purpose**: Used exclusively for scanning VietQR payment codes and standard QR codes in real-time.
- **Data Handling**: Camera frames are analyzed locally in memory using on-device optical processing. No photos, videos, or camera recordings are saved or transmitted to external servers.

### B. Notification Access (`android.permission.BIND_NOTIFICATION_LISTENER_SERVICE`)
- **Purpose**: Enables the optional automatic tracking of banking balance fluctuations by reading incoming push notifications or SMS transaction alerts from supported Vietnamese banking and digital wallet applications.
- **Data Handling**: Notification content is parsed entirely on your device via local regular expression matching to extract amounts and timestamps. Personal messages, OTP codes, or unrelated notifications are discarded immediately and never retained.

### C. Storage and Photo Access
- **Purpose**: Allows users to optionally attach and display a photo of their personal receive-money QR code.
- **Data Handling**: Selected QR images are stored in the application's isolated private internal storage on your device and are never uploaded.

## 3. On-Device Machine Learning and Classification

The financial classification engine (expense and income categorizer) runs completely offline using local heuristic algorithms and on-device natural language models. No financial text or transaction description is transmitted to cloud AI APIs.

## 4. Third-Party Services

PiggyBite does not integrate third-party advertising SDKs, tracking frameworks, or analytics services that harvest user identities or behavior.

## 5. Security

All local application records are protected by Android platform sandbox isolation and encrypted application storage mechanisms.

## 6. Children's Privacy

PiggyBite does not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children.

## 7. Changes to This Privacy Policy

We may update our Privacy Policy periodically. Any updates will be published within this document in our official repository.

## 8. Contact Information

If you have any questions or feedback regarding this Privacy Policy, you may contact the development team via our repository issue tracker:
- Repository: https://github.com/vinh0407/PiggyBite
