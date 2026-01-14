"use client"

import { ArrowLeft, User, Ruler, Scale, Target, Calendar, Mail, Phone } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"

interface PersonalInfoScreenProps {
  onBack: () => void
}

export function PersonalInfoScreen({ onBack }: PersonalInfoScreenProps) {
  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-semibold flex-1">Personal Info</h1>
      </div>

      <div className="flex-1 overflow-y-auto p-4 pb-24 space-y-4">
        {/* Profile photo */}
        <div className="flex flex-col items-center py-4">
          <div className="w-24 h-24 rounded-full bg-gradient-to-br from-primary to-primary/50 flex items-center justify-center mb-3">
            <User className="w-12 h-12 text-primary-foreground" />
          </div>
          <button className="text-sm text-primary font-medium">Change Photo</button>
        </div>

        {/* Form fields */}
        <div className="space-y-4">
          <div>
            <label className="text-sm text-muted-foreground mb-1.5 block">Full Name</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input defaultValue="John Doe" className="pl-10" />
            </div>
          </div>

          <div>
            <label className="text-sm text-muted-foreground mb-1.5 block">Email</label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input defaultValue="john@example.com" className="pl-10" />
            </div>
          </div>

          <div>
            <label className="text-sm text-muted-foreground mb-1.5 block">Phone</label>
            <div className="relative">
              <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input defaultValue="+1 234 567 8900" className="pl-10" />
            </div>
          </div>

          <div>
            <label className="text-sm text-muted-foreground mb-1.5 block">Date of Birth</label>
            <div className="relative">
              <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input defaultValue="1990-01-15" type="date" className="pl-10" />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-sm text-muted-foreground mb-1.5 block">Height</label>
              <div className="relative">
                <Ruler className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input defaultValue="178 cm" className="pl-10" />
              </div>
            </div>
            <div>
              <label className="text-sm text-muted-foreground mb-1.5 block">Weight</label>
              <div className="relative">
                <Scale className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input defaultValue="175 lbs" className="pl-10" />
              </div>
            </div>
          </div>

          <div>
            <label className="text-sm text-muted-foreground mb-1.5 block">Fitness Goal</label>
            <div className="relative">
              <Target className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input defaultValue="Build Muscle" className="pl-10" />
            </div>
          </div>
        </div>
      </div>

      {/* Save button */}
      <div className="fixed bottom-0 left-0 right-0 max-w-md mx-auto p-4 border-t border-border bg-background">
        <Button className="w-full">Save Changes</Button>
      </div>
    </div>
  )
}
