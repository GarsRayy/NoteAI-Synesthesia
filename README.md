# 🌌 Synesthesia - AI-Powered Visual Journal Super-App

![Synesthesia Banner](https://raw.githubusercontent.com/GarsRayy/NoteAI-Synesthesia/main/screenshots/SS2.png)

**Synesthesia** is an immersive, intelligent journaling ecosystem built with **Kotlin Multiplatform (KMP)**. It transforms traditional note-taking into a celestial sensory experience, blending state-of-the-art AI analysis with a dynamic visual language that responds to your emotional state.

---

## 📺 Visual Showcase

| ☀️ Daylight Mode (Normal) | 🌌 Astronomy Mode (Space) |
| :---: | :---: |
| ![Daylight Home](screenshots/SS1.png) | ![Astronomy Home](screenshots/SS2.png) |
| *Clean interface with dynamic sky* | *Immersive galaxy with starfields* |

| 🧘 Sanctuary (Mindfulness) | 📊 Insights (Analytics) |
| :---: | :---: |
| ![Sanctuary](screenshots/SS3.png) | ![Insights](screenshots/SS4.png) |
| *Interactive breathing & meditation* | *Real-time data-driven mood trends* |

### 🎥 Live Demo
[![Watch the Demo](https://img.shields.io/badge/Demo-Watch%20Now-red?style=for-the-badge&logo=youtube)](https://youtube.com/shorts/RYtQWypN5Vo)

---

## ✅ Sprint Deliverables & Progress

### 🚀 Sprint 1: Foundation (100% Complete)
- [x] **Clean Architecture Setup**: Implementation of Presentation, Domain, and Data layers.
- [x] **KMP Project Core**: Cross-platform configuration for Android and iOS.
- [x] **Dependency Injection**: Full integration using Koin (+10% Bonus).
- [x] **Local Persistence**: SQLDelight database configuration.

### 🚀 Sprint 2: Core Features & UI Overhaul (100% Complete)
- [x] **Celestial Design System**: Implementation of dynamic Starfield and Daylight Sky backgrounds.
- [x] **Guided Journaling**: Two-step flow for emotion-focused memory creation.
- [x] **AI Resonance Engine**: Automated title generation and content paraphrasing via Gemini API.
- [x] **Interactive Galaxy**: 3D-effect Constellation Canvas for memory visualization.

### 🚀 Sprint 3: Advanced Intelligence & UX (100% Complete)
- [x] **Advanced Analytics**: Interactive Mood Calendar and color-coded real-time charts.
- [x] **Offline Resilience**: Real-time network monitoring with "Offline Save" fallback capability.
- [x] **Smart Wellness**: AI-driven ritual matching and 5-4-3-2-1 Grounding exercises.
- [x] **Local Audio Engine**: Offline-first ambient playback for Sonic Zen.
- [x] **Search & Filter**: High-performance pill-shaped search with category filtering and multi-sort.

---

## 🏗️ Architecture & Technology Stack

### Technical Overview
Synesthesia is built following **Clean Architecture** principles to ensure a scalable and maintainable codebase:
-   **Presentation**: Compose Multiplatform with MVVM (StateFlow/SharedFlow).
-   **Domain**: Pure Kotlin business logic, Interactors (Use Cases), and Repository Interfaces.
-   **Data**: SQLDelight (Local), Ktor (Remote API), and DataStore (Preferences).

### Core Stack
| Layer | Technology |
|---|---|
| **Language** | Kotlin (100%) |
| **Framework** | Compose Multiplatform (1.7.0) |
| **Dependency Injection** | Koin (4.0.0) |
| **AI Integration** | Google Gemini API |
| **Database** | SQLDelight (2.0.2) |
| **Networking** | Ktor Client (3.0.1) |
| **Concurrency** | Kotlin Coroutines & Flow |

---

## 🛠️ Setup Instructions

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/GarsRayy/NoteAI-Synesthesia.git
    ```
2.  **API Key Configuration**
    Add `GEMINI_API_KEY=your_key_here` to your `local.properties` file.
3.  **Environment**
    Recommended IDE: **Android Studio Ladybug (2024.2.1) or newer**.
4.  **Run Application**
    Execute the Gradle task `:composeApp:installDebug` for Android or the corresponding iOS target.

---
*Developed with ❤️ as a major project for Mobile Application Development - ITERA.*
