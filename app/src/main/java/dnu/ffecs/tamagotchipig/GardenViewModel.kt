package dnu.ffecs.tamagotchipig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GardenViewModel(
    private val foodRepository: FoodRepository,
    private val gardenRepository: GardenRepository
) : ViewModel() {

    private val _garden = MutableStateFlow(createInitialGarden())
    val garden: StateFlow<GardenState> = _garden.asStateFlow()

    val inventory = foodRepository.inventoryFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        observeGarden()
        startTick()
    }

    // 🌱 загрузка из DataStore
    private fun observeGarden() {
        viewModelScope.launch {
            gardenRepository.gardenFlow.collect { state ->
                _garden.value = state
            }
        }
    }

    // 🌱 стартовое состояние (fallback)
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

    // 🌿 посадка
    fun plant(row: Int, col: Int) {
        updateGarden { state ->
            val newState = state.copy(
                beds = state.beds.mapIndexed { r, rowList ->
                    rowList.mapIndexed { c, bed ->
                        if (r == row && c == col && !bed.isPlanted) {
                            bed.copy(
                                isPlanted = true,
                                isReady = false,
                                plantedAt = System.currentTimeMillis()
                            )
                        } else bed
                    }
                }
            )

            save(newState)
            newState
        }
    }

    // 🌾 сбор
    fun harvest(row: Int, col: Int) {
        val bed = _garden.value.beds[row][col]
        if (!bed.isReady) return

        viewModelScope.launch {
            foodRepository.addFood(bed.food)
        }

        updateGarden { state ->
            val newState = state.copy(
                beds = state.beds.mapIndexed { r, rowList ->
                    rowList.mapIndexed { c, b ->
                        if (r == row && c == col) {
                            b.copy(
                                isPlanted = false,
                                isReady = false,
                                plantedAt = null
                            )
                        } else b
                    }
                }
            )

            save(newState)
            newState
        }
    }

    fun useFood(food: Food) {
        viewModelScope.launch {
            foodRepository.subtractFood(food)
        }
    }

    // 💀 reset сада
    fun resetGarden() {
        viewModelScope.launch {
            val fresh = createInitialGarden()
            gardenRepository.clearGarden()
            _garden.value = fresh
        }
    }

    // ⏱ рост растений
    private fun startTick() {
        viewModelScope.launch {
            while (true) {
                delay(1000)

                val now = System.currentTimeMillis()

                updateGarden { state ->
                    val newState = state.copy(
                        beds = state.beds.map { row ->
                            row.map { bed ->
                                if (bed.isPlanted && !bed.isReady && bed.plantedAt != null) {

                                    val passedSeconds =
                                        (now - bed.plantedAt) / 1000

                                    if (passedSeconds >= bed.food.growTime * 60) {
                                        bed.copy(isReady = true)
                                    } else bed

                                } else bed
                            }
                        }
                    )

                    save(newState)
                    newState
                }
            }
        }
    }

    // ⏳ время до готовности
    fun getTimeLeft(bed: GardenBedState): Long {
        val start = bed.plantedAt ?: return 0L
        val elapsed = System.currentTimeMillis() - start
        val total = bed.food.growTime * 1000L * 60
        return (total - elapsed).coerceAtLeast(0L)
    }

    fun resetInventory() {
        viewModelScope.launch {
            foodRepository.clearInventory()
        }
    }

    // 💾 сохранение в DataStore
    private fun save(state: GardenState) {
        viewModelScope.launch {
            gardenRepository.saveGarden(state)
        }
    }

    private fun updateGarden(update: (GardenState) -> GardenState) {
        _garden.value = update(_garden.value)
    }
}

class GardenViewModelFactory(
    private val foodRepository: FoodRepository,
    private val gardenRepository: GardenRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GardenViewModel(foodRepository, gardenRepository) as T
    }
}