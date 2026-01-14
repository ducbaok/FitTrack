package com.fittrack.ui.workouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.databinding.ItemWorkoutExerciseBinding

/**
 * Adapter for selected exercises in Build Workout screen.
 */
class WorkoutExerciseAdapter(
    private val onRemove: (Int) -> Unit,
    private val onSetsChange: (Int, Int) -> Unit,
    private val onRepsChange: (Int, Int) -> Unit,
    private val onWeightChange: (Int, Float) -> Unit
) : ListAdapter<BuildWorkoutExercise, WorkoutExerciseAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkoutExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onRemove, onSetsChange, onRepsChange, onWeightChange)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemWorkoutExerciseBinding,
        private val onRemove: (Int) -> Unit,
        private val onSetsChange: (Int, Int) -> Unit,
        private val onRepsChange: (Int, Int) -> Unit,
        private val onWeightChange: (Int, Float) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentExercise: BuildWorkoutExercise? = null

        fun bind(exercise: BuildWorkoutExercise) {
            currentExercise = exercise
            
            binding.tvExerciseName.text = exercise.name
            binding.tvMuscle.text = exercise.muscle
            binding.tvSets.text = exercise.sets.toString()
            binding.tvReps.text = exercise.reps.toString()
            binding.tvWeight.text = exercise.weight.toInt().toString()
            
            // Remove button
            binding.btnRemove.setOnClickListener {
                onRemove(exercise.exerciseId)
            }
            
            // Sets controls
            binding.btnSetsMinus.setOnClickListener {
                currentExercise?.let { 
                    onSetsChange(it.exerciseId, it.sets - 1) 
                }
            }
            binding.btnSetsPlus.setOnClickListener {
                currentExercise?.let { 
                    onSetsChange(it.exerciseId, it.sets + 1) 
                }
            }
            
            // Reps controls
            binding.btnRepsMinus.setOnClickListener {
                currentExercise?.let { 
                    onRepsChange(it.exerciseId, it.reps - 1) 
                }
            }
            binding.btnRepsPlus.setOnClickListener {
                currentExercise?.let { 
                    onRepsChange(it.exerciseId, it.reps + 1) 
                }
            }
            
            // Weight controls (in 2.5kg increments)
            binding.btnWeightMinus.setOnClickListener {
                currentExercise?.let { 
                    onWeightChange(it.exerciseId, it.weight - 2.5f) 
                }
            }
            binding.btnWeightPlus.setOnClickListener {
                currentExercise?.let { 
                    onWeightChange(it.exerciseId, it.weight + 2.5f) 
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<BuildWorkoutExercise>() {
        override fun areItemsTheSame(oldItem: BuildWorkoutExercise, newItem: BuildWorkoutExercise): Boolean {
            return oldItem.exerciseId == newItem.exerciseId
        }

        override fun areContentsTheSame(oldItem: BuildWorkoutExercise, newItem: BuildWorkoutExercise): Boolean {
            return oldItem == newItem
        }
    }
}
