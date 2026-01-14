package com.fittrack.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.fittrack.R
import com.fittrack.domain.usecase.FatigueColor

/**
 * Represents a clickable body part with its drawable.
 * All body parts use the same viewport (312x569) so x/y is always 0.
 */
data class BodyPart(
    val id: String,
    val drawableResId: Int,
    val muscleDbId: Int = 0  // Maps to database muscle ID
)

/**
 * Custom View for displaying and interacting with anatomy diagrams.
 * Uses VectorDrawables with same viewport - all stacked at 0,0.
 */
class AnatomyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bodyDrawable: Drawable? = null
    private var gender: Gender = Gender.MALE
    private var bodyView: BodyView = BodyView.FRONT
    private var detailLevel: DetailLevel = DetailLevel.BASIC

    // Body parts with their drawables (keyed by drawableResId)
    private val bodyParts = mutableListOf<BodyPart>()
    private val bodyPartDrawables = mutableMapOf<Int, Drawable>()
    private val bodyPartBitmaps = mutableMapOf<Int, Bitmap>()

    // Muscle fatigue colors
    private val muscleFatigueColors = mutableMapOf<String, FatigueColor>()

    // Listener for muscle tap events
    var onMuscleClickListener: ((muscleId: String) -> Unit)? = null

    // Currently selected muscle
    private var selectedMuscle: String? = null

    // Viewport dimensions (same for all body parts)
    private val viewportWidth = 312f
    private val viewportHeight = 569f

    init {
        isClickable = true
        isFocusable = true
        loadDrawables()
    }

    fun setGender(gender: Gender) {
        if (this.gender != gender) {
            this.gender = gender
            loadDrawables()
        }
    }

    fun setBodyView(bodyView: BodyView) {
        if (this.bodyView != bodyView) {
            this.bodyView = bodyView
            loadDrawables()
        }
    }

    fun setDetailLevel(detailLevel: DetailLevel) {
        if (this.detailLevel != detailLevel) {
            this.detailLevel = detailLevel
            loadDrawables()
        }
    }

    fun setMuscleFatigueColors(colors: Map<String, FatigueColor>) {
        muscleFatigueColors.clear()
        muscleFatigueColors.putAll(colors)
        invalidate()
    }

    fun setMuscleFatigueColor(muscleId: String, color: FatigueColor) {
        muscleFatigueColors[muscleId] = color
        invalidate()
    }

    private fun loadDrawables() {
        // Load main body outline
        bodyDrawable = ContextCompat.getDrawable(context, R.drawable.anatomy_male_front_vector)?.mutate()

        // Clear previous
        bodyParts.clear()
        bodyPartDrawables.clear()
        bodyPartBitmaps.clear()

        if (bodyView == BodyView.FRONT && gender == Gender.MALE) {
            // All body parts with same viewport - stacked at 0,0
            // IDs without left/right - both sides map to same muscle
            bodyParts.addAll(listOf(
                // Chest (muscleId = 1)
                BodyPart("chest", R.drawable.man_front_left_chest, 1),
                BodyPart("chest", R.drawable.man_front_right_chest, 1),
                
                // Shoulders (muscleId = 3)
                BodyPart("shoulders", R.drawable.man_front_left_front_shoulders, 3),
                BodyPart("shoulders", R.drawable.man_front_right_front_shoulders, 3),
                
                // Biceps (muscleId = 4)
                BodyPart("biceps", R.drawable.man_front_left_biceps, 4),
                BodyPart("biceps", R.drawable.man_front_right_biceps, 4),
                
                // Forearms (muscleId = 9)
                BodyPart("forearms", R.drawable.man_front_left_forearms, 9),
                BodyPart("forearms", R.drawable.man_front_right_forearms, 9),
                
                // Hands (muscleId = 9, same as forearms)
                BodyPart("hands", R.drawable.man_front_left_hand, 9),
                BodyPart("hands", R.drawable.man_front_right_hand, 9),
                
                // Abs (muscleId = 8)
                BodyPart("abs", R.drawable.man_front_abdominals, 8),
                
                // Obliques (muscleId = 8, same as abs)
                BodyPart("obliques", R.drawable.man_front_left_obliques, 8),
                BodyPart("obliques", R.drawable.man_front_right_obliques, 8),
                
                // Traps (muscleId = 2)
                BodyPart("traps", R.drawable.man_front_left_traps, 2),
                BodyPart("traps", R.drawable.man_front_right_traps, 2),
                
                // Quads (muscleId = 6)
                BodyPart("quads", R.drawable.man_front_left_quads, 6),
                BodyPart("quads", R.drawable.man_front_right_quads, 6),
                
                // Calves (muscleId = 6)
                BodyPart("calves", R.drawable.man_front_left_calves, 6),
                BodyPart("calves", R.drawable.man_front_right_calves, 6)
            ))
        } else if (bodyView == BodyView.BACK && gender == Gender.MALE) {
            // BACK view body parts
            bodyDrawable = ContextCompat.getDrawable(context, R.drawable.man_back_divided_body)?.mutate()
            
            bodyParts.addAll(listOf(
                // Traps (muscleId = 2)
                BodyPart("traps", R.drawable.man_back_traps, 2),
                BodyPart("traps_middle", R.drawable.man_back_traps_middle, 2),
                
                // Rear Shoulders (muscleId = 3)
                BodyPart("shoulders", R.drawable.man_back_left_rear_shoulders, 3),
                BodyPart("shoulders", R.drawable.man_back_right_rear_shoulders, 3),
                
                // Lats (muscleId = 2)
                BodyPart("lats", R.drawable.man_back_left_lats, 2),
                BodyPart("lats", R.drawable.man_back_right_lats, 2),
                
                // Triceps (muscleId = 5)
                BodyPart("triceps", R.drawable.man_back_left_triceps, 5),
                BodyPart("triceps", R.drawable.man_back_right_triceps, 5),
                
                // Forearms (muscleId = 9)
                BodyPart("forearms", R.drawable.man_back_left_forearm, 9),
                BodyPart("forearms", R.drawable.man_back_right_forearm, 9),
                
                // Hands (muscleId = 9)
                BodyPart("hands", R.drawable.man_back_left_hand, 9),
                BodyPart("hands", R.drawable.man_back_right_hand, 9),
                
                // Lower Back (muscleId = 2)
                BodyPart("lowerback", R.drawable.man_back_lowerback, 2),
                
                // Glutes (muscleId = 7)
                BodyPart("glutes", R.drawable.man_back_left_glutes, 7),
                BodyPart("glutes", R.drawable.man_back_right_glutes, 7),
                
                // Hamstrings (muscleId = 7)
                BodyPart("hamstrings", R.drawable.man_back_left_hamstring, 7),
                BodyPart("hamstrings", R.drawable.man_back_right_hamstring, 7),
                
                // Calves (muscleId = 6)
                BodyPart("calves", R.drawable.man_back_left_calves, 6),
                BodyPart("calves", R.drawable.man_back_right_calves, 6)
            ))
        } else if (bodyView == BodyView.FRONT && gender == Gender.FEMALE) {
            // FEMALE FRONT view
            bodyDrawable = ContextCompat.getDrawable(context, R.drawable.woman_front_outline_divided)?.mutate()
            
            bodyParts.addAll(listOf(
                // Chest (muscleId = 1)
                BodyPart("chest", R.drawable.woman_front_left_chest, 1),
                BodyPart("chest", R.drawable.woman_front_right_chest, 1),

                // Shoulders (muscleId = 3)
                BodyPart("shoulders", R.drawable.woman_front_left_front_shoulders, 3),
                BodyPart("shoulders", R.drawable.woman_front_right_front_shoulders, 3),

                // Biceps (muscleId = 4)
                BodyPart("biceps", R.drawable.woman_front_left_biceps, 4),
                BodyPart("biceps", R.drawable.woman_front_right_biceps, 4),

                // Forearms (muscleId = 9)
                BodyPart("forearms", R.drawable.woman_front_left_forearm, 9),
                BodyPart("forearms", R.drawable.woman_front_right_forearm, 9),

                // Hands (muscleId = 9)
                BodyPart("hands", R.drawable.woman_front_left_hand, 9),
                BodyPart("hands", R.drawable.woman_front_right_hand, 9),

                // Abs (muscleId = 8)
                BodyPart("abs", R.drawable.woman_front_abnominals, 8),

                // Obliques (muscleId = 8)
                BodyPart("obliques", R.drawable.woman_front_left_obliques, 8),
                BodyPart("obliques", R.drawable.woman_front_right_obliques, 8),
                
                // Traps (muscleId = 2)
                BodyPart("traps", R.drawable.woman_front_left_traps, 2),
                BodyPart("traps", R.drawable.woman_front_right_traps, 2),

                // Quads (muscleId = 6)
                BodyPart("quads", R.drawable.woman_front_left_quads, 6),
                BodyPart("quads", R.drawable.woman_front_right_quads, 6),

                // Calves (muscleId = 6)
                BodyPart("calves", R.drawable.woman_front_left_calves, 6),
                BodyPart("calves", R.drawable.woman_front_right_calves, 6)
            ))
        } else if (bodyView == BodyView.BACK && gender == Gender.FEMALE) {
            // FEMALE BACK view
            bodyDrawable = ContextCompat.getDrawable(context, R.drawable.woman_back_outline)?.mutate()
            
            bodyParts.addAll(listOf(
                // Traps (muscleId = 2)
                BodyPart("traps", R.drawable.woman_back_traps, 2),
                BodyPart("traps_middle", R.drawable.woman_back_traps_middle, 2),

                // Rear Shoulders (muscleId = 3)
                BodyPart("shoulders", R.drawable.woman_back_left_rear_shoulders, 3),
                BodyPart("shoulders", R.drawable.woman_back_right_rear_shoulders, 3),

                // Lats (muscleId = 2)
                BodyPart("lats", R.drawable.woman_back_left_lats, 2),
                BodyPart("lats", R.drawable.woman_back_right_lats, 2),

                // Triceps (muscleId = 5) - Note: Assets might be named bicep/tricep differently in back view
                // Checking file list: woman_back_left_bicep exists? Yes. 
                // Wait, back view usually shows Triceps. 
                // File list has `woman_back_left_bicep`. This might be Tricep or actual Bicep visible from back?
                // Standard anatomy back view shows Triceps. I will map `bicep` to Triceps (5) if that's what the file is, 
                // OR check if there is a tricep file. 
                // File list: `woman_back_left_bicep`. NO `woman_back_left_tricep`. 
                // I will map `woman_back_left_bicep` to Triceps ID (5) for now as a best guess for the asset name vs visual.
                BodyPart("triceps", R.drawable.woman_back_left_bicep, 5),
                BodyPart("triceps", R.drawable.woman_back_right_bicep, 5),

                // Forearms (muscleId = 9)
                BodyPart("forearms", R.drawable.woman_back_left_forearm, 9),
                BodyPart("forearms", R.drawable.woman_back_right_forearm, 9),

                // Hands (muscleId = 9)
                BodyPart("hands", R.drawable.woman_back_left_hand, 9),
                BodyPart("hands", R.drawable.woman_back_right_hand, 9),

                // Lower Back (muscleId = 2)
                BodyPart("lowerback", R.drawable.woman_back_lowerback, 2),

                // Glutes (muscleId = 7)
                BodyPart("glutes", R.drawable.woman_back_left_glute, 7),
                BodyPart("glutes", R.drawable.woman_back_right_glute, 7),

                // Hamstrings (muscleId = 7)
                BodyPart("hamstrings", R.drawable.woman_back_left_hamstrings, 7),
                BodyPart("hamstrings", R.drawable.woman_back_right_hamstrings, 7),

                // Calves (muscleId = 6)
                BodyPart("calves", R.drawable.woman_back_left_calves, 6),
                BodyPart("calves", R.drawable.woman_back_right_calves, 6)
            ))
        }

        // Load drawables for each body part (keyed by drawableResId)
        for (part in bodyParts) {
            ContextCompat.getDrawable(context, part.drawableResId)?.mutate()?.let { drawable ->
                bodyPartDrawables[part.drawableResId] = drawable
            }
        }

        if (width > 0 && height > 0) {
            updateBounds()
        }

        invalidate()
    }

    private fun updateBounds() {
        val scale = calculateScale()
        val translateX = ((width - viewportWidth * scale) / 2).toInt()
        val translateY = ((height - viewportHeight * scale) / 2).toInt()
        val scaledWidth = (viewportWidth * scale).toInt()
        val scaledHeight = (viewportHeight * scale).toInt()

        // Update main body bounds
        bodyDrawable?.setBounds(translateX, translateY, translateX + scaledWidth, translateY + scaledHeight)

        // All body parts have same bounds (stacked at same position)
        bodyPartBitmaps.clear()
        for (part in bodyParts) {
            bodyPartDrawables[part.drawableResId]?.let { drawable ->
                drawable.setBounds(translateX, translateY, translateX + scaledWidth, translateY + scaledHeight)

                // Create bitmap for hit testing
                val bitmap = Bitmap.createBitmap(scaledWidth.coerceAtLeast(1), scaledHeight.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, scaledWidth, scaledHeight)
                drawable.draw(canvas)
                drawable.setBounds(translateX, translateY, translateX + scaledWidth, translateY + scaledHeight)
                bodyPartBitmaps[part.drawableResId] = bitmap
            }
        }
    }

    private fun calculateScale(): Float {
        if (width == 0 || height == 0) return 1f
        val scaleX = width.toFloat() / viewportWidth
        val scaleY = height.toFloat() / viewportHeight
        return minOf(scaleX, scaleY)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateBounds()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw selected body parts with pink fill (under the outline)
        selectedMuscle?.let { muscleId ->
            // Draw all parts that match this muscle ID
            for (part in bodyParts) {
                if (part.id == muscleId) {
                    bodyPartDrawables[part.drawableResId]?.let { drawable ->
                        drawable.setTint(Color.parseColor("#E91E63")) // Same pink as outline
                        drawable.draw(canvas)
                        drawable.setTintList(null)
                    }
                }
            }
        }

        // Draw the main body outline on TOP (always visible) - Pink/red tinted
        bodyDrawable?.let { drawable ->
            drawable.setTint(Color.parseColor("#E91E63")) // Pink/magenta color
            drawable.draw(canvas)
        }

        // Draw fatigue overlays for muscles that have fatigue data
        for ((muscleId, fatigueColor) in muscleFatigueColors) {
            if (muscleId == selectedMuscle) continue
            
            // Draw all parts that match this muscle ID
            for (part in bodyParts) {
                if (part.id == muscleId) {
                    bodyPartDrawables[part.drawableResId]?.let { drawable ->
                        val tintColor = when (fatigueColor) {
                            FatigueColor.RED -> Color.parseColor("#FF5252")
                            FatigueColor.YELLOW -> Color.parseColor("#FFD54F")
                            FatigueColor.GREEN -> Color.parseColor("#81C784")
                        }
                        drawable.setTint(tintColor)
                        drawable.draw(canvas)
                        drawable.setTintList(null)
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val hitMuscle = findHitMuscle(event.x, event.y)
                if (hitMuscle != null && hitMuscle != selectedMuscle) {
                    selectedMuscle = hitMuscle
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val hitMuscle = findHitMuscle(event.x, event.y)
                if (hitMuscle != selectedMuscle) {
                    selectedMuscle = hitMuscle
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                val hitMuscle = findHitMuscle(event.x, event.y)
                if (hitMuscle != null) {
                    selectedMuscle = hitMuscle
                    onMuscleClickListener?.invoke(hitMuscle)
                    performClick()
                }
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun findHitMuscle(x: Float, y: Float): String? {
        val scale = calculateScale()
        val translateX = (width - viewportWidth * scale) / 2
        val translateY = (height - viewportHeight * scale) / 2
        val scaledWidth = viewportWidth * scale
        val scaledHeight = viewportHeight * scale

        // Check if in bounds
        if (x < translateX || x > translateX + scaledWidth ||
            y < translateY || y > translateY + scaledHeight) {
            return null
        }

        // Convert to bitmap coordinates
        val bitmapX = ((x - translateX) / scaledWidth * scaledWidth).toInt()
        val bitmapY = ((y - translateY) / scaledHeight * scaledHeight).toInt()

        // Check each body part (order: top of body to bottom)
        for (part in bodyParts) {
            val bitmap = bodyPartBitmaps[part.drawableResId] ?: continue

            if (bitmapX >= 0 && bitmapX < bitmap.width && bitmapY >= 0 && bitmapY < bitmap.height) {
                val pixel = bitmap.getPixel(bitmapX, bitmapY)
                val alpha = Color.alpha(pixel)

                if (alpha > 50) {
                    return part.id
                }
            }
        }
        return null
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        for (bitmap in bodyPartBitmaps.values) {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
        bodyPartBitmaps.clear()
    }
}
