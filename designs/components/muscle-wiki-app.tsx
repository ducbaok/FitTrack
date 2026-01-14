"use client"

import { useState } from "react"
import { BodyMap } from "./body-map"
import { ExerciseList } from "./exercise-list"
import { ExerciseDetail } from "./exercise-detail"
import { WorkoutsScreen } from "./workouts-screen"
import { WorkoutDetail } from "./workout-detail"
import { ProfileScreen } from "./profile-screen"
import { ExploreScreen } from "./explore-screen"
import { BuildWorkoutScreen } from "./build-workout-screen"
import { BuildRoutineScreen } from "./build-routine-screen"
import { HistoryScreen } from "./history-screen"
import { TrackWorkoutScreen } from "./track-workout-screen"
import { PersonalInfoScreen } from "./personal-info-screen"
import { SettingsScreen } from "./settings-screen"
import { BottomNav } from "./bottom-nav"
import { Header } from "./header"

export type MuscleGroup =
  | "chest"
  | "shoulders"
  | "biceps"
  | "forearms"
  | "abs"
  | "quads"
  | "calves"
  | "traps"
  | "lats"
  | "triceps"
  | "glutes"
  | "hamstrings"
  | "lower-back"
  | null

export type ViewMode = "front" | "back"

export default function MuscleWikiApp() {
  const [selectedMuscle, setSelectedMuscle] = useState<MuscleGroup>(null)
  const [viewMode, setViewMode] = useState<ViewMode>("front")
  const [activeTab, setActiveTab] = useState("home")
  const [category, setCategory] = useState("Featured")
  const [selectedExercise, setSelectedExercise] = useState<{
    name: string
    equipment: string
    difficulty: string
  } | null>(null)
  const [selectedWorkout, setSelectedWorkout] = useState<any>(null)
  const [workoutSubScreen, setWorkoutSubScreen] = useState<string | null>(null)
  const [profileSubScreen, setProfileSubScreen] = useState<string | null>(null)

  const handleWorkoutNavigate = (screen: string) => {
    setWorkoutSubScreen(screen)
  }

  const handleProfileNavigate = (screen: string) => {
    setProfileSubScreen(screen)
  }

  const resetState = () => {
    setSelectedMuscle(null)
    setSelectedExercise(null)
    setSelectedWorkout(null)
    setWorkoutSubScreen(null)
    setProfileSubScreen(null)
  }

  // Determine if bottom nav should show
  const showBottomNav = !selectedExercise && !selectedWorkout && !workoutSubScreen && !profileSubScreen

  return (
    <div className="min-h-screen bg-background flex flex-col max-w-md mx-auto">
      {/* Home tab */}
      {activeTab === "home" && !selectedMuscle && !selectedExercise && (
        <Header category={category} onCategoryChange={setCategory} />
      )}

      {activeTab === "home" && (
        <>
          {selectedExercise && selectedMuscle ? (
            <ExerciseDetail
              exercise={selectedExercise}
              muscle={selectedMuscle}
              onBack={() => setSelectedExercise(null)}
            />
          ) : selectedMuscle ? (
            <ExerciseList
              muscle={selectedMuscle}
              onBack={() => setSelectedMuscle(null)}
              onSelectExercise={setSelectedExercise}
            />
          ) : (
            <BodyMap viewMode={viewMode} setViewMode={setViewMode} onSelectMuscle={setSelectedMuscle} />
          )}
        </>
      )}

      {/* Explore tab */}
      {activeTab === "explore" && (
        <>
          {selectedExercise ? (
            <ExerciseDetail
              exercise={selectedExercise}
              muscle={selectedExercise.equipment}
              onBack={() => setSelectedExercise(null)}
            />
          ) : (
            <ExploreScreen onSelectExercise={setSelectedExercise} />
          )}
        </>
      )}

      {/* Workout tab */}
      {activeTab === "workout" && (
        <>
          {workoutSubScreen === "build-workout" ? (
            <BuildWorkoutScreen onBack={() => setWorkoutSubScreen(null)} />
          ) : workoutSubScreen === "build-routine" ? (
            <BuildRoutineScreen onBack={() => setWorkoutSubScreen(null)} />
          ) : workoutSubScreen === "history" ? (
            <HistoryScreen onBack={() => setWorkoutSubScreen(null)} />
          ) : workoutSubScreen === "track-workout" ? (
            <TrackWorkoutScreen onBack={() => setWorkoutSubScreen(null)} />
          ) : selectedWorkout ? (
            <WorkoutDetail workout={selectedWorkout} onBack={() => setSelectedWorkout(null)} />
          ) : (
            <WorkoutsScreen onSelectWorkout={setSelectedWorkout} onNavigate={handleWorkoutNavigate} />
          )}
        </>
      )}

      {/* Profile tab */}
      {activeTab === "profile" && (
        <>
          {profileSubScreen === "personal-info" ? (
            <PersonalInfoScreen onBack={() => setProfileSubScreen(null)} />
          ) : profileSubScreen === "settings" ? (
            <SettingsScreen onBack={() => setProfileSubScreen(null)} />
          ) : (
            <ProfileScreen onNavigate={handleProfileNavigate} />
          )}
        </>
      )}

      {showBottomNav && (
        <BottomNav
          activeTab={activeTab}
          onTabChange={(tab) => {
            setActiveTab(tab)
            resetState()
          }}
        />
      )}
    </div>
  )
}
