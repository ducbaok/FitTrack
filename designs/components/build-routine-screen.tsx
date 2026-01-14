"use client"

import { ArrowLeft, ChevronDown } from "lucide-react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"

interface BuildRoutineScreenProps {
  onBack: () => void
}

const daysOfWeek = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
const availableWorkouts = ["Push Day", "Pull Day", "Leg Day", "Upper Body", "Lower Body", "Full Body", "Rest"]

export function BuildRoutineScreen({ onBack }: BuildRoutineScreenProps) {
  const [routineName, setRoutineName] = useState("")
  const [scheduleType, setScheduleType] = useState<"weekly" | "monthly">("weekly")
  const [weeklySchedule, setWeeklySchedule] = useState<Record<string, string>>({})

  const assignWorkout = (day: string, workout: string) => {
    setWeeklySchedule((prev) => ({ ...prev, [day]: workout }))
  }

  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-semibold flex-1">Build Routine</h1>
      </div>

      <div className="flex-1 overflow-y-auto p-4 pb-24 space-y-4">
        {/* Routine Name */}
        <Input
          placeholder="Routine Name"
          value={routineName}
          onChange={(e) => setRoutineName(e.target.value)}
          className="text-lg font-medium"
        />

        {/* Schedule Type Toggle */}
        <div className="flex gap-2">
          <button
            onClick={() => setScheduleType("weekly")}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition-colors ${
              scheduleType === "weekly" ? "bg-primary text-primary-foreground" : "bg-muted"
            }`}
          >
            Weekly
          </button>
          <button
            onClick={() => setScheduleType("monthly")}
            className={`flex-1 py-2 rounded-lg text-sm font-medium transition-colors ${
              scheduleType === "monthly" ? "bg-primary text-primary-foreground" : "bg-muted"
            }`}
          >
            Monthly
          </button>
        </div>

        {/* Weekly Schedule */}
        {scheduleType === "weekly" && (
          <div className="space-y-2">
            <h3 className="font-semibold">Weekly Schedule</h3>
            {daysOfWeek.map((day) => (
              <div key={day} className="flex items-center justify-between bg-muted rounded-xl p-3">
                <span className="font-medium text-sm">{day}</span>
                <DropdownMenu>
                  <DropdownMenuTrigger className="flex items-center gap-1.5 px-3 py-1.5 bg-background rounded-lg text-sm">
                    {weeklySchedule[day] || "Select Workout"}
                    <ChevronDown className="w-3.5 h-3.5" />
                  </DropdownMenuTrigger>
                  <DropdownMenuContent>
                    {availableWorkouts.map((workout) => (
                      <DropdownMenuItem key={workout} onClick={() => assignWorkout(day, workout)}>
                        {workout}
                      </DropdownMenuItem>
                    ))}
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>
            ))}
          </div>
        )}

        {/* Monthly placeholder */}
        {scheduleType === "monthly" && (
          <div className="bg-muted rounded-xl p-6 text-center">
            <p className="text-muted-foreground text-sm">Monthly schedule coming soon</p>
          </div>
        )}
      </div>

      {/* Save button */}
      <div className="fixed bottom-0 left-0 right-0 max-w-md mx-auto p-4 border-t border-border bg-background">
        <Button className="w-full" disabled={!routineName}>
          Save Routine
        </Button>
      </div>
    </div>
  )
}
