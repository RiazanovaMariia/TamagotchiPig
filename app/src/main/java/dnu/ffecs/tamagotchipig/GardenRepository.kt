package dnu.ffecs.tamagotchipig

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class GardenRepository(context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("garden_data") }
    )

    private object Keys {
        val GARDEN = stringPreferencesKey("garden")
    }

    val gardenFlow: Flow<GardenState> = dataStore.data
        .map { prefs ->
            val json = prefs[Keys.GARDEN]

            if (json.isNullOrEmpty()) {
                createInitialGarden()
            } else {
                parseGarden(json)
            }
        }

    suspend fun saveGarden(state: GardenState) {
        dataStore.edit { prefs ->
            prefs[Keys.GARDEN] = toJson(state)
        }
    }

    suspend fun clearGarden() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.GARDEN)
        }
    }

    // CREATE INITIAL
    private fun createInitialGarden(): GardenState {
        val beds = FoodList.foods.map { food ->
            List(3) {
                GardenBedState(
                    food = food,
                    isPlanted = false,
                    isReady = false,
                    plantedAt = null
                )
            }
        }
        return GardenState(beds)
    }

    // PARSE JSON
    private fun parseGarden(json: String): GardenState {
        val array = org.json.JSONArray(json)

        val beds = mutableListOf<GardenBedState>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)

            val food = FoodList.foods.find {
                it.id == obj.getString("foodId")
            } ?: continue

            beds.add(
                GardenBedState(
                    food = food,
                    isPlanted = obj.getBoolean("isPlanted"),
                    isReady = obj.getBoolean("isReady"),
                    plantedAt = obj.getLong("plantedAt").takeIf { it != -1L }
                )
            )
        }

        return GardenState(beds.chunked(3))
    }

    // TO JSON
    private fun toJson(state: GardenState): String {
        val array = org.json.JSONArray()

        state.beds.flatten().forEach { bed ->
            val obj = org.json.JSONObject()
            obj.put("foodId", bed.food.id)
            obj.put("isPlanted", bed.isPlanted)
            obj.put("isReady", bed.isReady)
            obj.put("plantedAt", bed.plantedAt ?: -1)
            array.put(obj)
        }

        return array.toString()
    }
}
