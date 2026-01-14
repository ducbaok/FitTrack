package com.fittrack.data.local

import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the database with initial exercise data on first launch.
 * Using hardcoded data to work with free tier Supabase.
 */
@Singleton
class ExerciseSeeder @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) {
    
    /**
     * Seeds exercises if database is empty.
     */
    suspend fun seedIfEmpty() {
        val existing = exerciseRepository.getAllExercises().first()
        if (existing.isEmpty()) {
            exerciseRepository.insertAllExercises(getSampleExercises())
        }
    }
    
    private fun getSampleExercises(): List<ExerciseEntity> = listOf(
        // ============ CHEST (muscleId = 1) ============
        ExerciseEntity(
            exerciseId = 1,
            name = "Bench Press",
            muscleId = 1, // Chest
            equipment = "Barbell",
            difficulty = "Intermediate",
            type = "Compound",
            instructions = """
                1. Lie flat on a bench with your feet on the ground
                2. Grip the barbell slightly wider than shoulder width
                3. Unrack the bar and lower it to your mid-chest
                4. Press the bar back up to the starting position
                5. Keep your shoulder blades squeezed together throughout
            """.trimIndent(),
            proTips = "Keep your wrists straight and elbows at 45 degrees to protect your shoulders. Breathe in as you lower, exhale as you press.",
            imageUrl = "bench_press"
        ),
        ExerciseEntity(
            exerciseId = 2,
            name = "Push-Ups",
            muscleId = 1,
            equipment = "Bodyweight",
            difficulty = "Beginner",
            type = "Compound",
            instructions = """
                1. Start in a plank position with hands shoulder-width apart
                2. Keep your body in a straight line from head to heels
                3. Lower your chest to the ground by bending your elbows
                4. Push back up to the starting position
                5. Keep your core tight throughout
            """.trimIndent(),
            proTips = "Don't let your hips sag or pike up. For easier variation, do them on your knees.",
            imageUrl = "push_ups"
        ),
        ExerciseEntity(
            exerciseId = 3,
            name = "Dumbbell Flyes",
            muscleId = 1,
            equipment = "Dumbbell",
            difficulty = "Intermediate",
            type = "Isolation",
            instructions = """
                1. Lie on a flat bench holding dumbbells above your chest
                2. Keep a slight bend in your elbows
                3. Lower the weights out to the sides in an arc
                4. Stop when you feel a stretch in your chest
                5. Squeeze your chest to bring weights back together
            """.trimIndent(),
            proTips = "Focus on the stretch at the bottom and squeeze at the top. Don't go too heavy - this is about the stretch.",
            imageUrl = "dumbbell_flyes"
        ),
        
        // ============ BACK (muscleId = 2) ============
        ExerciseEntity(
            exerciseId = 4,
            name = "Pull-Ups",
            muscleId = 2, // Back
            equipment = "Bodyweight",
            difficulty = "Intermediate",
            type = "Compound",
            instructions = """
                1. Hang from a pull-up bar with an overhand grip
                2. Grip slightly wider than shoulder width
                3. Pull yourself up until your chin is over the bar
                4. Lower yourself with control
                5. Keep your core engaged throughout
            """.trimIndent(),
            proTips = "Initiate the movement by squeezing your shoulder blades together. If you can't do a full pull-up, use resistance bands.",
            imageUrl = "pull_ups"
        ),
        ExerciseEntity(
            exerciseId = 5,
            name = "Barbell Rows",
            muscleId = 2,
            equipment = "Barbell",
            difficulty = "Intermediate",
            type = "Compound",
            instructions = """
                1. Stand with feet shoulder-width apart, knees slightly bent
                2. Hinge at the hips, keeping your back straight
                3. Grip the barbell with hands shoulder-width apart
                4. Pull the bar to your lower chest/upper abs
                5. Lower with control and repeat
            """.trimIndent(),
            proTips = "Keep your back flat throughout. Squeeze your shoulder blades at the top of each rep.",
            imageUrl = "barbell_rows"
        ),
        ExerciseEntity(
            exerciseId = 6,
            name = "Lat Pulldown",
            muscleId = 2,
            equipment = "Cable",
            difficulty = "Beginner",
            type = "Compound",
            instructions = """
                1. Sit at a lat pulldown machine
                2. Grip the bar wider than shoulder width
                3. Pull the bar down to your upper chest
                4. Squeeze your lats at the bottom
                5. Return with control
            """.trimIndent(),
            proTips = "Don't lean back excessively. Focus on pulling with your elbows, not your hands.",
            imageUrl = "lat_pulldown"
        ),
        
        // ============ SHOULDERS (muscleId = 3) ============
        ExerciseEntity(
            exerciseId = 7,
            name = "Overhead Press",
            muscleId = 3, // Shoulders
            equipment = "Barbell",
            difficulty = "Intermediate",
            type = "Compound",
            instructions = """
                1. Stand with feet shoulder-width apart
                2. Hold the barbell at shoulder height
                3. Press the bar straight up overhead
                4. Lock out your arms at the top
                5. Lower with control back to shoulders
            """.trimIndent(),
            proTips = "Squeeze your glutes and core to protect your lower back. Don't arch excessively.",
            imageUrl = "overhead_press"
        ),
        ExerciseEntity(
            exerciseId = 8,
            name = "Lateral Raises",
            muscleId = 3,
            equipment = "Dumbbell",
            difficulty = "Beginner",
            type = "Isolation",
            instructions = """
                1. Stand with dumbbells at your sides
                2. Keep a slight bend in your elbows
                3. Raise arms out to the sides until parallel to ground
                4. Hold briefly at the top
                5. Lower with control
            """.trimIndent(),
            proTips = "Lead with your elbows, not your hands. Use lighter weight with strict form for better results.",
            imageUrl = "lateral_raises"
        ),
        
        // ============ BICEPS (muscleId = 4) ============
        ExerciseEntity(
            exerciseId = 9,
            name = "Barbell Curls",
            muscleId = 4, // Biceps
            equipment = "Barbell",
            difficulty = "Beginner",
            type = "Isolation",
            instructions = """
                1. Stand with feet hip-width apart
                2. Hold barbell with underhand grip at thigh level
                3. Curl the bar up toward your shoulders
                4. Squeeze biceps at the top
                5. Lower with control
            """.trimIndent(),
            proTips = "Keep your elbows pinned to your sides. Don't swing the weight - use controlled movement.",
            imageUrl = "barbell_curls"
        ),
        ExerciseEntity(
            exerciseId = 10,
            name = "Hammer Curls",
            muscleId = 4,
            equipment = "Dumbbell",
            difficulty = "Beginner",
            type = "Isolation",
            instructions = """
                1. Stand with dumbbells at your sides, palms facing in
                2. Keep your elbows close to your body
                3. Curl the weights up without rotating your wrists
                4. Squeeze at the top
                5. Lower with control
            """.trimIndent(),
            proTips = "This exercise also targets the brachialis for thicker arms. Keep strict form for best results.",
            imageUrl = "hammer_curls"
        ),
        
        // ============ TRICEPS (muscleId = 5) ============
        ExerciseEntity(
            exerciseId = 11,
            name = "Tricep Pushdowns",
            muscleId = 5, // Triceps
            equipment = "Cable",
            difficulty = "Beginner",
            type = "Isolation",
            instructions = """
                1. Stand at a cable machine with rope attachment
                2. Keep elbows pinned to your sides
                3. Push the rope down until arms are straight
                4. Spread the rope at the bottom for extra squeeze
                5. Return with control
            """.trimIndent(),
            proTips = "Don't let your elbows flare out. Focus on squeezing the triceps at the bottom.",
            imageUrl = "tricep_pushdowns"
        ),
        ExerciseEntity(
            exerciseId = 12,
            name = "Dips",
            muscleId = 5,
            equipment = "Bodyweight",
            difficulty = "Intermediate",
            type = "Compound",
            instructions = """
                1. Grip parallel bars and lift yourself up
                2. Keep your body upright for tricep focus
                3. Lower until elbows are at 90 degrees
                4. Push back up to the starting position
                5. Keep your core tight
            """.trimIndent(),
            proTips = "Lean forward slightly for more chest, stay upright for more triceps. Add weight when bodyweight becomes easy.",
            imageUrl = "dips"
        ),
        
        // ============ LEGS - QUADS (muscleId = 6) ============
        ExerciseEntity(
            exerciseId = 13,
            name = "Squats",
            muscleId = 6, // Quads
            equipment = "Barbell",
            difficulty = "Intermediate",
            type = "Compound",
            instructions = """
                1. Position bar on upper back, feet shoulder-width apart
                2. Brace your core and unrack the bar
                3. Sit back and down, keeping chest up
                4. Go as low as your mobility allows (at least parallel)
                5. Drive through your heels to stand
            """.trimIndent(),
            proTips = "Keep your knees tracking over your toes. Squat depth is more important than weight.",
            imageUrl = "squats"
        ),
        ExerciseEntity(
            exerciseId = 14,
            name = "Leg Press",
            muscleId = 6,
            equipment = "Machine",
            difficulty = "Beginner",
            type = "Compound",
            instructions = """
                1. Sit in the leg press machine
                2. Place feet shoulder-width on the platform
                3. Release the safety and lower the weight
                4. Push through your heels to extend legs
                5. Don't lock out completely at the top
            """.trimIndent(),
            proTips = "Don't let your lower back round at the bottom. Higher foot placement = more glutes/hamstrings.",
            imageUrl = "leg_press"
        ),
        
        // ============ LEGS - HAMSTRINGS (muscleId = 7) ============
        ExerciseEntity(
            exerciseId = 15,
            name = "Romanian Deadlift",
            muscleId = 7, // Hamstrings
            equipment = "Barbell",
            difficulty = "Intermediate",
            type = "Compound",
            instructions = """
                1. Stand with feet hip-width apart, holding barbell
                2. Keep a slight bend in your knees
                3. Hinge at hips, pushing your butt back
                4. Lower bar along your legs until you feel hamstring stretch
                5. Squeeze glutes to return to standing
            """.trimIndent(),
            proTips = "Keep your back flat throughout. The movement comes from your hips, not your lower back.",
            imageUrl = "romanian_deadlift"
        ),
        ExerciseEntity(
            exerciseId = 16,
            name = "Leg Curls",
            muscleId = 7,
            equipment = "Machine",
            difficulty = "Beginner",
            type = "Isolation",
            instructions = """
                1. Lie face down on the leg curl machine
                2. Position the pad just above your heels
                3. Curl your legs up towards your glutes
                4. Squeeze hamstrings at the top
                5. Lower with control
            """.trimIndent(),
            proTips = "Don't let your hips rise off the pad. Control the negative for maximum muscle activation.",
            imageUrl = "leg_curls"
        ),
        
        // ============ ABS (muscleId = 8) ============
        ExerciseEntity(
            exerciseId = 17,
            name = "Plank",
            muscleId = 8, // Abs
            equipment = "Bodyweight",
            difficulty = "Beginner",
            type = "Isolation",
            instructions = """
                1. Start in a push-up position on your forearms
                2. Keep your body in a straight line
                3. Squeeze your core and glutes
                4. Hold the position for time
                5. Don't let your hips sag or pike
            """.trimIndent(),
            proTips = "Breathe steadily throughout. Start with 30 seconds and work up to 2 minutes.",
            imageUrl = "plank"
        ),
        ExerciseEntity(
            exerciseId = 18,
            name = "Hanging Leg Raises",
            muscleId = 8,
            equipment = "Bodyweight",
            difficulty = "Advanced",
            type = "Isolation",
            instructions = """
                1. Hang from a pull-up bar
                2. Keep your legs straight
                3. Raise legs until parallel to ground (or higher)
                4. Lower with control
                5. Avoid swinging
            """.trimIndent(),
            proTips = "Bend knees to make it easier. Focus on using your abs, not momentum.",
            imageUrl = "hanging_leg_raises"
        ),
        
        // ============ FOREARMS (muscleId = 9) ============
        ExerciseEntity(
            exerciseId = 19,
            name = "Wrist Curls",
            muscleId = 9, // Forearms
            equipment = "Dumbbell",
            difficulty = "Beginner",
            type = "Isolation",
            instructions = """
                1. Sit with forearms resting on thighs, palms up
                2. Hold dumbbells with wrists hanging over knees
                3. Curl the weight up by flexing your wrists
                4. Lower with control
                5. Keep forearms stationary
            """.trimIndent(),
            proTips = "Use lighter weight with high reps. Forearms respond well to volume.",
            imageUrl = "wrist_curls"
        ),
        ExerciseEntity(
            exerciseId = 20,
            name = "Farmer's Walk",
            muscleId = 9,
            equipment = "Dumbbell",
            difficulty = "Beginner",
            type = "Compound",
            instructions = """
                1. Pick up heavy dumbbells at your sides
                2. Stand tall with shoulders back
                3. Walk forward with controlled steps
                4. Keep your core tight
                5. Walk for distance or time
            """.trimIndent(),
            proTips = "Great for grip strength and overall conditioning. Use the heaviest weight you can hold.",
            imageUrl = "farmers_walk"
        )
    )
}
