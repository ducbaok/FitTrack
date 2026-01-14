"use client"

import type React from "react"

import { Search, Star, ChevronDown } from "lucide-react"
import { useState } from "react"
import { Input } from "@/components/ui/input"
import { cn } from "@/lib/utils"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"

interface ExploreScreenProps {
  onSelectExercise: (exercise: any) => void
}

const allExercises = [
  { name: "Bench Press", equipment: "Barbell", muscle: "Chest", difficulty: "Intermediate", type: "Compound" },
  { name: "Squats", equipment: "Barbell", muscle: "Quads", difficulty: "Intermediate", type: "Compound" },
  { name: "Deadlift", equipment: "Barbell", muscle: "Back", difficulty: "Advanced", type: "Compound" },
  { name: "Overhead Press", equipment: "Barbell", muscle: "Shoulders", difficulty: "Intermediate", type: "Compound" },
  { name: "Pull-ups", equipment: "Bodyweight", muscle: "Back", difficulty: "Intermediate", type: "Compound" },
  { name: "Push-ups", equipment: "Bodyweight", muscle: "Chest", difficulty: "Beginner", type: "Compound" },
  { name: "Dumbbell Curl", equipment: "Dumbbell", muscle: "Biceps", difficulty: "Beginner", type: "Isolation" },
  { name: "Tricep Pushdown", equipment: "Cable", muscle: "Triceps", difficulty: "Beginner", type: "Isolation" },
  { name: "Leg Press", equipment: "Machine", muscle: "Quads", difficulty: "Beginner", type: "Compound" },
  { name: "Lateral Raises", equipment: "Dumbbell", muscle: "Shoulders", difficulty: "Beginner", type: "Isolation" },
  { name: "Cable Flyes", equipment: "Cable", muscle: "Chest", difficulty: "Beginner", type: "Isolation" },
  { name: "Leg Curl", equipment: "Machine", muscle: "Hamstrings", difficulty: "Beginner", type: "Isolation" },
]

const categories = ["All", "Barbell", "Dumbbell", "Cable", "Machine", "Bodyweight"]
const muscles = ["All", "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Quads", "Hamstrings"]

export function ExploreScreen({ onSelectExercise }: ExploreScreenProps) {
  const [searchQuery, setSearchQuery] = useState("")
  const [favorites, setFavorites] = useState<string[]>([])
  const [categoryFilter, setCategoryFilter] = useState("All")
  const [muscleFilter, setMuscleFilter] = useState("All")

  const toggleFavorite = (name: string, e: React.MouseEvent) => {
    e.stopPropagation()
    setFavorites((prev) => (prev.includes(name) ? prev.filter((n) => n !== name) : [...prev, name]))
  }

  const filteredExercises = allExercises.filter((ex) => {
    if (searchQuery && !ex.name.toLowerCase().includes(searchQuery.toLowerCase())) return false
    if (categoryFilter !== "All" && ex.equipment !== categoryFilter) return false
    if (muscleFilter !== "All" && ex.muscle !== muscleFilter) return false
    return true
  })

  return (
    <div className="flex-1 flex flex-col bg-background pb-16">
      {/* Header */}
      <div className="px-4 py-4 border-b border-border">
        <h1 className="text-2xl font-bold mb-3">Explore</h1>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            placeholder="Search exercises..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* Filters */}
      <div className="flex gap-2 px-4 py-3 border-b border-border">
        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center gap-1 px-3 py-1.5 rounded-full bg-muted text-sm font-medium">
            {categoryFilter === "All" ? "Equipment" : categoryFilter}
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
          <DropdownMenuTrigger className="flex items-center gap-1 px-3 py-1.5 rounded-full bg-muted text-sm font-medium">
            {muscleFilter === "All" ? "Muscle" : muscleFilter}
            <ChevronDown className="w-3.5 h-3.5" />
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            {muscles.map((muscle) => (
              <DropdownMenuItem key={muscle} onClick={() => setMuscleFilter(muscle)}>
                {muscle}
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

            <div className="flex-1 min-w-0">
              <h3 className="font-medium text-foreground truncate">{exercise.name}</h3>
              <p className="text-sm text-muted-foreground">
                {exercise.muscle} • {exercise.equipment}
              </p>
            </div>

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
