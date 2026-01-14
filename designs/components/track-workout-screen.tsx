"use client"

import { ArrowLeft, TrendingUp, Flame, Dumbbell, Clock, Calendar } from "lucide-react"
import { useState } from "react"

interface TrackWorkoutScreenProps {
  onBack: () => void
}

export function TrackWorkoutScreen({ onBack }: TrackWorkoutScreenProps) {
  const [period, setPeriod] = useState<"daily" | "weekly" | "monthly" | "yearly">("weekly")

  const stats = {
    daily: { workouts: 1, duration: "47 min", volume: "12,450 lbs", calories: 320 },
    weekly: { workouts: 4, duration: "3h 12min", volume: "55,750 lbs", calories: 1280 },
    monthly: { workouts: 16, duration: "12h 45min", volume: "223,000 lbs", calories: 5120 },
    yearly: { workouts: 156, duration: "130h", volume: "2.1M lbs", calories: 52000 },
  }

  const currentStats = stats[period]

  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-semibold flex-1">Track Workout</h1>
        <button className="p-2 hover:bg-muted rounded-full transition-colors">
          <Calendar className="w-5 h-5" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-4 pb-20 space-y-4">
        {/* Period selector */}
        <div className="flex gap-1 bg-muted p-1 rounded-lg">
          {(["daily", "weekly", "monthly", "yearly"] as const).map((p) => (
            <button
              key={p}
              onClick={() => setPeriod(p)}
              className={`flex-1 py-2 rounded-md text-xs font-medium transition-colors capitalize ${
                period === p ? "bg-primary text-primary-foreground" : "text-muted-foreground"
              }`}
            >
              {p}
            </button>
          ))}
        </div>

        {/* Stats grid */}
        <div className="grid grid-cols-2 gap-3">
          <div className="bg-muted rounded-xl p-4 text-center">
            <Dumbbell className="w-6 h-6 mx-auto mb-2 text-primary" />
            <p className="text-2xl font-bold">{currentStats.workouts}</p>
            <p className="text-xs text-muted-foreground">Workouts</p>
          </div>
          <div className="bg-muted rounded-xl p-4 text-center">
            <Clock className="w-6 h-6 mx-auto mb-2 text-blue-500" />
            <p className="text-2xl font-bold">{currentStats.duration}</p>
            <p className="text-xs text-muted-foreground">Duration</p>
          </div>
          <div className="bg-muted rounded-xl p-4 text-center">
            <TrendingUp className="w-6 h-6 mx-auto mb-2 text-green-500" />
            <p className="text-2xl font-bold">{currentStats.volume}</p>
            <p className="text-xs text-muted-foreground">Volume</p>
          </div>
          <div className="bg-muted rounded-xl p-4 text-center">
            <Flame className="w-6 h-6 mx-auto mb-2 text-orange-500" />
            <p className="text-2xl font-bold">{currentStats.calories}</p>
            <p className="text-xs text-muted-foreground">Calories</p>
          </div>
        </div>

        {/* Progress chart placeholder */}
        <div className="bg-muted rounded-xl p-4">
          <h3 className="font-semibold mb-3">Progress</h3>
          <div className="h-40 flex items-end justify-around gap-2">
            {[40, 65, 45, 80, 55, 70, 60].map((height, i) => (
              <div key={i} className="flex-1 flex flex-col items-center gap-1">
                <div
                  className="w-full bg-primary rounded-t transition-all duration-300"
                  style={{ height: `${height}%` }}
                />
                <span className="text-[10px] text-muted-foreground">{["M", "T", "W", "T", "F", "S", "S"][i]}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
