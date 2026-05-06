package dnu.ffecs.tamagotchipig

data class Food(
    val id: String,
    val title: String,
    val hungerRestore: Int,
    val growTime: Int
)

object FoodList {
    val foods = listOf(
        Food("cabbage", "Cabbage", 10, 5),
        Food("carrot", "Carrot", 15, 10),
        Food("corn", "Corn", 25, 30)
    )
}

data class FoodInventoryItem(
    val food: Food,
    val amount: Int
)