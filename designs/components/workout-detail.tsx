"use client"

import { ArrowLeft, MoreVertical, Clock, Dumbbell, Flame, GripVertical } from "lucide-react"
import { Button } from "@/components/ui/button"

interface WorkoutDetailProps {
  workout: {
    id: number
    name: string
    exercises: number
    duration: string
    muscles: string[]
    color: string
  }
  onBack: () => void
}

const workoutExercises = [
  { name: "Bench Press", sets: 4, reps: "8-10", rest: "90s", equipment: "Barbell" },
  { name: "Incline Dumbbell Press", sets: 3, reps: "10-12", rest: "60s", equipment: "Dumbbells" },
  { name: "Overhead Press", sets: 3, reps: "8-10", rest: "90s", equipment: "Barbell" },
  { name: "Lateral Raises", sets: 3, reps: "12-15", rest: "45s", equipment: "Dumbbells" },
  { name: "Tricep Pushdown", sets: 3, reps: "12-15", rest: "45s", equipment: "Cable" },
  { name: "Overhead Extension", sets: 3, reps: "10-12", rest: "45s", equipment: "Dumbbell" },
]

export function WorkoutDetail({ workout, onBack }: WorkoutDetailProps) {
  const totalSets = workoutExercises.reduce((acc, ex) => acc + ex.sets, 0)

  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div className="flex-1">
          <h1 className="text-lg font-semibold">{workout.name}</h1>
        </div>
        <button className="p-2 hover:bg-muted rounded-full transition-colors">
          <MoreVertical className="w-5 h-5" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto">
        {/* Hero section */}
        <div className={`${workout.color} p-6`}>
          <div className="flex items-center justify-center mb-4">
            <div className="w-20 h-20 bg-white/20 rounded-full flex items-center justify-center">
              <Dumbbell className="w-10 h-10 text-white" />
            </div>
          </div>
          <h2 className="text-2xl font-bold text-white text-center mb-2">{workout.name}</h2>
          <div className="flex items-center justify-center gap-4 text-white/80">
            <span className="flex items-center gap-1.5">
              <Dumbbell className="w-4 h-4" />
              {workout.exercises} exercises
            </span>
            <span className="flex items-center gap-1.5">
              <Clock className="w-4 h-4" />
              {workout.duration}
            </span>
          </div>
          <div className="flex justify-center gap-2 mt-4">
            {workout.muscles.map((muscle, idx) => (
              <span key={idx} className="px-3 py-1 bg-white/20 rounded-full text-sm text-white">
                {muscle}
              </span>
            ))}
          </div>
        </div>

        {/* Stats */}
        <div className="flex border-b border-border">
          <div className="flex-1 p-4 text-center border-r border-border">
            <p className="text-2xl font-bold">{totalSets}</p>
            <p className="text-xs text-muted-foreground">Total Sets</p>
          </div>
          <div className="flex-1 p-4 text-center border-r border-border">
            <p className="text-2xl font-bold">{workout.exercises}</p>
            <p className="text-xs text-muted-foreground">Exercises</p>
          </div>
          <div className="flex-1 p-4 text-center">
            <p className="text-2xl font-bold">~{workout.duration.replace(" min", "")}</p>
            <p className="text-xs text-muted-foreground">Minutes</p>
          </div>
        </div>

        {/* Exercise list */}
        <div className="p-4">
          <h3 className="font-semibold mb-3">Exercises</h3>
          <div className="space-y-2">
            {workoutExercises.map((exercise, index) => (
              <div key={index} className="flex items-center gap-3 bg-muted rounded-xl p-3">
                <div className="text-muted-foreground">
                  <GripVertical className="w-5 h-5" />
                </div>
                <div className="w-10 h-10 bg-background rounded-lg flex items-center justify-center">
                  <span className="font-semibold text-primary">{index + 1}</span>
                </div>
                <div className="flex-1 min-w-0">
                  <h4 className="font-medium text-sm">{exercise.name}</h4>
                  <p className="text-xs text-muted-foreground">{exercise.equipment}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-medium">
                    {exercise.sets} × {exercise.reps}
                  </p>
                  <p className="text-xs text-muted-foreground">Rest {exercise.rest}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom action */}
      <div className="p-4 border-t border-border">
        <Button className="w-full gap-2" size="lg">
          <Flame className="w-5 h-5" />
          Start Workout
        </Button>
      </div>
    </div>
  )
}
