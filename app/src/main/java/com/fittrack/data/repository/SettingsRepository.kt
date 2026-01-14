package com.fittrack.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.fittrack.ui.home.Gender
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing user settings and preferences.
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _gender = MutableStateFlow(loadGender())
    val gender: StateFlow<Gender> = _gender.asStateFlow()

    /**
     * Update and persist users gender.
     */
    fun setGender(newGender: Gender) {
        prefs.edit().putString(KEY_GENDER, newGender.name).apply()
        _gender.value = newGender
    }

    private fun loadGender(): Gender {
        val genderName = prefs.getString(KEY_GENDER, Gender.MALE.name)
        return try {
            Gender.valueOf(genderName ?: Gender.MALE.name)
        } catch (e: Exception) {
            Gender.MALE
        }
    }

    companion object {
        private const val PREFS_NAME = "fittrack_settings"
        private const val KEY_GENDER = "gender"
    }
}
