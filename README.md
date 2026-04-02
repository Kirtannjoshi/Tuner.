<p align="center">
  <img src="web/public/logo.svg" width="600" alt="Tuner Animated Logo">
</p>

<p align="center">
  <img src="https://img.shields.io/github/actions/workflow/status/Kirtannjoshi/Tuner./android.yml?branch=main&label=Android%20Build&style=for-the-badge&logo=android&color=3DDC84" alt="Android Build Status">
  <img src="https://img.shields.io/github/v/release/Kirtannjoshi/Tuner.?include_prereleases&label=Latest%20Version&style=for-the-badge&logo=github&color=EC4899" alt="Latest Release Version">
  <img src="https://img.shields.io/badge/Firebase-Hosting_Live-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase Deployment Status">
</p>

---

## 📻 Executive Summary
**Tuner** is a state-of-the-art, high-fidelity radio streaming ecosystem designed for the modern listener. By unifying a massive global directory of over 40,000 AM/FM stations with a "Sober, Delicate, and Aesthetic" design philosophy, Tuner redefines the digital radio experience across Android and the Web.

Developed as a flagship project for **Dev.For.Me**, Tuner leverages hardware-aware audio routing and a sophisticated background-foreground synchronization engine to deliver lag-free, ultra-low-latency sound to every user, everywhere.

---

## 🏛️ System Architecture

### 📱 Android Flagship Core
- **Jetpack Compose 1.9+**: A fully declarative UI architecture enabling fluid, 120Hz-ready transitions and hardware-accelerated layouts.
- **Media3 (ExoPlayer)**: Our low-latency streaming engine featuring custom `LoadControl` logic for near-instant playback starting at 100ms.
- **Background Foreground Service**: Tuned for extreme stability, maintaining persistent high-quality audio streams even under heavy system resource pressure or lock-screen states.
- **Dynamic Versioning**: Automated build number synchronization tied directly to the **GitHub Action Run Number** for 100% build traceability.

### 🌐 Web Responsive Edge
- **React 18 & Vite**: A lightning-fast, edge-delivered front-end architecture optimized for mobile and desktop browsers alike.
- **Framer Motion**: Smooth, glassmorphic UI interactions designed to mirror the premium feel of the native Android application.
- **Unified Project ID**: Seamlessly synced with the **`radiowave-7xxi7`** Firebase ecosystem for global directory consistency.

---



## 📦 Getting Started

### 🌍 Experience the Web App
Access the high-fidelity web radio directly in your browser:
**[Launch Tuner Web](https://radiowave-7xxi7.web.app)**

### 📱 Install on Android
For the full native experience, download the APK directly from our verified professional mirror:
**[Direct APK Download](https://radiowave-7xxi7.web.app/download/tuner.apk)**

---

## 🤝 Maintenance & Privacy
As a private organization project, Tuner is maintained with a strict focus on security and codebase hygiene.

- **Private Secrets**: All sensitive keys (Firebase, Google Services) are managed via externalized GitHub Secrets and a non-public `secrets/` architecture.
- **Codebase Hygiene**: Strict `.gitignore` rules prevent build-clutter and binary leaks, ensuring a performant development environment.

© 2026 Tuner. All rights reserved. Professional Grade Audio. 📡
