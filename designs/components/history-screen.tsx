"use client"

import { ArrowLeft, Calendar, ChevronRight, Dumbbell, Clock, TrendingUp } from "lucide-react"
import { useState } from "react"

interface HistoryScreenProps {
  onBack: () => void
}

const historyData = [
  {
    date: "Today",
    workouts: [
      { name: "Bench Press", sets: 4, reps: "8-10", weight: "185 lbs", duration: "12 min" },
      { name: "Incline Press", sets: 3, reps: "10-12", weight: "135 lbs", duration: "8 min" },
    ],
  },
  {
    date: "Yesterday",
    workouts: [
      { name: "Squats", sets: 5, reps: "5", weight: "225 lbs", duration: "15 min" },
      { name: "Leg Press", sets: 4, reps: "12", weight: "360 lbs", duration: "10 min" },
    ],
  },
  {
    date: "Jan 8, 2026",
    workouts: [
      { name: "Pull-ups", sets: 4, reps: "8-10", weight: "BW", duration: "8 min" },
      { name: "Barbell Row", sets: 4, reps: "8", weight: "155 lbs", duration: "10 min" },
    ],
  },
]

export function HistoryScreen({ onBack }: HistoryScreenProps) {
  const [filterType, setFilterType] = useState<"day" | "range">("day")

  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-semibold flex-1">History</h1>
        <button className="p-2 hover:bg-muted rounded-full transition-colors">
          <Calendar className="w-5 h-5" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-4 pb-20 space-y-4">
        {/* Filter toggle */}
        <div className="flex gap-2">
          <button
            onClick={() => setFilterType("day")}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition-colors ${
              filterType === "day" ? "bg-primary text-primary-foreground" : "bg-muted"
            }`}
          >
            By Day
          </button>
          <button
            onClick={() => setFilterType("range")}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition-colors ${
              filterType === "range" ? "bg-primary text-primary-foreground" : "bg-muted"
            }`}
          >
            Date Range
          </button>
        </div>

        {/* History list */}
        <div className="space-y-4">
          {historyData.map((day, dayIndex) => (
            <div key={dayIndex}>
              <p className="text-sm font-medium text-muted-foreground mb-2">{day.date}</p>
              <div className="space-y-2">
                {day.workouts.map((exercise, exIndex) => (
                  <button
                    key={exIndex}
                    className="w-full bg-muted rounded-xl p-4 text-left hover:bg-muted/80 transition-colors"
                  >
                    <div className="flex items-center justify-between mb-2">
                      <h4 className="font-semibold">{exercise.name}</h4>
                      <ChevronRight className="w-4 h-4 text-muted-foreground" />
                    </div>
                    <div className="flex items-center gap-4 text-sm text-muted-foreground">
                      <span className="flex items-center gap-1">
                        <Dumbbell className="w-3.5 h-3.5" />
                        {exercise.sets} x {exercise.reps}
                      </span>
                      <span className="flex items-center gap-1">
                        <TrendingUp className="w-3.5 h-3.5" />
                        {exercise.weight}
                      </span>
                      <span className="flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5" />
                        {exercise.duration}
                      </span>
                    </div>
                  </button>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
