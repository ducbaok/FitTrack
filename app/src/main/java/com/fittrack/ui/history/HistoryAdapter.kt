package com.fittrack.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.R
import com.fittrack.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Sealed class representing items in the history list.
 */
sealed class HistoryListItem {
    data class DateHeader(val dateLabel: String) : HistoryListItem()
    data class WorkoutItem(val workout: WorkoutWithExercise) : HistoryListItem()
}

/**
 * Adapter for workout history list with date headers.
 */
class HistoryAdapter(
    private val onWorkoutClick: (WorkoutWithExercise) -> Unit = {}
) : ListAdapter<HistoryListItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_WORKOUT_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryListItem.DateHeader -> TYPE_DATE_HEADER
            is HistoryListItem.WorkoutItem -> TYPE_WORKOUT_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_history_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_WORKOUT_ITEM -> {
                val binding = ItemHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                WorkoutViewHolder(binding, onWorkoutClick)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HistoryListItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item)
            is HistoryListItem.WorkoutItem -> (holder as WorkoutViewHolder).bind(item.workout)
        }
    }

    class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDateHeader: TextView = view.findViewById(R.id.tvDateHeader)
        
        fun bind(item: HistoryListItem.DateHeader) {
            tvDateHeader.text = item.dateLabel
        }
    }

    class WorkoutViewHolder(
        private val binding: ItemHistoryBinding,
        private val onWorkoutClick: (WorkoutWithExercise) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(workout: WorkoutWithExercise) {
            binding.tvExerciseName.text = workout.exerciseName
            
            // Reps
            val repsText = workout.reps?.let { "$it reps" } ?: "---"
            binding.tvSetsReps.text = repsText
            
            // Weight
            val weightText = workout.weightKg?.let { "${it}kg" } ?: "---"
            binding.tvWeight.text = weightText
            
            // Time
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            binding.tvDuration.text = timeFormat.format(Date(workout.timestamp))
            
            binding.root.setOnClickListener {
                onWorkoutClick(workout)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<HistoryListItem>() {
        override fun areItemsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
            return when {
                oldItem is HistoryListItem.DateHeader && newItem is HistoryListItem.DateHeader ->
                    oldItem.dateLabel == newItem.dateLabel
                oldItem is HistoryListItem.WorkoutItem && newItem is HistoryListItem.WorkoutItem ->
                    oldItem.workout.workoutId == newItem.workout.workoutId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: HistoryListItem, newItem: HistoryListItem): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Extension function to flatten HistoryDayGroup list into HistoryListItem list.
 */
fun List<HistoryDayGroup>.toListItems(): List<HistoryListItem> {
    val items = mutableListOf<HistoryListItem>()
    for (group in this) {
        items.add(HistoryListItem.DateHeader(group.dateLabel))
        for (workout in group.workouts) {
            items.add(HistoryListItem.WorkoutItem(workout))
        }
    }
    return items
}
