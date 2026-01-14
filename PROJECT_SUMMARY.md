# FitTrack Project Summary

## Overview
FitTrack is a visual-first strength training Android application built with Kotlin. It focuses on interactive anatomy visualization to track muscle fatigue and workout progress.

## Technology Stack
- **Language**: Kotlin
- **Platform**: Android (Min SDK 24, Target SDK 34)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt (Dagger)
- **Database**: Room (SQLite)
- **Concurrency**: Coroutines & Flow
- **UI**: XML Layouts with Material Design 3

## Core Features (Modules)
The application is structured around 5 main features:

1.  **Home (`ui/home`)**
    - **Key Component**: `AnatomyView` (Custom View)
    - **Function**: Interactive muscle map (Front/Back, Male/Female) showing fatigue and engagement.

2.  **Workouts (`ui/workouts`)**
    - **Hub**: Dashboard for managing routines and programs.
    - **Tracker**: Active workout session logger (Sets, Reps, Weight).
    - **Builder**: Tools for creating workout templates and weekly schedules.

3.  **Explore (`ui/explore`)**
    - **Function**: Exercise library and search.
    - **Detail**: In-depth exercise instructions and muscle targeting.

4.  **History (`ui/history`)**
    - **Function**: Calendar and list view of completed workouts.
    - **Stats**: Progress tracking and volume analysis.

5.  **Profile (`ui/profile`)**
    - **Function**: User settings (`SettingsFragment`) and personal stats.

## Database Schema
- **Entities**: `WorkoutTemplate`, `Exercise`, `Routine`, `RoutineDay`.
- **Relationship**: 
  - Routines contain Days.
  - Days contain Workout Templates.
  - Templates contain Exercises (with target sets/reps).

## Current State
- **Phase**: 5 - UI Refinement.
- **Recent Work**: 
  - Upgraded `AnatomyView` to support Female anatomy.
  - Refactored Workout Tracker UI.
  - Implementing Workout Hub Dashboard.

  - Implementing Workout Hub Dashboard.

## Handoff / Next Actions status
We were mid-implementation of the **Workout Hub** (Dashboard).

### 1. Work in Progress (Verified)
The following files were created/modified and **Build Verification PASSED**:
- `e:\FitTrack\app\src\main\res\layout\fragment_workout_hub.xml` (Created)
- `e:\FitTrack\app\src\main\res\layout\item_routine.xml` (Created)
- `e:\FitTrack\app\src\main\java\com\fittrack\ui\workouts\WorkoutHubAdapter.kt` (Created)
- `e:\FitTrack\app\src\main\java\com\fittrack\ui\workouts\WorkoutsFragment.kt` (Refactored to use Hub layout)
- `e:\FitTrack\app\src\main\java\com\fittrack\ui\workouts\WorkoutsViewModel.kt` (Updated with mock Hub data)
- `e:\FitTrack\app\src\main\java\com\fittrack\ui\home\AnatomyView.kt` (Updated with Female anatomy - Syntax fixed)

### 2. Immediate Next Steps
1.  **Test Hub Logic**: The app builds, but you need to run it to verify the Dashboard displays correctly and "Start Workout" navigation works.
2.  **Refine Hub**: 
    - Wire up "Build Workout" buttons.
    - Implement the "My Workouts" vs "History" Tab logic.

### 3. Implementation Plan
After the Hub is stable, follow the *Screen-by-Screen* plan:
1.  **Exercise Selector** (New Feature)
2.  **Explore Details** (Refinement)
3.  **Dialogs** (Refinement)

