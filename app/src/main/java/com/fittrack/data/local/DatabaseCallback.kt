package com.fittrack.data.local

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fittrack.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Database callback to prepopulate sample data on first run.
 */
class DatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        CoroutineScope(Dispatchers.IO).launch {
            // Insert sample muscles
            db.execSQL("""
                INSERT INTO muscles (muscleId, name, svgPathId) VALUES 
                (1, 'Chest', 'muscle_chest_left'),
                (2, 'Back', 'muscle_lat_left'),
                (3, 'Shoulders', 'muscle_deltoid_left'),
                (4, 'Biceps', 'muscle_bicep_left'),
                (5, 'Triceps', 'muscle_tricep_left'),
                (6, 'Abs', 'muscle_abs'),
                (7, 'Quads', 'muscle_quad_left'),
                (8, 'Hamstrings', 'muscle_hamstring_left'),
                (9, 'Glutes', 'muscle_glute_left'),
                (10, 'Calves', 'muscle_calf_left')
            """.trimIndent())

            // Insert sample exercises
            db.execSQL("""
                INSERT INTO exercises (exerciseId, name, muscleId, regionId, equipment, difficulty, instructions) VALUES 
                (1, 'Bench Press', 1, NULL, 'Barbell', 'Intermediate', '1. Lie flat on a bench\n2. Grip the bar slightly wider than shoulders\n3. Lower bar to chest\n4. Press back up'),
                (2, 'Push-ups', 1, NULL, 'Bodyweight', 'Beginner', '1. Start in plank position\n2. Lower chest to floor\n3. Push back up\n4. Keep core tight'),
                (3, 'Dumbbell Fly', 1, NULL, 'Dumbbell', 'Intermediate', '1. Lie on bench with dumbbells\n2. Arms extended above chest\n3. Lower arms in an arc\n4. Squeeze chest to return'),
                (4, 'Cable Crossover', 1, NULL, 'Cable', 'Advanced', '1. Stand between cable stations\n2. Grip handles at shoulder height\n3. Bring hands together\n4. Squeeze and return'),
                
                (5, 'Pull-ups', 2, NULL, 'Bodyweight', 'Intermediate', '1. Hang from bar with overhand grip\n2. Pull up until chin over bar\n3. Lower with control'),
                (6, 'Lat Pulldown', 2, NULL, 'Machine', 'Beginner', '1. Sit at machine\n2. Grip bar wide\n3. Pull to upper chest\n4. Control the return'),
                (7, 'Barbell Row', 2, NULL, 'Barbell', 'Intermediate', '1. Bend at hips, back flat\n2. Pull bar to lower chest\n3. Squeeze shoulder blades\n4. Lower with control'),
                (8, 'Dumbbell Row', 2, NULL, 'Dumbbell', 'Beginner', '1. One hand on bench\n2. Pull dumbbell to hip\n3. Squeeze back at top\n4. Lower slowly'),
                
                (9, 'Overhead Press', 3, NULL, 'Barbell', 'Intermediate', '1. Hold bar at shoulders\n2. Press overhead\n3. Lock out arms\n4. Lower with control'),
                (10, 'Lateral Raise', 3, NULL, 'Dumbbell', 'Beginner', '1. Hold dumbbells at sides\n2. Raise arms to sides\n3. Stop at shoulder height\n4. Lower slowly'),
                (11, 'Face Pulls', 3, NULL, 'Cable', 'Beginner', '1. Set cable at face height\n2. Pull rope to face\n3. Squeeze rear delts\n4. Return slowly'),
                
                (12, 'Bicep Curl', 4, NULL, 'Dumbbell', 'Beginner', '1. Hold dumbbells at sides\n2. Curl up to shoulders\n3. Squeeze at top\n4. Lower slowly'),
                (13, 'Barbell Curl', 4, NULL, 'Barbell', 'Beginner', '1. Hold barbell underhand\n2. Curl to chest\n3. Keep elbows fixed\n4. Lower with control'),
                (14, 'Hammer Curl', 4, NULL, 'Dumbbell', 'Beginner', '1. Hold dumbbells neutral grip\n2. Curl up\n3. Keep thumbs up\n4. Lower slowly'),
                
                (15, 'Tricep Pushdown', 5, NULL, 'Cable', 'Beginner', '1. Hold cable at chest\n2. Push down until arms straight\n3. Keep elbows fixed\n4. Return slowly'),
                (16, 'Skull Crushers', 5, NULL, 'Barbell', 'Intermediate', '1. Lie on bench, bar overhead\n2. Lower bar to forehead\n3. Extend arms\n4. Keep elbows still'),
                (17, 'Dips', 5, NULL, 'Bodyweight', 'Intermediate', '1. Support on parallel bars\n2. Lower until arms at 90°\n3. Push back up\n4. Keep core tight'),
                
                (18, 'Crunches', 6, NULL, 'Bodyweight', 'Beginner', '1. Lie on back, knees bent\n2. Curl shoulders up\n3. Squeeze abs at top\n4. Lower slowly'),
                (19, 'Plank', 6, NULL, 'Bodyweight', 'Beginner', '1. Hold forearm plank\n2. Keep body straight\n3. Engage core\n4. Hold for time'),
                (20, 'Leg Raises', 6, NULL, 'Bodyweight', 'Intermediate', '1. Lie flat on back\n2. Raise legs to 90°\n3. Lower slowly\n4. Don''t touch floor'),
                
                (21, 'Squats', 7, NULL, 'Barbell', 'Intermediate', '1. Bar on upper back\n2. Squat until thighs parallel\n3. Keep chest up\n4. Drive through heels'),
                (22, 'Leg Press', 7, NULL, 'Machine', 'Beginner', '1. Sit in leg press\n2. Lower weight\n3. Push through heels\n4. Don''t lock knees'),
                (23, 'Lunges', 7, NULL, 'Bodyweight', 'Beginner', '1. Step forward\n2. Lower back knee\n3. Push through front heel\n4. Alternate legs'),
                
                (24, 'Romanian Deadlift', 8, NULL, 'Barbell', 'Intermediate', '1. Hold bar, slight knee bend\n2. Hinge at hips\n3. Lower bar down legs\n4. Squeeze glutes to rise'),
                (25, 'Leg Curl', 8, NULL, 'Machine', 'Beginner', '1. Lie face down on machine\n2. Curl heels to glutes\n3. Squeeze at top\n4. Lower slowly'),
                
                (26, 'Hip Thrust', 9, NULL, 'Barbell', 'Intermediate', '1. Back against bench\n2. Bar across hips\n3. Thrust hips up\n4. Squeeze glutes at top'),
                (27, 'Glute Bridge', 9, NULL, 'Bodyweight', 'Beginner', '1. Lie on back, knees bent\n2. Push hips up\n3. Squeeze glutes\n4. Lower slowly'),
                
                (28, 'Calf Raise', 10, NULL, 'Machine', 'Beginner', '1. Stand on calf machine\n2. Rise onto toes\n3. Hold at top\n4. Lower slowly'),
                (29, 'Seated Calf Raise', 10, NULL, 'Machine', 'Beginner', '1. Sit at calf machine\n2. Push through toes\n3. Full range of motion\n4. Control descent')
            """.trimIndent())
        }
    }
}
