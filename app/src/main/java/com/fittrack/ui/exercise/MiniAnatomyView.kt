package com.fittrack.ui.exercise

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.fittrack.R
import com.fittrack.ui.home.Gender

/**
 * Mini anatomy view for Exercise Detail screen.
 * Shows a simplified body outline with highlighted muscles.
 * Supports both FRONT and BACK body views.
 */
class MiniAnatomyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class BodySide { FRONT, BACK }

    private var bodySide: BodySide = BodySide.FRONT
    private var gender: Gender = Gender.MALE
    private var bodyOutline: Drawable? = null
    
    // Muscles to highlight with their color (Int)
    private val highlightedMuscles = mutableMapOf<String, Int>()
    
    // Body part drawables
    private val muscleDrawables = mutableMapOf<String, Drawable>()
    
    // Viewport for scaling
    private val viewportWidth = 312f
    private val viewportHeight = 569f

    init {
        loadDrawables()
    }

    fun setBodySide(side: BodySide) {
        if (bodySide != side) {
            bodySide = side
            loadDrawables()
            invalidate()
        }
    }

    fun setGender(gender: Gender) {
        if (this.gender != gender) {
            this.gender = gender
            loadDrawables()
            invalidate()
        }
    }

    fun setHighlightedMuscles(muscles: Map<String, Int>) {
        highlightedMuscles.clear()
        highlightedMuscles.putAll(muscles)
        invalidate()
    }

    fun highlightMuscle(muscleId: String, color: Int) {
        highlightedMuscles[muscleId] = color
        invalidate()
    }

    private fun loadDrawables() {
        muscleDrawables.clear()
        
        if (gender == Gender.MALE) {
            when (bodySide) {
                BodySide.FRONT -> {
                    bodyOutline = ContextCompat.getDrawable(context, R.drawable.anatomy_male_front_vector)?.mutate()
                    
                    loadMuscleDrawable("chest", R.drawable.man_front_left_chest)
                    loadMuscleDrawable("chest_r", R.drawable.man_front_right_chest)
                    loadMuscleDrawable("shoulders", R.drawable.man_front_left_front_shoulders)
                    loadMuscleDrawable("shoulders_r", R.drawable.man_front_right_front_shoulders)
                    loadMuscleDrawable("biceps", R.drawable.man_front_left_biceps)
                    loadMuscleDrawable("biceps_r", R.drawable.man_front_right_biceps)
                    loadMuscleDrawable("forearms", R.drawable.man_front_left_forearms)
                    loadMuscleDrawable("forearms_r", R.drawable.man_front_right_forearms)
                    loadMuscleDrawable("abs", R.drawable.man_front_abdominals)
                    loadMuscleDrawable("obliques", R.drawable.man_front_left_obliques)
                    loadMuscleDrawable("obliques_r", R.drawable.man_front_right_obliques)
                    loadMuscleDrawable("quads", R.drawable.man_front_left_quads)
                    loadMuscleDrawable("quads_r", R.drawable.man_front_right_quads)
                    loadMuscleDrawable("calves", R.drawable.man_front_left_calves)
                    loadMuscleDrawable("calves_r", R.drawable.man_front_right_calves)
                    loadMuscleDrawable("traps", R.drawable.man_front_left_traps)
                    loadMuscleDrawable("traps_r", R.drawable.man_front_right_traps)
                }
                BodySide.BACK -> {
                    bodyOutline = ContextCompat.getDrawable(context, R.drawable.man_back_divided_body)?.mutate()
                    
                    loadMuscleDrawable("traps", R.drawable.man_back_traps)
                    loadMuscleDrawable("traps_middle", R.drawable.man_back_traps_middle)
                    loadMuscleDrawable("shoulders", R.drawable.man_back_left_rear_shoulders)
                    loadMuscleDrawable("shoulders_r", R.drawable.man_back_right_rear_shoulders)
                    loadMuscleDrawable("lats", R.drawable.man_back_left_lats)
                    loadMuscleDrawable("lats_r", R.drawable.man_back_right_lats)
                    loadMuscleDrawable("triceps", R.drawable.man_back_left_triceps)
                    loadMuscleDrawable("triceps_r", R.drawable.man_back_right_triceps)
                    loadMuscleDrawable("forearms", R.drawable.man_back_left_forearm)
                    loadMuscleDrawable("forearms_r", R.drawable.man_back_right_forearm)
                    loadMuscleDrawable("lowerback", R.drawable.man_back_lowerback)
                    loadMuscleDrawable("glutes", R.drawable.man_back_left_glutes)
                    loadMuscleDrawable("glutes_r", R.drawable.man_back_right_glutes)
                    loadMuscleDrawable("hamstrings", R.drawable.man_back_left_hamstring)
                    loadMuscleDrawable("hamstrings_r", R.drawable.man_back_right_hamstring)
                    loadMuscleDrawable("calves", R.drawable.man_back_left_calves)
                    loadMuscleDrawable("calves_r", R.drawable.man_back_right_calves)
                }
            }
        } else {
            // FEMALE
            when (bodySide) {
                BodySide.FRONT -> {
                    bodyOutline = ContextCompat.getDrawable(context, R.drawable.woman_front_outline_divided)?.mutate()
                    
                    loadMuscleDrawable("chest", R.drawable.woman_front_left_chest)
                    loadMuscleDrawable("chest_r", R.drawable.woman_front_right_chest)
                    loadMuscleDrawable("shoulders", R.drawable.woman_front_left_front_shoulders)
                    loadMuscleDrawable("shoulders_r", R.drawable.woman_front_right_front_shoulders)
                    loadMuscleDrawable("biceps", R.drawable.woman_front_left_biceps)
                    loadMuscleDrawable("biceps_r", R.drawable.woman_front_right_biceps)
                    loadMuscleDrawable("forearms", R.drawable.woman_front_left_forearm)
                    loadMuscleDrawable("forearms_r", R.drawable.woman_front_right_forearm)
                    loadMuscleDrawable("abs", R.drawable.woman_front_abnominals) // Note typo in usage
                    loadMuscleDrawable("obliques", R.drawable.woman_front_left_obliques)
                    loadMuscleDrawable("obliques_r", R.drawable.woman_front_right_obliques)
                    loadMuscleDrawable("quads", R.drawable.woman_front_left_quads)
                    loadMuscleDrawable("quads_r", R.drawable.woman_front_right_quads)
                    loadMuscleDrawable("calves", R.drawable.woman_front_left_calves)
                    loadMuscleDrawable("calves_r", R.drawable.woman_front_right_calves)
                    loadMuscleDrawable("traps", R.drawable.woman_front_left_traps)
                    loadMuscleDrawable("traps_r", R.drawable.woman_front_right_traps)
                }
                BodySide.BACK -> {
                    bodyOutline = ContextCompat.getDrawable(context, R.drawable.woman_back_outline)?.mutate()
                    
                    loadMuscleDrawable("traps", R.drawable.woman_back_traps)
                    loadMuscleDrawable("traps_middle", R.drawable.woman_back_traps_middle)
                    loadMuscleDrawable("shoulders", R.drawable.woman_back_left_rear_shoulders)
                    loadMuscleDrawable("shoulders_r", R.drawable.woman_back_right_rear_shoulders)
                    loadMuscleDrawable("lats", R.drawable.woman_back_left_lats)
                    loadMuscleDrawable("lats_r", R.drawable.woman_back_right_lats)
                    // Note: Mapping bicep asset to triceps logic as per AnatomyView
                    loadMuscleDrawable("triceps", R.drawable.woman_back_left_bicep) 
                    loadMuscleDrawable("triceps_r", R.drawable.woman_back_right_bicep)
                    loadMuscleDrawable("forearms", R.drawable.woman_back_left_forearm)
                    loadMuscleDrawable("forearms_r", R.drawable.woman_back_right_forearm)
                    loadMuscleDrawable("lowerback", R.drawable.woman_back_lowerback)
                    loadMuscleDrawable("glutes", R.drawable.woman_back_left_glute)
                    loadMuscleDrawable("glutes_r", R.drawable.woman_back_right_glute)
                    loadMuscleDrawable("hamstrings", R.drawable.woman_back_left_hamstrings)
                    loadMuscleDrawable("hamstrings_r", R.drawable.woman_back_right_hamstrings)
                    loadMuscleDrawable("calves", R.drawable.woman_back_left_calves)
                    loadMuscleDrawable("calves_r", R.drawable.woman_back_right_calves)
                }
            }
        }
    }

    private fun loadMuscleDrawable(id: String, resId: Int) {
        ContextCompat.getDrawable(context, resId)?.mutate()?.let {
            muscleDrawables[id] = it
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (width == 0 || height == 0) return
        
        val scale = calculateScale()
        val translateX = ((width - viewportWidth * scale) / 2).toInt()
        val translateY = ((height - viewportHeight * scale) / 2).toInt()
        val scaledWidth = (viewportWidth * scale).toInt()
        val scaledHeight = (viewportHeight * scale).toInt()

        // Draw highlighted muscles first (under outline)
        for ((muscleId, intensity) in highlightedMuscles) {
            drawMuscleWithIntensity(canvas, muscleId, intensity, translateX, translateY, scaledWidth, scaledHeight)
        }

        // Draw body outline on top with strong neutral tint
        bodyOutline?.apply {
            setBounds(translateX, translateY, translateX + scaledWidth, translateY + scaledHeight)
            setTint(Color.parseColor("#DDFFFFFF"))
            draw(canvas)
        }
    }

    private fun drawMuscleWithIntensity(
        canvas: Canvas,
        muscleId: String,
        color: Int,
        translateX: Int,
        translateY: Int,
        scaledWidth: Int,
        scaledHeight: Int
    ) {
        // Find matching muscle drawables (left and right versions)
        val muscleKeys = muscleDrawables.keys.filter { 
            it == muscleId || it.startsWith("${muscleId}_") || it == "${muscleId}_r"
        }
        
        for (key in muscleKeys) {
            muscleDrawables[key]?.apply {
                setBounds(translateX, translateY, translateX + scaledWidth, translateY + scaledHeight)
                setTint(color)
                draw(canvas)
                setTintList(null)
            }
        }
    }

    private fun calculateScale(): Float {
        val scaleX = width.toFloat() / viewportWidth
        val scaleY = height.toFloat() / viewportHeight
        return minOf(scaleX, scaleY)
    }

    companion object {
        /**
         * Map muscle ID to muscle name for highlighting.
         * Returns list of muscle IDs to highlight on both front and back views.
         */
        fun getMuscleIdsForMuscle(muscleId: Int): Pair<List<String>, List<String>> {
            // Returns (frontMuscles, backMuscles)
            return when (muscleId) {
                1 -> listOf("chest") to emptyList() // Chest
                2 -> emptyList<String>() to listOf("traps", "lats", "lowerback") // Back
                3 -> listOf("shoulders") to listOf("shoulders") // Shoulders
                4 -> listOf("biceps") to emptyList() // Biceps
                5 -> emptyList<String>() to listOf("triceps") // Triceps
                6 -> listOf("quads", "calves") to emptyList() // Legs (Quads focus)
                7 -> emptyList<String>() to listOf("glutes", "hamstrings") // Glutes/Hamstrings
                8 -> listOf("abs", "obliques") to emptyList() // Core
                9 -> listOf("forearms") to listOf("forearms") // Forearms
                else -> emptyList<String>() to emptyList()
            }
        }
    }
}
