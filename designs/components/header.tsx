"use client"

import { ChevronDown } from "lucide-react"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"

interface HeaderProps {
  username?: string
  category: string
  onCategoryChange: (category: string) => void
}

const categories = ["Favourite", "Featured", "Bodyweight", "Barbell", "Dumbbell", "Machine", "Cable"]

export function Header({ username = "John", category, onCategoryChange }: HeaderProps) {
  return (
    <header className="flex items-center justify-between px-4 py-3 border-b border-border">
      <div className="flex flex-col">
        <span className="text-muted-foreground text-sm">Hello,</span>
        <span className="font-semibold text-lg text-foreground">{username}</span>
      </div>

      <DropdownMenu>
        <DropdownMenuTrigger className="flex items-center gap-1.5 px-3 py-1.5 bg-muted rounded-full text-sm font-medium">
          {category}
          <ChevronDown className="w-4 h-4" />
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          {categories.map((cat) => (
            <DropdownMenuItem
              key={cat}
              onClick={() => onCategoryChange(cat)}
              className={category === cat ? "bg-primary/10 text-primary" : ""}
            >
              {cat}
            </DropdownMenuItem>
          ))}
        </DropdownMenuContent>
      </DropdownMenu>
    </header>
  )
}
