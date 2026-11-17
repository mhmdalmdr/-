package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.TutorRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.sudxSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "sudx_student_settings")

enum class AppThemeMode(val titleAr: String) {
    SYSTEM("تلقائي"),
    LIGHT("نهاري"),
    DARK("الوضع الليلي")
}

enum class AcademicStream(val titleAr: String, val subtitleAr: String) {
    SCIENCE("القسم العلمي", "الرياضيات، الفيزياء، الكيمياء، الأحياء"),
    ARTS("القسم الأدبي", "التاريخ، الجغرافيا، الدراسات الإسلامية، الأدب")
}

data class StudentSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val academicStream: AcademicStream = AcademicStream.SCIENCE,
    val defaultTutorRole: TutorRole = TutorRole.GENERAL,
    val searchGroundingDefault: Boolean = true,
    val studyRemindersEnabled: Boolean = true,
    val pomodoroFocusMinutes: Int = 25,
    val pomodoroShortBreakMinutes: Int = 5,
    val pomodoroLongBreakMinutes: Int = 15,
    val pomodoroCompletedSessions: Int = 0,
    val pomodoroTotalFocusMinutes: Int = 0,
    val pomodoroSoundEnabled: Boolean = true,
    val pomodoroAutoStartNext: Boolean = false
)

class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    constructor(context: Context) : this(context.applicationContext.sudxSettingsDataStore)

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACADEMIC_STREAM = stringPreferencesKey("academic_stream")
        val DEFAULT_TUTOR_ROLE = stringPreferencesKey("default_tutor_role")
        val SEARCH_GROUNDING_DEFAULT = booleanPreferencesKey("search_grounding_default")
        val STUDY_REMINDERS_ENABLED = booleanPreferencesKey("study_reminders_enabled")
        val POMODORO_FOCUS_MINUTES = intPreferencesKey("pomodoro_focus_minutes")
        val POMODORO_SHORT_BREAK_MINUTES = intPreferencesKey("pomodoro_short_break_minutes")
        val POMODORO_LONG_BREAK_MINUTES = intPreferencesKey("pomodoro_long_break_minutes")
        val POMODORO_COMPLETED_SESSIONS = intPreferencesKey("pomodoro_completed_sessions")
        val POMODORO_TOTAL_FOCUS_MINUTES = intPreferencesKey("pomodoro_total_focus_minutes")
        val POMODORO_SOUND_ENABLED = booleanPreferencesKey("pomodoro_sound_enabled")
        val POMODORO_AUTO_START_NEXT = booleanPreferencesKey("pomodoro_auto_start_next")
    }

    val settings: Flow<StudentSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val themeMode = prefs[Keys.THEME_MODE]?.let { name ->
                AppThemeMode.entries.firstOrNull { it.name == name }
            } ?: AppThemeMode.SYSTEM

            val academicStream = prefs[Keys.ACADEMIC_STREAM]?.let { name ->
                AcademicStream.entries.firstOrNull { it.name == name }
            } ?: AcademicStream.SCIENCE

            val tutorRole = prefs[Keys.DEFAULT_TUTOR_ROLE]?.let { name ->
                TutorRole.entries.firstOrNull { it.name == name }
            } ?: TutorRole.GENERAL

            StudentSettings(
                themeMode = themeMode,
                academicStream = academicStream,
                defaultTutorRole = tutorRole,
                searchGroundingDefault = prefs[Keys.SEARCH_GROUNDING_DEFAULT] ?: true,
                studyRemindersEnabled = prefs[Keys.STUDY_REMINDERS_ENABLED] ?: true,
                pomodoroFocusMinutes = (prefs[Keys.POMODORO_FOCUS_MINUTES] ?: 25).coerceIn(5, 120),
                pomodoroShortBreakMinutes = (prefs[Keys.POMODORO_SHORT_BREAK_MINUTES] ?: 5).coerceIn(1, 30),
                pomodoroLongBreakMinutes = (prefs[Keys.POMODORO_LONG_BREAK_MINUTES] ?: 15).coerceIn(5, 60),
                pomodoroCompletedSessions = (prefs[Keys.POMODORO_COMPLETED_SESSIONS] ?: 0).coerceAtLeast(0),
                pomodoroTotalFocusMinutes = (prefs[Keys.POMODORO_TOTAL_FOCUS_MINUTES] ?: 0).coerceAtLeast(0),
                pomodoroSoundEnabled = prefs[Keys.POMODORO_SOUND_ENABLED] ?: true,
                pomodoroAutoStartNext = prefs[Keys.POMODORO_AUTO_START_NEXT] ?: false
            )
        }

    suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun setAcademicStream(stream: AcademicStream) {
        dataStore.edit { prefs ->
            prefs[Keys.ACADEMIC_STREAM] = stream.name
        }
    }

    suspend fun setDefaultTutorRole(role: TutorRole) {
        dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_TUTOR_ROLE] = role.name
        }
    }

    suspend fun setSearchGroundingDefault(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SEARCH_GROUNDING_DEFAULT] = enabled
        }
    }

    suspend fun setStudyRemindersEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.STUDY_REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setPomodoroDurations(
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ) {
        dataStore.edit { prefs ->
            prefs[Keys.POMODORO_FOCUS_MINUTES] = focusMinutes.coerceIn(5, 120)
            prefs[Keys.POMODORO_SHORT_BREAK_MINUTES] = shortBreakMinutes.coerceIn(1, 30)
            prefs[Keys.POMODORO_LONG_BREAK_MINUTES] = longBreakMinutes.coerceIn(5, 60)
        }
    }

    suspend fun recordPomodoroFocusCompletion(focusMinutes: Int) {
        dataStore.edit { prefs ->
            val currentCount = prefs[Keys.POMODORO_COMPLETED_SESSIONS] ?: 0
            val currentMinutes = prefs[Keys.POMODORO_TOTAL_FOCUS_MINUTES] ?: 0
            prefs[Keys.POMODORO_COMPLETED_SESSIONS] = currentCount + 1
            prefs[Keys.POMODORO_TOTAL_FOCUS_MINUTES] = currentMinutes + focusMinutes.coerceAtLeast(1)
        }
    }

    suspend fun resetPomodoroStats() {
        dataStore.edit { prefs ->
            prefs[Keys.POMODORO_COMPLETED_SESSIONS] = 0
            prefs[Keys.POMODORO_TOTAL_FOCUS_MINUTES] = 0
        }
    }

    suspend fun setPomodoroSoundEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.POMODORO_SOUND_ENABLED] = enabled
        }
    }

    suspend fun setPomodoroAutoStartNext(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.POMODORO_AUTO_START_NEXT] = enabled
        }
    }
}
