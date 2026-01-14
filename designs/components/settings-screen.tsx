"use client"

import { ArrowLeft, Bell, Moon, Globe, Lock, HelpCircle, FileText, LogOut } from "lucide-react"
import { Switch } from "@/components/ui/switch"

interface SettingsScreenProps {
  onBack: () => void
}

export function SettingsScreen({ onBack }: SettingsScreenProps) {
  return (
    <div className="flex-1 flex flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 border-b border-border">
        <button onClick={onBack} className="p-2 hover:bg-muted rounded-full transition-colors">
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-xl font-semibold">Settings</h1>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {/* Preferences */}
        <div>
          <h3 className="font-semibold mb-2 text-sm text-muted-foreground uppercase">Preferences</h3>
          <div className="bg-muted rounded-xl overflow-hidden">
            <div className="flex items-center gap-3 p-4 border-b border-border">
              <Bell className="w-5 h-5 text-muted-foreground" />
              <span className="flex-1">Notifications</span>
              <Switch defaultChecked />
            </div>
            <div className="flex items-center gap-3 p-4 border-b border-border">
              <Moon className="w-5 h-5 text-muted-foreground" />
              <span className="flex-1">Dark Mode</span>
              <Switch defaultChecked />
            </div>
            <div className="flex items-center gap-3 p-4">
              <Globe className="w-5 h-5 text-muted-foreground" />
              <span className="flex-1">Language</span>
              <span className="text-sm text-muted-foreground">English</span>
            </div>
          </div>
        </div>

        {/* Security */}
        <div>
          <h3 className="font-semibold mb-2 text-sm text-muted-foreground uppercase">Security</h3>
          <div className="bg-muted rounded-xl overflow-hidden">
            <button className="w-full flex items-center gap-3 p-4 hover:bg-background/50 transition-colors">
              <Lock className="w-5 h-5 text-muted-foreground" />
              <span className="flex-1 text-left">Change Password</span>
            </button>
          </div>
        </div>

        {/* Support */}
        <div>
          <h3 className="font-semibold mb-2 text-sm text-muted-foreground uppercase">Support</h3>
          <div className="bg-muted rounded-xl overflow-hidden">
            <button className="w-full flex items-center gap-3 p-4 border-b border-border hover:bg-background/50 transition-colors">
              <HelpCircle className="w-5 h-5 text-muted-foreground" />
              <span className="flex-1 text-left">Help & Support</span>
            </button>
            <button className="w-full flex items-center gap-3 p-4 hover:bg-background/50 transition-colors">
              <FileText className="w-5 h-5 text-muted-foreground" />
              <span className="flex-1 text-left">Terms & Privacy</span>
            </button>
          </div>
        </div>

        {/* Logout */}
        <button className="w-full flex items-center justify-center gap-2 p-4 bg-red-500/10 text-red-500 rounded-xl hover:bg-red-500/20 transition-colors">
          <LogOut className="w-5 h-5" />
          <span className="font-medium">Log Out</span>
        </button>
      </div>
    </div>
  )
}
