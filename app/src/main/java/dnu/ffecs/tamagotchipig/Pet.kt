package dnu.ffecs.tamagotchipig

data class Pet(
    var happiness: Int = 100,
    var energy: Int = 100,
    var hunger: Int = 100,
    var daysAlive: Int = 0,
    var lastUpdateTime: Long = System.currentTimeMillis(),
    var lastDayUpdateTime: Long = System.currentTimeMillis(),
    var petType: String = "pig",
    var isSleeping: Boolean = false
)