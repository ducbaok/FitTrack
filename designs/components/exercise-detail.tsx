"use client"

import { ArrowLeft, Play, Star, FileText, CheckCircle2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useState } from "react"
import { cn } from "@/lib/utils"

interface ExerciseDetailProps {
  exercise: {
    name: string
    equipment: string
    difficulty: string
  }
  muscle: string
  onBack: () => void
}

export function ExerciseDetail({ exercise, muscle, onBack }: ExerciseDetailProps) {
  const [isFavorite, setIsFavorite] = useState(false)

  const instructions = [
    "Position yourself on the equipment with proper form",
    "Keep your core engaged throughout the movement",
    "Control the weight on both the lifting and lowering phase",
    "Breathe out during exertion, breathe in during the eccentric phase",
    "Complete the full range of motion for maximum effectiveness",
  ]

  const tips = [
    "Start with lighter weight to master the form",
    "Focus on mind-muscle connection",
    "Don't rush the movement",
  ]

  const fatigueLevel = 65

  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <div className="flex-1">
          <h1 className="text-lg font-semibold">{exercise.name}</h1>
          <p className="text-sm text-muted-foreground capitalize">{muscle}</p>
        </div>
        <button
          onClick={() => setIsFavorite(!isFavorite)}
          className="p-2 hover:bg-muted rounded-full transition-colors"
        >
          <Star className={cn("w-5 h-5", isFavorite ? "text-yellow-500 fill-yellow-500" : "text-muted-foreground")} />
        </button>
        <button className="p-2 hover:bg-muted rounded-full transition-colors">
          <FileText className="w-5 h-5" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto pb-20">
        {/* Video thumbnail */}
        <div className="relative aspect-video bg-muted">
          <div className="absolute inset-0 bg-gradient-to-br from-primary/20 to-primary/5 flex items-center justify-center">
            <button className="w-16 h-16 rounded-full bg-primary flex items-center justify-center shadow-lg">
              <Play className="w-7 h-7 text-primary-foreground ml-1" fill="currentColor" />
            </button>
          </div>
          <div className="absolute bottom-3 right-3 px-2 py-1 bg-black/70 rounded text-xs text-white">0:45</div>
        </div>

        {/* Exercise info */}
        <div className="p-4 space-y-6">
          <div className="flex flex-wrap gap-2">
            <span className="px-3 py-1 bg-muted rounded-full text-sm font-medium">{exercise.equipment}</span>
            <span
              className={cn(
                "px-3 py-1 rounded-full text-sm font-medium",
                exercise.difficulty === "Beginner" && "bg-green-500/20 text-green-600",
                exercise.difficulty === "Intermediate" && "bg-yellow-500/20 text-yellow-600",
                exercise.difficulty === "Advanced" && "bg-red-500/20 text-red-600",
              )}
            >
              {exercise.difficulty}
            </span>
            <span className="px-3 py-1 bg-muted rounded-full text-sm font-medium capitalize">{muscle}</span>
          </div>

          {/* Muscles worked visualization */}
          <div className="bg-muted rounded-xl p-4">
            <h3 className="font-semibold mb-3">Muscles Worked</h3>
            <div className="flex items-start gap-4">
              <div className="flex gap-2">
                <div className="w-16 h-24 bg-background rounded-lg flex items-center justify-center border-2 border-dashed border-muted-foreground/30">
                  <span className="text-[10px] text-muted-foreground text-center px-1">Front</span>
                </div>
                <div className="w-16 h-24 bg-background rounded-lg flex items-center justify-center border-2 border-dashed border-muted-foreground/30">
                  <span className="text-[10px] text-muted-foreground text-center px-1">Back</span>
                </div>
              </div>
              <div className="flex-1 space-y-2">
                <div className="flex items-center gap-2">
                  <span className="w-3 h-3 rounded-full bg-red-500" />
                  <span className="text-sm">
                    Primary: <span className="capitalize">{muscle}</span>
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="w-3 h-3 rounded-full bg-orange-400" />
                  <span className="text-sm">Secondary: Triceps</span>
                </div>
                <div className="flex items-center gap-2">
                  <span className="w-3 h-3 rounded-full bg-yellow-400" />
                  <span className="text-sm">Stabilizers: Core</span>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-muted rounded-xl p-4">
            <div className="flex items-center justify-between mb-2">
              <h3 className="font-semibold">Fatigue Level</h3>
              <span className="text-sm text-muted-foreground">{fatigueLevel}%</span>
            </div>
            <div className="h-3 rounded-full overflow-hidden bg-background">
              <div
                className="h-full rounded-full transition-all duration-300"
                style={{
                  width: `${fatigueLevel}%`,
                  background: `linear-gradient(to right, #9ca3af, #dc2626)`,
                }}
              />
            </div>
            <p className="text-xs text-muted-foreground mt-2">Higher fatigue means more recovery time needed</p>
          </div>

          {/* Instructions */}
          <div>
            <h3 className="font-semibold mb-3">Instructions</h3>
            <div className="space-y-3">
              {instructions.map((instruction, index) => (
                <div key={index} className="flex gap-3">
                  <span className="w-6 h-6 rounded-full bg-primary/20 text-primary flex items-center justify-center text-sm font-medium shrink-0">
                    {index + 1}
                  </span>
                  <p className="text-sm text-muted-foreground leading-relaxed">{instruction}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Tips */}
          <div className="bg-primary/10 rounded-xl p-4">
            <h3 className="font-semibold mb-3 text-primary">Pro Tips</h3>
            <div className="space-y-2">
              {tips.map((tip, index) => (
                <div key={index} className="flex items-start gap-2">
                  <CheckCircle2 className="w-4 h-4 text-primary mt-0.5 shrink-0" />
                  <p className="text-sm">{tip}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Bottom action */}
      <div className="fixed bottom-0 left-0 right-0 max-w-md mx-auto p-4 border-t border-border bg-background">
        <Button className="w-full">Add to Workout</Button>
      </div>
    </div>
  )
}
