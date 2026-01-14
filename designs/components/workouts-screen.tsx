"use client"

import { Calendar, Dumbbell, ListChecks, History, ChevronRight } from "lucide-react"

interface WorkoutsScreenProps {
  onSelectWorkout: (workout: any) => void
  onNavigate: (screen: string) => void
}

const userWorkouts = [
  {
    id: 1,
    name: "Push Day",
    exercises: 6,
    fatigueLevel: 75,
    muscles: ["Chest", "Shoulders", "Triceps"],
  },
  {
    id: 2,
    name: "Pull Day",
    exercises: 5,
    fatigueLevel: 65,
    muscles: ["Back", "Biceps", "Forearms"],
  },
  {
    id: 3,
    name: "Leg Day",
    exercises: 6,
    fatigueLevel: 85,
    muscles: ["Quads", "Hamstrings", "Calves"],
  },
  {
    id: 4,
    name: "Upper Body",
    exercises: 8,
    fatigueLevel: 70,
    muscles: ["Chest", "Back", "Shoulders"],
  },
]

export function WorkoutsScreen({ onSelectWorkout, onNavigate }: WorkoutsScreenProps) {
  return (
    <div className="flex-1 flex flex-col bg-background overflow-y-auto pb-20">
      {/* Header */}
      <div className="px-4 py-4 border-b border-border">
        <h1 className="text-2xl font-bold">Workout</h1>
        <p className="text-sm text-muted-foreground">Build and track your workouts</p>
      </div>

      <div className="flex-1 p-4 space-y-6">
        <div className="grid grid-cols-2 gap-3">
          {/* Track Workout */}
          <button
            onClick={() => onNavigate("track-workout")}
            className="bg-muted rounded-xl p-4 text-left hover:bg-muted/80 transition-colors"
          >
            <div className="w-10 h-10 bg-primary/20 rounded-lg flex items-center justify-center mb-3">
              <Calendar className="w-5 h-5 text-primary" />
            </div>
            <h3 className="font-semibold text-sm">Track Workout</h3>
            <p className="text-xs text-muted-foreground mt-1">Daily/Weekly/Monthly</p>
          </button>

          {/* Build Workout */}
          <button
            onClick={() => onNavigate("build-workout")}
            className="bg-muted rounded-xl p-4 text-left hover:bg-muted/80 transition-colors"
          >
            <div className="w-10 h-10 bg-blue-500/20 rounded-lg flex items-center justify-center mb-3">
              <Dumbbell className="w-5 h-5 text-blue-500" />
            </div>
            <h3 className="font-semibold text-sm">Build Workout</h3>
            <p className="text-xs text-muted-foreground mt-1">Add exercises & sets</p>
          </button>

          {/* Build Routine */}
          <button
            onClick={() => onNavigate("build-routine")}
            className="bg-muted rounded-xl p-4 text-left hover:bg-muted/80 transition-colors"
          >
            <div className="w-10 h-10 bg-green-500/20 rounded-lg flex items-center justify-center mb-3">
              <ListChecks className="w-5 h-5 text-green-500" />
            </div>
            <h3 className="font-semibold text-sm">Build Routine</h3>
            <p className="text-xs text-muted-foreground mt-1">Weekly/Monthly plan</p>
          </button>

          {/* History */}
          <button
            onClick={() => onNavigate("history")}
            className="bg-muted rounded-xl p-4 text-left hover:bg-muted/80 transition-colors"
          >
            <div className="w-10 h-10 bg-orange-500/20 rounded-lg flex items-center justify-center mb-3">
              <History className="w-5 h-5 text-orange-500" />
            </div>
            <h3 className="font-semibold text-sm">History</h3>
            <p className="text-xs text-muted-foreground mt-1">Recent exercises</p>
          </button>
        </div>

        {/* User's Workouts List with Fatigue Level */}
        <div>
          <h2 className="font-semibold mb-3">My Workouts</h2>
          <div className="space-y-3">
            {userWorkouts.map((workout) => (
              <button
                key={workout.id}
                onClick={() => onSelectWorkout(workout)}
                className="w-full bg-muted rounded-xl p-4 text-left hover:bg-muted/80 transition-colors"
              >
                <div className="flex items-center justify-between mb-2">
                  <h3 className="font-semibold">{workout.name}</h3>
                  <ChevronRight className="w-5 h-5 text-muted-foreground" />
                </div>

                <div className="flex flex-wrap gap-1.5 mb-3">
                  {workout.muscles.map((muscle, idx) => (
                    <span key={idx} className="px-2 py-0.5 bg-background rounded-full text-xs">
                      {muscle}
                    </span>
                  ))}
                </div>

                {/* Fatigue level bar */}
                <div>
                  <div className="flex items-center justify-between text-xs mb-1">
                    <span className="text-muted-foreground">Fatigue Level</span>
                    <span>{workout.fatigueLevel}%</span>
                  </div>
                  <div className="h-2 rounded-full overflow-hidden bg-background">
                    <div
                      className="h-full rounded-full transition-all duration-300"
                      style={{
                        width: `${workout.fatigueLevel}%`,
                        background: `linear-gradient(to right, #9ca3af, #dc2626)`,
                      }}
                    />
                  </div>
                </div>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
