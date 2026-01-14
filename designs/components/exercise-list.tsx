"use client"

import type React from "react"

import { ArrowLeft, Star, Search, ChevronDown } from "lucide-react"
import type { MuscleGroup } from "./muscle-wiki-app"
import { cn } from "@/lib/utils"
import { useState } from "react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"

interface ExerciseListProps {
  muscle: MuscleGroup
  onBack: () => void
  onSelectExercise: (exercise: { name: string; equipment: string; difficulty: string; type: string }) => void
}

const exerciseData: Record<string, { name: string; equipment: string; difficulty: string; type: string }[]> = {
  chest: [
    { name: "Bench Press", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Incline Dumbbell Press", equipment: "Dumbbells", difficulty: "Intermediate", type: "Compound" },
    { name: "Push-ups", equipment: "Bodyweight", difficulty: "Beginner", type: "Compound" },
    { name: "Cable Flyes", equipment: "Cable", difficulty: "Beginner", type: "Isolation" },
    { name: "Dumbbell Flyes", equipment: "Dumbbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Decline Bench Press", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
  ],
  shoulders: [
    { name: "Overhead Press", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Lateral Raises", equipment: "Dumbbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Front Raises", equipment: "Dumbbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Face Pulls", equipment: "Cable", difficulty: "Beginner", type: "Isolation" },
    { name: "Arnold Press", equipment: "Dumbbell", difficulty: "Intermediate", type: "Compound" },
  ],
  biceps: [
    { name: "Barbell Curl", equipment: "Barbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Hammer Curls", equipment: "Dumbbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Preacher Curl", equipment: "Machine", difficulty: "Intermediate", type: "Isolation" },
    { name: "Concentration Curl", equipment: "Dumbbell", difficulty: "Beginner", type: "Isolation" },
  ],
  forearms: [
    { name: "Wrist Curls", equipment: "Barbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Reverse Wrist Curls", equipment: "Barbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Farmer's Walk", equipment: "Dumbbells", difficulty: "Intermediate", type: "Compound" },
    { name: "Dead Hang", equipment: "Pull-up Bar", difficulty: "Beginner", type: "Isolation" },
  ],
  abs: [
    { name: "Crunches", equipment: "Bodyweight", difficulty: "Beginner", type: "Isolation" },
    { name: "Plank", equipment: "Bodyweight", difficulty: "Beginner", type: "Isolation" },
    { name: "Hanging Leg Raise", equipment: "Pull-up Bar", difficulty: "Intermediate", type: "Compound" },
    { name: "Cable Crunch", equipment: "Cable", difficulty: "Intermediate", type: "Isolation" },
    { name: "Russian Twist", equipment: "Bodyweight", difficulty: "Beginner", type: "Isolation" },
    { name: "Ab Wheel Rollout", equipment: "Ab Wheel", difficulty: "Advanced", type: "Compound" },
  ],
  quads: [
    { name: "Squats", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Leg Press", equipment: "Machine", difficulty: "Beginner", type: "Compound" },
    { name: "Lunges", equipment: "Dumbbells", difficulty: "Beginner", type: "Compound" },
    { name: "Leg Extension", equipment: "Machine", difficulty: "Beginner", type: "Compound" },
    { name: "Bulgarian Split Squat", equipment: "Dumbbells", difficulty: "Intermediate", type: "Compound" },
    { name: "Front Squats", equipment: "Barbell", difficulty: "Advanced", type: "Compound" },
  ],
  calves: [
    { name: "Standing Calf Raise", equipment: "Machine", difficulty: "Beginner", type: "Isolation" },
    { name: "Seated Calf Raise", equipment: "Machine", difficulty: "Beginner", type: "Isolation" },
    { name: "Donkey Calf Raise", equipment: "Machine", difficulty: "Intermediate", type: "Isolation" },
    { name: "Single Leg Calf Raise", equipment: "Bodyweight", difficulty: "Beginner", type: "Isolation" },
  ],
  traps: [
    { name: "Barbell Shrugs", equipment: "Barbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Dumbbell Shrugs", equipment: "Dumbbells", difficulty: "Beginner", type: "Isolation" },
    { name: "Face Pulls", equipment: "Cable", difficulty: "Beginner", type: "Isolation" },
    { name: "Upright Rows", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
  ],
  lats: [
    { name: "Pull-ups", equipment: "Pull-up Bar", difficulty: "Intermediate", type: "Compound" },
    { name: "Lat Pulldown", equipment: "Cable", difficulty: "Beginner", type: "Isolation" },
    { name: "Barbell Row", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Single Arm Dumbbell Row", equipment: "Dumbbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Seated Cable Row", equipment: "Cable", difficulty: "Beginner", type: "Isolation" },
    { name: "T-Bar Row", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
  ],
  triceps: [
    { name: "Tricep Dips", equipment: "Bodyweight", difficulty: "Intermediate", type: "Compound" },
    { name: "Tricep Pushdown", equipment: "Cable", difficulty: "Beginner", type: "Isolation" },
    { name: "Skull Crushers", equipment: "Machine", difficulty: "Intermediate", type: "Isolation" },
    { name: "Overhead Tricep Extension", equipment: "Dumbbell", difficulty: "Beginner", type: "Isolation" },
    { name: "Close Grip Bench Press", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
  ],
  glutes: [
    { name: "Hip Thrust", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Glute Bridge", equipment: "Bodyweight", difficulty: "Beginner", type: "Isolation" },
    { name: "Romanian Deadlift", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Cable Kickback", equipment: "Cable", difficulty: "Beginner", type: "Isolation" },
    { name: "Sumo Deadlift", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
  ],
  hamstrings: [
    { name: "Romanian Deadlift", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Leg Curl", equipment: "Machine", difficulty: "Beginner", type: "Isolation" },
    { name: "Good Mornings", equipment: "Barbell", difficulty: "Advanced", type: "Compound" },
    { name: "Nordic Curl", equipment: "Bodyweight", difficulty: "Advanced", type: "Isolation" },
    { name: "Stiff Leg Deadlift", equipment: "Dumbbells", difficulty: "Intermediate", type: "Compound" },
  ],
  "lower-back": [
    { name: "Deadlift", equipment: "Barbell", difficulty: "Intermediate", type: "Compound" },
    { name: "Back Extension", equipment: "Machine", difficulty: "Beginner", type: "Isolation" },
    { name: "Good Mornings", equipment: "Barbell", difficulty: "Advanced", type: "Compound" },
    { name: "Superman", equipment: "Bodyweight", difficulty: "Beginner", type: "Isolation" },
  ],
}

const muscleNames: Record<string, string> = {
  chest: "Chest",
  shoulders: "Shoulders",
  biceps: "Biceps",
  forearms: "Forearms",
  abs: "Abdominals",
  quads: "Quadriceps",
  calves: "Calves",
  traps: "Trapezius",
  lats: "Latissimus Dorsi",
  triceps: "Triceps",
  glutes: "Glutes",
  hamstrings: "Hamstrings",
  "lower-back": "Lower Back",
}

const categories = ["All", "Favourite", "Barbell", "Dumbbell", "Cable", "Machine", "Bodyweight"]
const types = ["All", "Compound", "Isolation"]
const difficulties = ["All", "Beginner", "Intermediate", "Advanced"]

export function ExerciseList({ muscle, onBack, onSelectExercise }: ExerciseListProps) {
  const [favorites, setFavorites] = useState<string[]>([])
  const [categoryFilter, setCategoryFilter] = useState("All")
  const [typeFilter, setTypeFilter] = useState("All")
  const [difficultyFilter, setDifficultyFilter] = useState("All")

  const exercises = muscle ? exerciseData[muscle] || [] : []
  const muscleName = muscle ? muscleNames[muscle] : ""

  const toggleFavorite = (exerciseName: string, e: React.MouseEvent) => {
    e.stopPropagation()
    setFavorites((prev) =>
      prev.includes(exerciseName) ? prev.filter((n) => n !== exerciseName) : [...prev, exerciseName],
    )
  }

  const filteredExercises = exercises.filter((ex) => {
    if (categoryFilter !== "All" && ex.equipment !== categoryFilter) return false
    if (typeFilter !== "All" && ex.type !== typeFilter) return false
    if (difficultyFilter !== "All" && ex.difficulty !== difficultyFilter) return false
    return true
  })

  return (
    <div className="flex-1 flex flex-col pb-16">
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-semibold flex-1">{muscleName}</h1>
        <button className="p-2 hover:bg-muted rounded-full transition-colors">
          <Search className="w-5 h-5" />
        </button>
      </div>

      <div className="flex gap-2 px-4 py-3 overflow-x-auto border-b border-border">
        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center gap-1 px-3 py-1.5 rounded-full bg-muted text-sm font-medium whitespace-nowrap">
            {categoryFilter === "All" ? "Category" : categoryFilter}
            <ChevronDown className="w-3.5 h-3.5" />
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            {categories.map((cat) => (
              <DropdownMenuItem key={cat} onClick={() => setCategoryFilter(cat)}>
                {cat}
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>

        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center gap-1 px-3 py-1.5 rounded-full bg-muted text-sm font-medium whitespace-nowrap">
            {typeFilter === "All" ? "Type" : typeFilter}
            <ChevronDown className="w-3.5 h-3.5" />
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            {types.map((type) => (
              <DropdownMenuItem key={type} onClick={() => setTypeFilter(type)}>
                {type}
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>

        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center gap-1 px-3 py-1.5 rounded-full bg-muted text-sm font-medium whitespace-nowrap">
            {difficultyFilter === "All" ? "Difficulty" : difficultyFilter}
            <ChevronDown className="w-3.5 h-3.5" />
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            {difficulties.map((diff) => (
              <DropdownMenuItem key={diff} onClick={() => setDifficultyFilter(diff)}>
                {diff}
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      {/* Exercise list */}
      <div className="flex-1 overflow-y-auto">
        {filteredExercises.map((exercise, index) => (
          <div
            key={index}
            onClick={() => onSelectExercise(exercise)}
            className="flex items-center gap-4 px-4 py-3 border-b border-border hover:bg-muted/50 transition-colors cursor-pointer"
          >
            <button
              onClick={(e) => toggleFavorite(exercise.name, e)}
              className="p-1.5 hover:bg-muted rounded-full transition-colors"
            >
              <Star
                className={cn(
                  "w-5 h-5 transition-colors",
                  favorites.includes(exercise.name) ? "text-yellow-500 fill-yellow-500" : "text-muted-foreground",
                )}
              />
            </button>

            {/* Info */}
            <div className="flex-1 min-w-0">
              <h3 className="font-medium text-foreground truncate">{exercise.name}</h3>
              <p className="text-sm text-muted-foreground">
                {exercise.equipment} • {exercise.type}
              </p>
            </div>

            {/* Difficulty badge */}
            <span
              className={cn(
                "px-2 py-0.5 rounded-full text-xs font-medium",
                exercise.difficulty === "Beginner" && "bg-green-500/20 text-green-600",
                exercise.difficulty === "Intermediate" && "bg-yellow-500/20 text-yellow-600",
                exercise.difficulty === "Advanced" && "bg-red-500/20 text-red-600",
              )}
            >
              {exercise.difficulty}
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}
