package com.fittrack.ui.workouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.databinding.ItemExerciseSearchBinding

/**
 * Adapter for exercise search results.
 */
class ExerciseSearchAdapter(
    private val onExerciseClick: (ExerciseEntity) -> Unit
) : ListAdapter<ExerciseEntity, ExerciseSearchAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onExerciseClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemExerciseSearchBinding,
        private val onExerciseClick: (ExerciseEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: ExerciseEntity) {
            binding.tvExerciseName.text = exercise.name
            binding.tvExerciseInfo.text = "${getMuscleLabel(exercise.muscleId)} • ${exercise.equipment}"
            
            binding.btnAdd.setOnClickListener {
                onExerciseClick(exercise)
            }
            
            binding.root.setOnClickListener {
                onExerciseClick(exercise)
            }
        }

        private fun getMuscleLabel(muscleId: Int): String {
            return when (muscleId) {
                1 -> "Chest"
                2 -> "Back"
                3 -> "Shoulders"
                4 -> "Biceps"
                5 -> "Triceps"
                6 -> "Forearms"
                7 -> "Abs"
                8 -> "Quads"
                9 -> "Hamstrings"
                10 -> "Glutes"
                11 -> "Calves"
                else -> "Other"
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ExerciseEntity>() {
        override fun areItemsTheSame(oldItem: ExerciseEntity, newItem: ExerciseEntity): Boolean {
            return oldItem.exerciseId == newItem.exerciseId
        }

        override fun areContentsTheSame(oldItem: ExerciseEntity, newItem: ExerciseEntity): Boolean {
            return oldItem == newItem
        }
    }
}
