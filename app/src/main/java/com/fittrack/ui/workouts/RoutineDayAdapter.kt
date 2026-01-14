package com.fittrack.ui.workouts

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.R
import com.fittrack.data.local.entity.WorkoutTemplateEntity
import com.fittrack.databinding.ItemRoutineDayBinding

/**
 * Adapter for routine days in Build Routine screen.
 */
class RoutineDayAdapter(
    private var templates: List<WorkoutTemplateEntity> = emptyList(),
    private val onTemplateSelected: (Int, WorkoutTemplateEntity?) -> Unit,
    private val onClear: (Int) -> Unit
) : ListAdapter<RoutineDayDisplay, RoutineDayAdapter.ViewHolder>(DiffCallback) {

    fun updateTemplates(newTemplates: List<WorkoutTemplateEntity>) {
        templates = newTemplates
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoutineDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, templates, onTemplateSelected, onClear)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.templates = templates
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemRoutineDayBinding,
        var templates: List<WorkoutTemplateEntity>,
        private val onTemplateSelected: (Int, WorkoutTemplateEntity?) -> Unit,
        private val onClear: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: RoutineDayDisplay) {
            binding.tvDayName.text = day.dayName
            
            if (day.selectedTemplate != null) {
                binding.tvWorkoutName.text = day.selectedTemplate.name
                binding.tvWorkoutName.setTextColor(
                    binding.root.context.getColor(R.color.colorOnSurface)
                )
                binding.btnClear.isVisible = true
            } else {
                binding.tvWorkoutName.text = "Rest Day"
                binding.tvWorkoutName.setTextColor(
                    binding.root.context.getColor(R.color.colorMutedForeground)
                )
                binding.btnClear.isVisible = false
            }
            
            // Show popup menu when clicking the selector area
            binding.root.setOnClickListener { view ->
                showPopupMenu(view, day.dayOfWeek)
            }
            
            binding.btnClear.setOnClickListener {
                onClear(day.dayOfWeek)
            }
        }

        private fun showPopupMenu(anchor: android.view.View, dayOfWeek: Int) {
            val popup = PopupMenu(anchor.context, anchor)
            
            // Add "Rest Day" option
            popup.menu.add(0, -1, 0, "Rest Day")
            
            // Add template options
            templates.forEachIndexed { index, template ->
                popup.menu.add(0, index, index + 1, template.name)
            }
            
            popup.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == -1) {
                    onTemplateSelected(dayOfWeek, null)
                } else {
                    val template = templates.getOrNull(menuItem.itemId)
                    onTemplateSelected(dayOfWeek, template)
                }
                true
            }
            
            popup.show()
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<RoutineDayDisplay>() {
        override fun areItemsTheSame(oldItem: RoutineDayDisplay, newItem: RoutineDayDisplay): Boolean {
            return oldItem.dayOfWeek == newItem.dayOfWeek
        }

        override fun areContentsTheSame(oldItem: RoutineDayDisplay, newItem: RoutineDayDisplay): Boolean {
            return oldItem == newItem
        }
    }
}
