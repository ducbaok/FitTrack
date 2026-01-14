# FitTrack

A visual-first strength training Android app with an interactive anatomy heatmap.

---

## ✅ Current Features

### 🎯 Core Functionality
- **Interactive Anatomy View** - Tap muscle groups on SVG body diagrams
- **Male/Female Toggle** - Switch between body types
- **Front/Back Toggle** - View anterior/posterior muscles
- **Fatigue Heatmap** - Color-coded muscle recovery status
  - 🔴 Red = Tired (≥66% fatigue)
  - 🟡 Yellow = Recovering (33-66%)
  - 🟢 Green = Ready (<33%)

### 💪 Exercise System
- **Exercise Library** - 29 pre-loaded exercises
- **Smart Filtering** - By equipment and difficulty
- **Mark as Done** - One-tap workout logging with undo
- **Fatigue Decay** - 72-hour recovery algorithm (100/72 per hour)

### 🔐 Authentication
- **Mock Auth** - Works offline (Firebase ready but disabled)
- **Login/Signup UI** - Material 3 design

### 📊 Data & Tracking
- **Local Database** - Room with 5 entities
- **Streak Tracking** - Daily workout streaks
- **Workout History** - All completed exercises logged

---

## 🚀 Future Features

### Phase 7 - History & Gamification
- [ ] Workout history screen with calendar view
- [ ] Streak badge UI (7/30/100 day achievements)
- [ ] Weekly/monthly stats dashboard

### Phase 8 - Cloud Sync
- [ ] Firebase Authentication (Google Sign-In)
- [ ] Firestore cloud backup
- [ ] Multi-device sync

### Phase 9 - Polish & Testing
- [ ] Unit tests for ViewModels
- [ ] UI/UX improvements
- [ ] Dark mode optimization
- [ ] Performance profiling

### Phase 10 - Timers & Tracking Enhancements
- [ ] Rest Timer with Smart Suggestions (auto-suggest based on exercise type)
- [ ] Vibration/sound alerts when rest is complete
- [ ] Customizable timer presets (30s/60s/90s/120s)
- [ ] Personal Records (PR) Tracking with auto-detection
- [ ] PR history per exercise with date stamps
- [ ] "Beat your PR" motivation prompts

### Phase 11 - Workout Modes
- [ ] Superset/Circuit Mode
- [ ] Auto-rotate between exercises with minimal rest
- [ ] Track compound fatigue across muscle groups

### Phase 12 - Gamification & Engagement
- [ ] XP & Leveling System (earn XP per workout)
- [ ] Unlock badges/titles (e.g., "Iron Will", "Leg Day Legend")
- [ ] Weekly leaderboard (optional social comparison)
- [ ] Recovery Advisor ("Which muscle should I train today?")
- [ ] Suggest a balanced weekly split based on fatigue heatmap

### Phase 13 - Widgets & Accessibility
- [ ] Quick Log Widget (home screen widget for 2-tap logging)
- [ ] View today's fatigue summary at a glance

### Future Ideas
- [ ] Custom exercise creation
- [ ] Workout templates/routines
- [ ] Progress photos
- [ ] Body measurements tracking
- [ ] Social features (share progress)
- [ ] Wearable integration
- [ ] Voice-Activated Logging
- [ ] Export/Import Data (CSV/JSON)

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Kotlin, XML, Material 3 |
| Architecture | MVVM, Hilt DI |
| Database | Room (SQLite) |
| Async | Coroutines, Flow |
| Graphics | AndroidSVG |
| Auth | Firebase (optional) |

---

## 🏃 Quick Start

```bash
# Clone and open in Android Studio
# Connect device/emulator
# Click Run ▶️
```

**APK Location:** `app/build/outputs/apk/debug/app-debug.apk`