"use client"

import { ChevronRight, User, Ruler, Scale, Target, Settings } from "lucide-react"

interface ProfileScreenProps {
  onNavigate?: (screen: string) => void
}

const stats = {
  workouts: 156,
  hours: 89,
  streak: 12,
  level: 24,
}

const personalInfoItems = [
  { icon: User, label: "Name", value: "John Doe" },
  { icon: Target, label: "Fitness Goals", value: "Build Muscle" },
  { icon: Ruler, label: "Height", value: "5'10\"" },
  { icon: Scale, label: "Weight", value: "175 lbs" },
]

export function ProfileScreen({ onNavigate }: ProfileScreenProps) {
  return (
    <div className="flex-1 flex flex-col bg-background overflow-y-auto pb-20">
      {/* Profile header */}
      <div className="px-4 pt-6 pb-4">
        <div className="flex items-center gap-4">
          <div className="w-20 h-20 rounded-full bg-gradient-to-br from-primary to-primary/50 flex items-center justify-center">
            <User className="w-10 h-10 text-primary-foreground" />
          </div>
          <div>
            <h1 className="text-xl font-bold">John Doe</h1>
            <p className="text-sm text-muted-foreground">Level {stats.level} Athlete</p>
          </div>
        </div>
      </div>

      {/* Stats */}
      <div className="px-4 pb-4">
        <div className="bg-muted rounded-xl p-4">
          <div className="grid grid-cols-4 gap-2 text-center">
            <div>
              <p className="text-xl font-bold">{stats.workouts}</p>
              <p className="text-xs text-muted-foreground">Workouts</p>
            </div>
            <div>
              <p className="text-xl font-bold">{stats.hours}</p>
              <p className="text-xs text-muted-foreground">Hours</p>
            </div>
            <div>
              <p className="text-xl font-bold">{stats.streak}</p>
              <p className="text-xs text-muted-foreground">Streak</p>
            </div>
            <div>
              <p className="text-xl font-bold">{stats.level}</p>
              <p className="text-xs text-muted-foreground">Level</p>
            </div>
          </div>
          {/* Progress bar */}
          <div className="mt-4">
            <div className="flex items-center justify-between text-xs mb-1">
              <span className="text-muted-foreground">Level Progress</span>
              <span>750 / 1000 XP</span>
            </div>
            <div className="h-2 bg-background rounded-full overflow-hidden">
              <div className="h-full w-3/4 bg-primary rounded-full" />
            </div>
          </div>
        </div>
      </div>

      <div className="px-4 pb-4">
        <div className="flex items-center justify-between mb-2">
          <h3 className="font-semibold">Personal Info</h3>
          <button onClick={() => onNavigate?.("personal-info")} className="text-sm text-primary">
            Edit
          </button>
        </div>
        <div className="bg-muted rounded-xl overflow-hidden">
          {personalInfoItems.map((item, index) => (
            <div key={index} className="flex items-center gap-3 p-4 border-b border-border last:border-0">
              <item.icon className="w-5 h-5 text-muted-foreground" />
              <span className="flex-1 text-left">{item.label}</span>
              <span className="text-sm text-muted-foreground">{item.value}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Settings link */}
      <div className="px-4 pb-4">
        <button
          onClick={() => onNavigate?.("settings")}
          className="w-full bg-muted rounded-xl p-4 flex items-center gap-3 hover:bg-muted/80 transition-colors"
        >
          <Settings className="w-5 h-5 text-muted-foreground" />
          <span className="flex-1 text-left font-medium">Settings</span>
          <ChevronRight className="w-5 h-5 text-muted-foreground" />
        </button>
      </div>
    </div>
  )
}
