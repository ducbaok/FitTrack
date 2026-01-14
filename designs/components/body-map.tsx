"use client"

import type { MuscleGroup, ViewMode } from "./muscle-wiki-app"
import { useState } from "react"

interface BodyMapProps {
  viewMode: ViewMode
  setViewMode: (mode: ViewMode) => void
  onSelectMuscle: (muscle: MuscleGroup) => void
}

function ToggleSwitch({
  options,
  value,
  onChange,
  size = "default",
}: {
  options: [string, string]
  value: string
  onChange: (value: string) => void
  size?: "default" | "small"
}) {
  const isSecondOption = value === options[1].toLowerCase()
  const isSmall = size === "small"

  return (
    <div className={`relative flex bg-muted rounded-full ${isSmall ? "p-0.5" : "p-1"}`}>
      <div
        className={`absolute top-0.5 bottom-0.5 w-[calc(50%-2px)] bg-primary rounded-full transition-transform duration-300 ease-out ${
          isSecondOption ? "translate-x-[calc(100%+2px)]" : "translate-x-0"
        }`}
      />
      {options.map((option) => (
        <button
          key={option}
          onClick={() => onChange(option.toLowerCase())}
          className={`relative z-10 ${isSmall ? "px-3 py-1 text-xs" : "px-4 py-1.5 text-sm"} font-medium rounded-full transition-colors duration-300 ${
            value === option.toLowerCase() ? "text-primary-foreground" : "text-muted-foreground hover:text-foreground"
          }`}
        >
          {option}
        </button>
      ))}
    </div>
  )
}

export function BodyMap({ viewMode, setViewMode, onSelectMuscle }: BodyMapProps) {
  const [gender, setGender] = useState<"male" | "female">("male")
  const [detailLevel, setDetailLevel] = useState<"basic" | "advanced">("basic")

  return (
    <div className="flex-1 flex flex-col pb-16">
      <div className="flex justify-center items-center gap-2 py-3 px-4">
        <ToggleSwitch
          options={["Male", "Female"]}
          value={gender}
          onChange={(value) => setGender(value as "male" | "female")}
          size="small"
        />
        <ToggleSwitch
          options={["Front", "Back"]}
          value={viewMode}
          onChange={(value) => setViewMode(value as ViewMode)}
          size="small"
        />
        <ToggleSwitch
          options={["Basic", "Advanced"]}
          value={detailLevel}
          onChange={(value) => setDetailLevel(value as "basic" | "advanced")}
          size="small"
        />
      </div>

      {/* Body visualization - placeholder until drawings complete */}
      <div className="flex-1 flex items-center justify-center px-4 pb-4">
        <div className="relative w-full max-w-[280px] aspect-[319/576]">
          <div className="absolute inset-0 flex items-center justify-center border-2 border-dashed border-muted-foreground/30 rounded-xl bg-muted/20">
            <div className="text-center p-4">
              <div className="w-16 h-16 mx-auto mb-3 rounded-full bg-muted flex items-center justify-center">
                <svg viewBox="0 0 24 24" className="w-8 h-8 text-muted-foreground">
                  <path
                    fill="currentColor"
                    d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"
                  />
                </svg>
              </div>
              <p className="text-sm text-muted-foreground font-medium">{gender === "male" ? "Male" : "Female"} Body</p>
              <p className="text-xs text-muted-foreground mt-1">
                {viewMode === "front" ? "Front" : "Back"} View ({detailLevel})
              </p>
              <p className="text-xs text-muted-foreground/60 mt-2">Drawing in progress...</p>
            </div>
          </div>
        </div>
      </div>

      <p className="text-center text-muted-foreground text-sm pb-4">Tap a muscle to see exercises</p>
    </div>
  )
}
