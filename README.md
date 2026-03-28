# 📻 Tuner Beta - Live Global Radio 

Tuner is a completely free, highly-optimized Android application and Mobile-Responsive Website that lets you stream over 40,000 live AM/FM radio stations from around the entire globe instantly. 

We utilize advanced hardware-aware audio routing, sophisticated background stream management, and a massive curated library to deliver the perfect radio experience.

[![Download for Android](https://img.shields.io/badge/Download_for-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](#releases)
[![Web Version](https://img.shields.io/badge/Live_Web-App-9146FF?style=for-the-badge&logo=react&logoColor=white)](https://tuner-fm-4b3c5.web.app)

---

## ✨ Core Features
* **Background Foreground Service**: Utilizes Android Media3 ExoPlayer running safely in a lightweight foreground service—your audio will flawlessly continue paying when you turn off your screen or lock your phone.
* **Auto-Reconnection**: Employs an intelligent exception fallback engine. If you drop a cell tower or enter a tunnel, Tuner won't crash; it mathematically buffers and restarts the playback socket autonomously.
* **Hardware Output Awareness**: Instantly recognizes when you connect AirPods, Wired Headphones, or rely on phone speakers and dynamically updates the visual interface directly through the `AudioManager` bridge.
* **Lock Screen Metadata**: Integrated asynchronous Coil background-fetching ensures that the exact logo of the station you're streaming is beamed beautifully straight to your Android notifications and lock screen.
* **Responsive Web Experience**: Prefer not to download the app? Access our entirely responsive, incredibly fast React web application equipped with Framer Motion animations and full mobile scaling.

## 🛠️ Technology Stack
### Android Application
- **Kotlin 1.9+ & Jetpack Compose**: Fully declarative UI paradigms running silky smooth at 120hz.
- **Media3 (ExoPlayer)**: Absolute state-of-the-art background streaming API replacing legacy `MediaPlayer`. 
- **Retrofit & Coroutines**: Rapid, asynchronous searches across massive international radio directories with smart query debouncing.

### Web Interface
- **React 18 & Vite**: Ultra-fast front-end processing architecture.
- **Framer Motion**: Smooth DOM-based transitions explicitly designed not to stutter on older hardware.
- **Firebase Hosting**: High-availability edge CDN delivery across the globe.

---

## 🚀 Installation 
Google Firebase blocks standalone executable hosting on their free-tier, but installing Tuner directly on your Android phone is simple safely.

1. Navigate to the **[Releases](https://github.com/KIRTANJOSHI/Tuner/releases)** tab of this repository.
2. Under "Assets", click and download the latest `tuner.beta.apk` file.
3. Open the file on your device and accept the generic "Install Unknown Apps" Android prompt. 
4. Select "Install Anyway" on the Play Protect popup *(Tuner is perfectly safe, but since it isn't deployed onto Google Play, Android warns you manually)*.

## 🤝 Contribution
Tuner is actively maintained and currently accepting issues and feature merge-requests. Make sure to detail any problems actively inside the Issues tab. 
