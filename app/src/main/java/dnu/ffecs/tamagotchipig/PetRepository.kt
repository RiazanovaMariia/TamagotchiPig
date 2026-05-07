package dnu.ffecs.tamagotchipig

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PetRepository(context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile("pet_data") }
    )

    private object Keys {
        val HAPPINESS = intPreferencesKey("happiness")
        val ENERGY = intPreferencesKey("energy")
        val HUNGER = intPreferencesKey("hunger")
        val DAYS = intPreferencesKey("days")
        val LAST_TIME = longPreferencesKey("last_time")
        val LAST_DAY_TIME = longPreferencesKey("last_day_time")
        val PET_TYPE = stringPreferencesKey("pet_type")
        val IS_SLEEPING = booleanPreferencesKey("is_sleeping")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val petFlow: Flow<Pet> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            Pet(
                hunger = prefs[Keys.HUNGER] ?: 100,
                energy = prefs[Keys.ENERGY] ?: 100,
                happiness = prefs[Keys.HAPPINESS] ?: 100,
                daysAlive = prefs[Keys.DAYS] ?: 0,
                lastUpdateTime = prefs[Keys.LAST_TIME] ?: System.currentTimeMillis(),
                lastDayUpdateTime = prefs[Keys.LAST_DAY_TIME] ?: System.currentTimeMillis(),
                petType = prefs[Keys.PET_TYPE] ?: "pig",
                isSleeping = prefs[Keys.IS_SLEEPING] == true
            )
        }

    val themeFlow: Flow<ThemeMode> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            when (prefs[Keys.THEME_MODE]) {
                "LIGHT" -> ThemeMode.LIGHT
                "DARK" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        }

    suspend fun saveTheme(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.name
        }
    }

    suspend fun savePet(pet: Pet) {
        dataStore.edit { prefs ->
            prefs[Keys.HUNGER] = pet.hunger
            prefs[Keys.ENERGY] = pet.energy
            prefs[Keys.HAPPINESS] = pet.happiness
            prefs[Keys.DAYS] = pet.daysAlive
            prefs[Keys.LAST_TIME] = pet.lastUpdateTime
            prefs[Keys.LAST_DAY_TIME] = pet.lastDayUpdateTime
            prefs[Keys.PET_TYPE] = pet.petType
            prefs[Keys.IS_SLEEPING] = pet.isSleeping
        }
    }
}

