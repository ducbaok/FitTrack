package com.fittrack.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.fittrack.R

/**
 * Custom View for displaying muscle diagrams using Android VectorDrawables.
 * Loads vector XML files and detects touches on named paths.
 * 
 * Features:
 * - White fill by default
 * - Gray highlight when pressed
 * - Color tint based on muscle fatigue
 */
class MuscleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Vector drawable to render
    private var muscleDrawable: Drawable? = null
    
    // Currently pressed muscle path name
    private var pressedMuscle: String? = null
    
    // Currently selected muscle path name  
    private var selectedMuscle: String? = null
    
    // Muscle fatigue colors (muscle name → color)
    private val muscleFatigueColors = mutableMapOf<String, Int>()
    
    // Listener for muscle tap events
    var onMuscleClickListener: ((muscleName: String) -> Unit)? = null
    
    // Colors
    private val colorNormal = Color.WHITE
    private val colorPressed = Color.parseColor("#CCCCCC")  // Light gray when pressed
    private val colorSelected = Color.parseColor("#4CAF50") // Green when selected
    
    init {
        isClickable = true
        isFocusable = true
        
        // Load default muscle drawable if set in XML
        context.theme.obtainStyledAttributes(attrs, R.styleable.MuscleView, 0, 0).apply {
            try {
                val resourceId = getResourceId(R.styleable.MuscleView_muscleDrawable, 0)
                if (resourceId != 0) {
                    setMuscleDrawable(resourceId)
                }
            } finally {
                recycle()
            }
        }
    }
    
    /**
     * Set the muscle vector drawable to display.
     */
    fun setMuscleDrawable(@DrawableRes resId: Int) {
        muscleDrawable = ContextCompat.getDrawable(context, resId)?.mutate()
        muscleDrawable?.setBounds(0, 0, width, height)
        invalidate()
    }
    
    /**
     * Set fatigue color for a specific muscle.
     */
    fun setMuscleFatigueColor(muscleName: String, color: Int) {
        muscleFatigueColors[muscleName] = color
        invalidate()
    }
    
    /**
     * Clear all fatigue colors.
     */
    fun clearFatigueColors() {
        muscleFatigueColors.clear()
        invalidate()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        muscleDrawable?.setBounds(0, 0, w, h)
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        muscleDrawable?.let { drawable ->
            // Apply color filter based on state
            val colorFilter: ColorFilter? = when {
                pressedMuscle != null -> PorterDuffColorFilter(colorPressed, PorterDuff.Mode.MULTIPLY)
                selectedMuscle != null -> PorterDuffColorFilter(colorSelected, PorterDuff.Mode.MULTIPLY)
                else -> null
            }
            
            drawable.colorFilter = colorFilter
            drawable.draw(canvas)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Highlight on press
                pressedMuscle = "pressed"
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                pressedMuscle = null
                
                // Get the muscle name from the drawable (for now, use the whole view)
                val muscleName = getMuscleNameAtPoint(event.x, event.y)
                
                if (muscleName != null) {
                    selectedMuscle = muscleName
                    onMuscleClickListener?.invoke(muscleName)
                    performClick()
                }
                
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                pressedMuscle = null
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    /**
     * Determine which muscle was tapped based on coordinates.
     * For single-path vectors, returns the path name.
     */
    private fun getMuscleNameAtPoint(x: Float, y: Float): String? {
        // For a single-muscle view (like right_hand.xml), 
        // if tap is within bounds, return the muscle name
        muscleDrawable?.let { drawable ->
            if (x >= 0 && x <= width && y >= 0 && y <= height) {
                // Extract muscle name from the tag or return a default
                return tag as? String ?: "muscle_unknown"
            }
        }
        return null
    }
    
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
