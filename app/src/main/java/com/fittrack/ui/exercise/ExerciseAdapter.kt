package com.fittrack.ui.exercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.R
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.databinding.ItemExerciseBinding

/**
 * RecyclerView adapter for displaying exercises.
 */
class ExerciseAdapter(
    private val onExerciseClick: (ExerciseEntity) -> Unit,
    private val onFavoriteClick: ((ExerciseEntity) -> Unit)? = null
) : ListAdapter<ExerciseEntity, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExerciseViewHolder(
        private val binding: ItemExerciseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onExerciseClick(getItem(position))
                }
            }

            binding.btnFavorite.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick?.invoke(getItem(position))
                }
            }
        }

        fun bind(exercise: ExerciseEntity) {
            binding.tvExerciseName.text = exercise.name
            binding.tvExerciseInfo.text = "${exercise.equipment} • ${exercise.difficulty}"
            binding.tvDifficulty.text = exercise.difficulty
            
            // Set difficulty badge style
            val (bgDrawable, textColor) = when (exercise.difficulty) {
                "Beginner" -> Pair(R.drawable.bg_badge_beginner, R.color.colorBeginner)
                "Intermediate" -> Pair(R.drawable.bg_badge_intermediate, R.color.colorIntermediate)
                "Advanced" -> Pair(R.drawable.bg_badge_advanced, R.color.colorAdvanced)
                else -> Pair(R.drawable.bg_badge_beginner, R.color.colorBeginner)
            }
            binding.tvDifficulty.setBackgroundResource(bgDrawable)
            binding.tvDifficulty.setTextColor(binding.root.context.getColor(textColor))

            // Update favorite star icon
            val starIcon = if (exercise.isFavorite) {
                R.drawable.ic_star_filled
            } else {
                R.drawable.ic_star
            }
            binding.btnFavorite.setImageResource(starIcon)
            
            val starTint = if (exercise.isFavorite) {
                R.color.colorFavorite // Gold/yellow for filled star
            } else {
                R.color.colorMutedForeground
            }
            binding.btnFavorite.setColorFilter(binding.root.context.getColor(starTint))
        }
    }

    class ExerciseDiffCallback : DiffUtil.ItemCallback<ExerciseEntity>() {
        override fun areItemsTheSame(oldItem: ExerciseEntity, newItem: ExerciseEntity): Boolean {
            return oldItem.exerciseId == newItem.exerciseId
        }

        override fun areContentsTheSame(oldItem: ExerciseEntity, newItem: ExerciseEntity): Boolean {
            return oldItem == newItem
        }
    }
}
