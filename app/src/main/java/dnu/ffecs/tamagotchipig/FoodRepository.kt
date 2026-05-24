package dnu.ffecs.tamagotchipig

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.io.IOException

class FoodRepository(context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        produceFile = { context.preferencesDataStoreFile("food_data") }
    )

    private object Keys {
        val INVENTORY = stringPreferencesKey("inventory")
    }

    // читання інвентаря
    val inventoryFlow: Flow<List<FoodInventoryItem>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            val json = prefs[Keys.INVENTORY] ?: "[]"
            parseInventory(json)
        }

    // додання їжі
    suspend fun addFood(food: Food) {
        dataStore.edit { prefs ->
            val current = parseInventory(prefs[Keys.INVENTORY] ?: "[]").toMutableList()

            val existing = current.find { it.food.id == food.id }

            if (existing != null) {
                val index = current.indexOf(existing)
                current[index] = existing.copy(amount = existing.amount + 1)
            } else {
                current.add(FoodInventoryItem(food, 1))
            }

            prefs[Keys.INVENTORY] = toJson(current)
        }
    }

    suspend fun subtractFood(food: Food) {
        dataStore.edit { prefs ->
            val current = parseInventory(prefs[Keys.INVENTORY] ?: "[]").toMutableList()

            val existing = current.find { it.food.id == food.id }

            if (existing != null) {
                val newAmount = existing.amount - 1

                if (newAmount > 0) {
                    val index = current.indexOf(existing)
                    current[index] = existing.copy(amount = newAmount)
                } else {
                    current.remove(existing)
                }
            }

            prefs[Keys.INVENTORY] = toJson(current)
        }
    }

    suspend fun clearInventory() {
        dataStore.edit { prefs ->
            prefs[Keys.INVENTORY] = "[]"
        }
    }

    // JSON → List
    private fun parseInventory(json: String): List<FoodInventoryItem> {
        val array = JSONArray(json)
        val list = mutableListOf<FoodInventoryItem>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val id = obj.getString("id")
            val amount = obj.getInt("amount")

            val food = FoodList.foods.find { it.id == id } ?: continue
            list.add(FoodInventoryItem(food, amount))
        }

        return list
    }

    // List → JSON
    private fun toJson(list: List<FoodInventoryItem>): String {
        val array = JSONArray()

        list.forEach {
            val obj = org.json.JSONObject()
            obj.put("id", it.food.id)
            obj.put("amount", it.amount)
            array.put(obj)
        }

        return array.toString()
    }
}