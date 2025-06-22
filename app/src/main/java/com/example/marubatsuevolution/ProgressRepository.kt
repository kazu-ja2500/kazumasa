package com.example.marubatsuevolution

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "progress")

class ProgressRepository(private val context: Context) {
    companion object {
        private val CURRENT_GAME_LEVEL_KEY = intPreferencesKey("current_game_level")
        private val HIGHEST_UNLOCKED_LEVEL_KEY = intPreferencesKey("highest_unlocked_level")
    }

    val currentLevel: Flow<Int> = context.progressDataStore.data.map { prefs ->
        prefs[CURRENT_GAME_LEVEL_KEY] ?: 1
    }

    val highestUnlockedLevel: Flow<Int> = context.progressDataStore.data.map { prefs ->
        prefs[HIGHEST_UNLOCKED_LEVEL_KEY] ?: 1
    }

    suspend fun setCurrentGameLevel(value: Int) {
        context.progressDataStore.edit { prefs ->
            prefs[CURRENT_GAME_LEVEL_KEY] = value
        }
    }

    suspend fun setHighestUnlockedLevel(value: Int) {
        context.progressDataStore.edit { prefs ->
            prefs[HIGHEST_UNLOCKED_LEVEL_KEY] = value
        }
    }
}
