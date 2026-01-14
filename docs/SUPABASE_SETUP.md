# Supabase Setup Guide for FitTrack

## 📋 Requirements Checklist

### Account & Project Setup
- [ ] Create Supabase account at [supabase.com](https://supabase.com)
- [ ] Create new project named "FitTrack"
- [ ] Note down your project credentials:
  - `Project URL`: `https://xxxxx.supabase.co`
  - `anon (public) key`: For client-side access
  - `service_role key`: For server-side only (keep secret!)

---

## 🔧 Step 1: Create Supabase Project

1. Go to [supabase.com](https://supabase.com) → **Start your project**
2. Sign in with GitHub (recommended) or email
3. Click **New Project**
4. Fill in:
   - **Name**: `FitTrack`
   - **Database Password**: Generate a strong password (save it!)
   - **Region**: Choose closest to your users (e.g., Singapore for Vietnam)
5. Click **Create new project** (takes ~2 minutes)

---

## 🗄️ Step 2: Run Database Schema

After project is created, go to **SQL Editor** and run this schema:

```sql
-- =============================================
-- FITTRACK SUPABASE SCHEMA
-- Run this in Supabase SQL Editor
-- =============================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =============================================
-- USERS TABLE
-- =============================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email TEXT UNIQUE NOT NULL,
    display_name TEXT,
    avatar_url TEXT,
    
    -- Gamification
    level INTEGER DEFAULT 1,
    total_xp INTEGER DEFAULT 0,
    current_streak INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- =============================================
-- WORKOUT LOGS TABLE
-- =============================================
CREATE TABLE workout_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    
    -- Workout Data
    date DATE NOT NULL,
    duration_minutes INTEGER,
    notes TEXT,
    xp_earned INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    
    -- Soft delete
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_workout_logs_user_id ON workout_logs(user_id);
CREATE INDEX idx_workout_logs_date ON workout_logs(date);

-- =============================================
-- EXERCISE LOGS TABLE
-- =============================================
CREATE TABLE exercise_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workout_log_id UUID REFERENCES workout_logs(id) ON DELETE CASCADE,
    exercise_id INTEGER,
    
    -- Set Data (JSONB for flexibility)
    sets JSONB NOT NULL, -- [{"weight": 80, "reps": 10}, ...]
    total_volume INTEGER,
    
    -- PR Tracking
    is_personal_record BOOLEAN DEFAULT FALSE,
    pr_type TEXT, -- 'weight', 'reps', 'volume'
    
    -- Fatigue
    fatigue_added REAL,
    
    -- Timestamps
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_exercise_logs_workout ON exercise_logs(workout_log_id);

-- =============================================
-- PERSONAL RECORDS TABLE
-- =============================================
CREATE TABLE personal_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    exercise_id INTEGER NOT NULL,
    
    -- PR Values
    max_weight REAL,
    max_weight_date DATE,
    max_reps INTEGER,
    max_reps_date DATE,
    max_volume INTEGER,
    max_volume_date DATE,
    
    -- Timestamps
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    
    UNIQUE(user_id, exercise_id)
);

-- =============================================
-- ROW LEVEL SECURITY
-- =============================================
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE workout_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE exercise_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE personal_records ENABLE ROW LEVEL SECURITY;

-- Users can only access their own data
CREATE POLICY "Users access own data" ON users
    FOR ALL USING (auth.uid() = id);

CREATE POLICY "Users access own workouts" ON workout_logs
    FOR ALL USING (auth.uid() = user_id);

CREATE POLICY "Users access own exercise logs" ON exercise_logs
    FOR ALL USING (
        workout_log_id IN (
            SELECT id FROM workout_logs WHERE user_id = auth.uid()
        )
    );

CREATE POLICY "Users access own PRs" ON personal_records
    FOR ALL USING (auth.uid() = user_id);

-- =============================================
-- UPDATED_AT TRIGGER
-- =============================================
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER workout_logs_updated_at
    BEFORE UPDATE ON workout_logs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER exercise_logs_updated_at
    BEFORE UPDATE ON exercise_logs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- =============================================
-- SUCCESS MESSAGE
-- =============================================
SELECT 'FitTrack schema created successfully!' as status;
```

---
## 🔐 Step 3: Configure Authentication

### 3.1 Email/Password Setup
1. Go to **Authentication** → **Providers** → **Email**.
2. Ensure **Enable Email provider** is ON.
3. For development, you may want to toggle **Confirm email** to **OFF** to allow immediate login after sign-up without checking email.

### 3.2 Google OAuth Setup
To enable Google Sign-In for the Android app, you need to configure a Web Client ID for Supabase to handle the exchange.

1. **Google Cloud Console**:
   - Go to [Google Cloud Console](https://console.cloud.google.com).
   - Create a project named `FitTrack`.
   - Go to **APIs & Services** → **OAuth consent screen**.
   - Select **External**, fill in the required app information (App name, User support email).
2. **Create Credentials**:
   - Go to **Credentials** → **Create Credentials** → **OAuth client ID**.
   - Select **Web application** (Note: Supabase requires a Web Client ID to handle the backend authentication exchange, even for mobile applications).
   - Under **Authorized redirect URIs**, add: `https://YOUR_PROJECT_ID.supabase.co/auth/v1/callback` (Replace with your actual Project URL).
   - Click **Create** and copy the **Client ID** and **Client Secret**.
3. **Supabase Dashboard**:
   - Go to **Authentication** → **Providers** → **Google**.
   - Enable the provider.
   - Paste the **Client ID** and **Client Secret** obtained from Google.
   - Click **Save**.

### 3.3 URL Configuration (Deep Linking)
To ensure the app can handle the redirect back from the browser after auth:
1. Go to **Authentication** → **URL Configuration**.
2. Set **Site URL** to: `fittrack://login`
3. Add **Redirect URLs**: `fittrack://*`

---

## 📱 Step 4: Android Project Configuration

### 4.1 Add Dependencies

Add to `app/build.gradle.kts`:

```kotlin
dependencies {
    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:2.0.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:gotrue-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    
    // Ktor client for Supabase
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-utils:2.3.7")
}
```

### 4.2 Add Credentials

Create `local.properties` entry (DO NOT commit to git):

```properties
SUPABASE_URL=https://YOUR_PROJECT_ID.supabase.co
SUPABASE_ANON_KEY=your_anon_key_here
```

Add to `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        // Read from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        
        buildConfigField("String", "SUPABASE_URL", "\"${properties["SUPABASE_URL"]}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${properties["SUPABASE_ANON_KEY"]}\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
}
```

### 4.3 Create SupabaseClient

Create `app/src/main/java/com/fittrack/data/remote/SupabaseClient.kt`:

```kotlin
package com.fittrack.data.remote

import com.fittrack.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(GoTrue) {
            scheme = "fittrack"
            host = "login"
        }
        install(Realtime)
    }
}
```

---

## 🔑 Step 5: Get Your Credentials

1. Go to your Supabase project dashboard
2. Click **Settings** (gear icon) → **API**
3. Copy these values:

| Field | Where to find | Usage |
|-------|---------------|-------|
| **Project URL** | Settings → API | `SUPABASE_URL` |
| **anon public** | Settings → API → Project API keys | `SUPABASE_ANON_KEY` |
| **service_role** | Settings → API → Project API keys | Server-side only (keep secret!) |

---

## ✅ Verification Checklist

After setup, verify everything works:

- [ ] Can access Supabase dashboard
- [ ] SQL schema executed without errors
- [ ] All 4 tables visible in Table Editor
- [ ] RLS policies show as enabled
- [ ] Authentication providers configured
- [ ] Android app builds with Supabase dependencies
- [ ] `SUPABASE_URL` and `SUPABASE_ANON_KEY` in `local.properties`

---

## 🆓 Free Tier Limits

Supabase free tier includes:

| Resource | Limit |
|----------|-------|
| Database | 500 MB |
| Storage | 1 GB |
| Bandwidth | 2 GB/month |
| Auth Users | Unlimited |
| API Requests | Unlimited |
| Realtime Messages | 2 million/month |

> [!TIP]
> The free tier is generous enough for development and early production. Upgrade when you have paying users.

---

## 🚀 Next Steps

After Supabase is set up:

1. [ ] Create `SupabaseDataSource` implementation
2. [ ] Integrate with existing Room database
3. [ ] Implement SyncManager
4. [ ] Test offline → online sync flow
