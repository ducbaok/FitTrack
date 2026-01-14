package com.fittrack.ui.workouts

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.R
import com.fittrack.databinding.ItemTrackerExerciseBinding
import com.fittrack.databinding.ItemTrackerSetBinding

/**
 * Data models for the Tracker.
 * TODO: Move these to a separate models file.
 */
data class TrackerSet(
    val id: String,
    val setNumber: Int,
    val previous: String = "-",
    var weight: String = "",
    var reps: String = "",
    var isCompleted: Boolean = false
)

data class TrackerExercise(
    val id: String,
    val name: String,
    val sets: List<TrackerSet>
)

/**
 * Adapter for the Workout Tracker screen.
 * Handles nested sets dynamically within each exercise item.
 */
class WorkoutTrackerAdapter(
    private val onAddSet: (String) -> Unit, // exerciseId
    private val onSetUpdate: (String, String, TrackerSet) -> Unit // exerciseId, setId, updatedSet
) : ListAdapter<TrackerExercise, WorkoutTrackerAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackerExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onAddSet, onSetUpdate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemTrackerExerciseBinding,
        private val onAddSet: (String) -> Unit,
        private val onSetUpdate: (String, String, TrackerSet) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentExercise: TrackerExercise? = null

        fun bind(exercise: TrackerExercise) {
            currentExercise = exercise
            binding.tvExerciseName.text = exercise.name

            // Clear previous sets
            binding.setsContainer.removeAllViews()

            // Add set rows dynamically
            exercise.sets.forEach { set ->
                addSetRow(set, exercise.id)
            }

            binding.btnAddSet.setOnClickListener {
                onAddSet(exercise.id)
            }
        }

        private fun addSetRow(set: TrackerSet, exerciseId: String) {
            val setBinding = ItemTrackerSetBinding.inflate(
                LayoutInflater.from(binding.root.context),
                binding.setsContainer,
                true
            )

            setBinding.tvSetNumber.text = set.setNumber.toString()
            setBinding.tvPrevious.text = set.previous
            setBinding.etWeight.setText(set.weight)
            setBinding.etReps.setText(set.reps)

            // Styling for completed state
            updateSetState(setBinding, set.isCompleted)

            // Text Watchers for Weight & Reps
            setBinding.etWeight.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    set.weight = s.toString()
                    onSetUpdate(exerciseId, set.id, set)
                }
            })

            setBinding.etReps.addTextChangedListener(object : SimpleTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    set.reps = s.toString()
                    onSetUpdate(exerciseId, set.id, set)
                }
            })

            // Complete Button Checkmark
            setBinding.btnCompleteSet.setOnClickListener {
                set.isCompleted = !set.isCompleted
                updateSetState(setBinding, set.isCompleted)
                onSetUpdate(exerciseId, set.id, set)
            }
        }

        private fun updateSetState(binding: ItemTrackerSetBinding, isCompleted: Boolean) {
            val context = binding.root.context
            if (isCompleted) {
                binding.btnCompleteSet.backgroundTintList = context.getColorStateList(R.color.accent_green)
                binding.root.setBackgroundColor(context.getColor(R.color.secondary_blue)) // Highlight row
            } else {
                binding.btnCompleteSet.backgroundTintList = context.getColorStateList(R.color.primary_navy)
                binding.root.setBackgroundColor(context.getColor(android.R.color.transparent))
            }
        }
    }

    abstract class SimpleTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    object DiffCallback : DiffUtil.ItemCallback<TrackerExercise>() {
        override fun areItemsTheSame(oldItem: TrackerExercise, newItem: TrackerExercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackerExercise, newItem: TrackerExercise): Boolean {
            return oldItem == newItem
        }
    }
}
