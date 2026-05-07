package dnu.ffecs.tamagotchipig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.ranges.coerceAtLeast
import kotlin.ranges.coerceAtMost

class PetViewModel(
    private val repository: PetRepository
) : ViewModel() {

    private val _pet = MutableStateFlow(Pet())
    val pet = _pet.asStateFlow()

    init {
        observePet()
        startLiveTick()
    }

    // читання з DataStore
    private fun observePet() {
        viewModelScope.launch {
            repository.petFlow.collect { petFromDb ->
                val processed = applyOfflineProgress(petFromDb)
                _pet.value = processed
            }
        }
    }

    // offline прогрес
    private fun applyOfflineProgress(pet: Pet): Pet {
        val now = System.currentTimeMillis()
        val delta = now - pet.lastUpdateTime

        val deltaDays = now - pet.lastDayUpdateTime
        val daysPassed = (deltaDays / (1000 * 60 * 60 * 24)).toInt()

        val hours = (delta / (1000 * 60 * 60)).toInt()
        if (hours <= 0) return pet

        val emptyBars = listOf(
            pet.hunger,
            pet.energy,
            pet.happiness
        ).count { it <= 0 }

        if (emptyBars >= 3) {
            return pet.copy(
                hunger = 0,
                energy = 0,
                happiness = 0
            )
        }

        val multiplier = when (emptyBars) {
            0 -> 3
            1 -> 8
            2 -> 13
            else -> 3
        }

        val baseDrop = (hours * multiplier).toInt()
        val remainder = delta % (1000 * 60 * 60 * 24)

        return if (pet.isSleeping) {
            pet.copy(
                hunger = (pet.hunger - baseDrop).coerceAtLeast(0),
                energy = (pet.energy + baseDrop*3).coerceAtMost(100),
                happiness = (pet.happiness - baseDrop).coerceAtLeast(0),
                daysAlive = pet.daysAlive + daysPassed,
                lastUpdateTime = now,
                lastDayUpdateTime = now - remainder
            )
        } else {
            pet.copy(
                hunger = (pet.hunger - baseDrop).coerceAtLeast(0),
                energy = (pet.energy - baseDrop).coerceAtLeast(0),
                happiness = (pet.happiness - baseDrop).coerceAtLeast(0),
                daysAlive = pet.daysAlive + daysPassed,
                lastUpdateTime = now,
                lastDayUpdateTime = now - remainder
            )
        }
    }

    // головний цикл
    private fun startLiveTick() {
        viewModelScope.launch {
            while (true) {
                delay(60000)

                val pet = _pet.value

                val updated = pet.copy(
                    hunger = (pet.hunger - 1).coerceAtLeast(0),

                    energy = if (pet.isSleeping) {
                        (pet.energy + 2).coerceAtMost(100)
                    } else {
                        (pet.energy - 1).coerceAtLeast(0)
                    },

                    happiness = (pet.happiness - 1).coerceAtLeast(0),
                    lastUpdateTime = System.currentTimeMillis()
                )

                _pet.value = updated
                repository.savePet(updated)
            }
        }
    }

    // FEED
    fun feed(amount: Int) {
        updateState {
            it.copy(
                hunger = (it.hunger + amount).coerceAtMost(100),
                happiness = (it.happiness + 5).coerceAtMost(100),
                energy = (it.energy - 10).coerceAtLeast(0),
                lastUpdateTime = System.currentTimeMillis()
            )
        }
    }

    fun changeStats(
        hungerDelta: Int = 0,
        energyDelta: Int = 0,
        happinessDelta: Int = 0
    ) {
        updateState {
            it.copy(
                hunger = (it.hunger + hungerDelta).coerceIn(0, 100),
                energy = (it.energy + energyDelta).coerceIn(0, 100),
                happiness = (it.happiness + happinessDelta).coerceIn(0, 100),
                lastUpdateTime = System.currentTimeMillis()
            )
        }
    }

    fun reset() {
        val newPet = Pet(
            hunger = 100,
            happiness = 100,
            energy = 100,
            daysAlive = 0,
            lastUpdateTime = System.currentTimeMillis(),
            lastDayUpdateTime = System.currentTimeMillis()
        )

        _pet.value = newPet

        viewModelScope.launch {
            repository.savePet(newPet)
        }
    }

    // updater
    private fun updateState(update: (Pet) -> Pet) {
        val newPet = update(_pet.value)
        _pet.value = newPet

        viewModelScope.launch {
            repository.savePet(newPet)
        }
    }

    fun toggleSleeping() {
        updateState {
            it.copy(isSleeping = !it.isSleeping)
        }
    }

    fun setPetType(type: String) {
        updateState {
            it.copy(petType = type)
        }
    }

    val themeMode = repository.themeFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ThemeMode.SYSTEM
    )

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            repository.saveTheme(mode)
        }
    }

}

class PetViewModelFactory(
    private val repository: PetRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PetViewModel(repository) as T
    }
}