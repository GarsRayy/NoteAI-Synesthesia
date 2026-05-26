package com.example.synesthesia.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * User Preferences interface
 */
interface UserPreferences {
    val isDarkMode: Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    
    val themeMode: Flow<String>
    suspend fun setThemeMode(mode: String)
    
    val userName: Flow<String>
    val userBio: Flow<String>
    val userPhotoUri: Flow<String?>
    suspend fun updateProfile(name: String, bio: String, photoUri: String?)
    
    val sortBy: Flow<String>
    suspend fun setSortBy(sortBy: String)
    
    val defaultCategory: Flow<String>
    suspend fun setDefaultCategory(category: String)
    
    val showPreview: Flow<Boolean>
    suspend fun setShowPreview(show: Boolean)
    
    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted()
}

/**
 * User Preferences menggunakan DataStore
 * 
 * DataStore adalah pengganti SharedPreferences yang lebih modern:
 * - Asynchronous dengan Coroutines dan Flow
 * - Type-safe dengan Preferences Keys
 * - Tidak blocking main thread
 * 
 * @param dataStore Instance DataStore dari platform
 */
class UserPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferences {
    // ==================== PREFERENCE KEYS ====================
    
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_BIO = stringPreferencesKey("user_bio")
        val USER_PHOTO_URI = stringPreferencesKey("user_photo_uri")
        val SORT_BY = stringPreferencesKey("sort_by")
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val SHOW_PREVIEW = booleanPreferencesKey("show_preview")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
    
    // ==================== DARK MODE ====================
    
    override val isDarkMode: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: false
    }
    
    override suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    // ==================== THEME MODE ====================

    override val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: "NORMAL"
    }

    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode
        }
    }

    // ==================== PROFILE ====================

    override val userName: Flow<String> = dataStore.data.map { it[Keys.USER_NAME] ?: "Stargazer" }
    override val userBio: Flow<String> = dataStore.data.map { it[Keys.USER_BIO] ?: "Exploring the galaxy of my emotions." }
    override val userPhotoUri: Flow<String?> = dataStore.data.map { it[Keys.USER_PHOTO_URI] }

    override suspend fun updateProfile(name: String, bio: String, photoUri: String?) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
            prefs[Keys.USER_BIO] = bio
            photoUri?.let { prefs[Keys.USER_PHOTO_URI] = it }
        }
    }
    
    // ==================== SORT BY ====================
    
    override val sortBy: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SORT_BY] ?: "UPDATED_DESC"
    }
    
    override suspend fun setSortBy(sortBy: String) {
        dataStore.edit { prefs ->
            prefs[Keys.SORT_BY] = sortBy
        }
    }
    
    // ==================== DEFAULT CATEGORY ====================
    
    override val defaultCategory: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.DEFAULT_CATEGORY] ?: "GENERAL"
    }
    
    override suspend fun setDefaultCategory(category: String) {
        dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_CATEGORY] = category
        }
    }
    
    // ==================== SHOW PREVIEW ====================
    
    override val showPreview: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SHOW_PREVIEW] ?: true
    }
    
    override suspend fun setShowPreview(show: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SHOW_PREVIEW] = show
        }
    }
    
    // ==================== ONBOARDING ====================
    
    override val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }
    
    override suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
        }
    }
}
