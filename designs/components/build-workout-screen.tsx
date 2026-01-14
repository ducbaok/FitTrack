"use client"

import { ArrowLeft, Search, Plus, X, Minus } from "lucide-react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

interface BuildWorkoutScreenProps {
  onBack: () => void
}

const allExercises = [
  { name: "Bench Press", equipment: "Barbell", muscle: "Chest" },
  { name: "Squats", equipment: "Barbell", muscle: "Quads" },
  { name: "Deadlift", equipment: "Barbell", muscle: "Back" },
  { name: "Overhead Press", equipment: "Barbell", muscle: "Shoulders" },
  { name: "Pull-ups", equipment: "Bodyweight", muscle: "Back" },
  { name: "Dumbbell Curl", equipment: "Dumbbell", muscle: "Biceps" },
  { name: "Tricep Pushdown", equipment: "Cable", muscle: "Triceps" },
  { name: "Leg Press", equipment: "Machine", muscle: "Quads" },
]

interface WorkoutExercise {
  name: string
  equipment: string
  muscle: string
  sets: number
  reps: number
  weight: number
}

export function BuildWorkoutScreen({ onBack }: BuildWorkoutScreenProps) {
  const [searchQuery, setSearchQuery] = useState("")
  const [workoutName, setWorkoutName] = useState("")
  const [selectedExercises, setSelectedExercises] = useState<WorkoutExercise[]>([])

  const filteredExercises = allExercises.filter(
    (ex) =>
      ex.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      ex.muscle.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  const addExercise = (exercise: (typeof allExercises)[0]) => {
    if (!selectedExercises.find((e) => e.name === exercise.name)) {
      setSelectedExercises([
        ...selectedExercises,
        {
          ...exercise,
          sets: 3,
          reps: 10,
          weight: 0,
        },
      ])
    }
  }

  const removeExercise = (name: string) => {
    setSelectedExercises(selectedExercises.filter((e) => e.name !== name))
  }

  const updateExercise = (name: string, field: "sets" | "reps" | "weight", value: number) => {
    setSelectedExercises(selectedExercises.map((e) => (e.name === name ? { ...e, [field]: Math.max(0, value) } : e)))
  }

  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-semibold flex-1">Build Workout</h1>
      </div>

      <div className="flex-1 overflow-y-auto p-4 pb-24 space-y-4">
        {/* Workout Name */}
        <Input
          placeholder="Workout Name"
          value={workoutName}
          onChange={(e) => setWorkoutName(e.target.value)}
          className="text-lg font-medium"
        />

        {/* Search exercises */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search exercises..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>

        {/* Search results */}
        {searchQuery && (
          <div className="bg-muted rounded-xl overflow-hidden">
            {filteredExercises.map((exercise, index) => (
              <div key={index} className="flex items-center justify-between p-3 border-b border-border last:border-0">
                <div>
                  <p className="font-medium text-sm">{exercise.name}</p>
                  <p className="text-xs text-muted-foreground">
                    {exercise.muscle} • {exercise.equipment}
                  </p>
                </div>
                <button
                  onClick={() => addExercise(exercise)}
                  className="p-2 bg-primary rounded-full hover:bg-primary/90 transition-colors"
                >
                  <Plus className="w-4 h-4 text-primary-foreground" />
                </button>
              </div>
            ))}
          </div>
        )}

        {/* Selected exercises */}
        {selectedExercises.length > 0 && (
          <div className="space-y-3">
            <h3 className="font-semibold">Added Exercises</h3>
            {selectedExercises.map((exercise, index) => (
              <div key={index} className="bg-muted rounded-xl p-4">
                <div className="flex items-center justify-between mb-3">
                  <div>
                    <p className="font-medium">{exercise.name}</p>
                    <p className="text-xs text-muted-foreground">{exercise.muscle}</p>
                  </div>
                  <button
                    onClick={() => removeExercise(exercise.name)}
                    className="p-1.5 hover:bg-background rounded-full transition-colors"
                  >
                    <X className="w-4 h-4 text-muted-foreground" />
                  </button>
                </div>

                {/* Sets, Reps, Weight controls */}
                <div className="grid grid-cols-3 gap-3">
                  <div className="text-center">
                    <p className="text-xs text-muted-foreground mb-1">Sets</p>
                    <div className="flex items-center justify-center gap-2">
                      <button
                        onClick={() => updateExercise(exercise.name, "sets", exercise.sets - 1)}
                        className="p-1 bg-background rounded"
                      >
                        <Minus className="w-3 h-3" />
                      </button>
                      <span className="font-medium w-6 text-center">{exercise.sets}</span>
                      <button
                        onClick={() => updateExercise(exercise.name, "sets", exercise.sets + 1)}
                        className="p-1 bg-background rounded"
                      >
                        <Plus className="w-3 h-3" />
                      </button>
                    </div>
                  </div>
                  <div className="text-center">
                    <p className="text-xs text-muted-foreground mb-1">Reps</p>
                    <div className="flex items-center justify-center gap-2">
                      <button
                        onClick={() => updateExercise(exercise.name, "reps", exercise.reps - 1)}
                        className="p-1 bg-background rounded"
                      >
                        <Minus className="w-3 h-3" />
                      </button>
                      <span className="font-medium w-6 text-center">{exercise.reps}</span>
                      <button
                        onClick={() => updateExercise(exercise.name, "reps", exercise.reps + 1)}
                        className="p-1 bg-background rounded"
                      >
                        <Plus className="w-3 h-3" />
                      </button>
                    </div>
                  </div>
                  <div className="text-center">
                    <p className="text-xs text-muted-foreground mb-1">Weight (lb)</p>
                    <div className="flex items-center justify-center gap-2">
                      <button
                        onClick={() => updateExercise(exercise.name, "weight", exercise.weight - 5)}
                        className="p-1 bg-background rounded"
                      >
                        <Minus className="w-3 h-3" />
                      </button>
                      <span className="font-medium w-8 text-center">{exercise.weight}</span>
                      <button
                        onClick={() => updateExercise(exercise.name, "weight", exercise.weight + 5)}
                        className="p-1 bg-background rounded"
                      >
                        <Plus className="w-3 h-3" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Save button */}
      <div className="fixed bottom-0 left-0 right-0 max-w-md mx-auto p-4 border-t border-border bg-background">
        <Button className="w-full" disabled={!workoutName || selectedExercises.length === 0}>
          Save Workout
        </Button>
      </div>
    </div>
  )
}
