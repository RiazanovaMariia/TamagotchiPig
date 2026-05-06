package dnu.ffecs.tamagotchipig

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dnu.ffecs.tamagotchipig.ui.theme.TamagotchiPigTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.text.set

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            TamagotchiPigTheme {
                val backStack = rememberNavBackStack(HomeRoute)

                val repository = PetRepository(applicationContext)
                val foodRepository = FoodRepository(applicationContext)
                val gardenRepository = GardenRepository(applicationContext)

                val viewModel: PetViewModel = viewModel(
                    factory = PetViewModelFactory(repository)
                )

                val gardenViewModel: GardenViewModel = viewModel(
                    factory = GardenViewModelFactory(foodRepository,gardenRepository)
                )

                fun navigateToGarden() {
                    val route = GardenRoute
                    if (backStack.lastOrNull() is GardenRoute) {
                        backStack[backStack.lastIndex] = route
                    } else {
                        backStack.add(route)
                    }
                }

                fun navigateToMathQuiz() {
                    val route = MathQuizRoute
                    if (backStack.lastOrNull() is MathQuizRoute) {
                        backStack[backStack.lastIndex] = route
                    } else {
                        backStack.add(route)
                    }
                }

                fun navigateToLuckyBox() {
                    val route = LuckyBoxRoute
                    if (backStack.lastOrNull() is LuckyBoxRoute) {
                        backStack[backStack.lastIndex] = route
                    } else {
                        backStack.add(route)
                    }
                }

                fun navigateToGuessNumber() {
                    val route = GuessNumberRoute
                    if (backStack.lastOrNull() is GuessNumberRoute) {
                        backStack[backStack.lastIndex] = route
                    } else {
                        backStack.add(route)
                    }
                }

                fun navigateToTapGame() {
                    val route = TapGameRoute
                    if (backStack.lastOrNull() is TapGameRoute) {
                        backStack[backStack.lastIndex] = route
                    } else {
                        backStack.add(route)
                    }
                }

                fun goHome() {
                    val route = HomeRoute
                    if (backStack.lastOrNull() is HomeRoute) {
                        backStack[backStack.lastIndex] = route
                    } else {
                        backStack.removeLastOrNull()
                    }
                }

                AppNavigation(
                    backStack = backStack,
                    petViewModel = viewModel,
                    gardenViewModel = gardenViewModel,
                    navigateToGarden = { navigateToGarden() },
                    goHome = { goHome() },
                    goToMathQuiz = {navigateToMathQuiz()},
                    goToLuckyBox = {navigateToLuckyBox()},
                    goToGuessNumber = {navigateToGuessNumber()},
                    goToTapGame = {navigateToTapGame()},
                )
            }
        }
    }
}

@Composable
fun AppNavigation(backStack: NavBackStack<NavKey>,
                  petViewModel: PetViewModel,
                  gardenViewModel: GardenViewModel,
                  navigateToGarden: ()->Unit,
                  goHome:()->Unit,
                  goToMathQuiz:()->Unit,
                  goToLuckyBox:()->Unit,
                  goToGuessNumber:()->Unit,
                  goToTapGame:()->Unit){
    NavDisplay(
        backStack = backStack,
        onBack = { if (backStack.size > 1)
            backStack.removeAt(backStack.lastIndex) },
        entryProvider = entryProvider {
            entry<HomeRoute> {
                HomeScreen(petViewModel, gardenViewModel, navigateToGarden,
                    goToMathQuiz, goToLuckyBox, goToGuessNumber, goToTapGame, goHome)
            }
            entry<GardenRoute> {
                GardenScreen(goHome, gardenViewModel)
            }
            entry<MathQuizRoute> {
                MathQuizScreen(petViewModel, goHome)
            }
            entry<LuckyBoxRoute> {
                LuckyBoxScreen(petViewModel, goHome)
            }
            entry<GuessNumberRoute> {
                GuessNumScreen(petViewModel, goHome)
            }
            entry<TapGameRoute> {
                TapGameScreen(petViewModel, goHome)
            }
        }
    )
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    pet: Pet,
    viewModel: PetViewModel,
    gardenViewModel: GardenViewModel
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = { SettingsContent(pet,viewModel, gardenViewModel) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun FoodDialog(
    onDismiss: () -> Unit,
    viewModel: PetViewModel,
    gardenViewModel: GardenViewModel
) {
    val inventory by gardenViewModel.inventory.collectAsState(initial = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose food") },
        text = {
            Column {

                if (inventory.isEmpty()) {
                    Text(
                        text = "No food available 😢",
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {

                    inventory.forEach { item ->

                        val food = item.food

                        Button(
                            onClick = {
                                if (item.amount > 0) {
                                    viewModel.feed(food.hungerRestore)
                                    gardenViewModel.useFood(food)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = item.amount > 0
                        ) {
                            Text("${food.title} (${item.amount}) - +${food.hungerRestore}")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Composable
fun SettingsContent(pet: Pet,viewModel: PetViewModel,gardenViewModel: GardenViewModel){
    var showHelp by remember { mutableStateOf(false) }
    var resetDialog by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf(pet.petType) }

    Column (horizontalAlignment = Alignment.CenterHorizontally){

        Text("Select your pet")

        Box(modifier = Modifier.fillMaxWidth()) {

            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pet: $selectedPet")
                Spacer(modifier = Modifier.weight(1f))
                Text("▼")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {

                DropdownMenuItem(
                    text = { Text("pig") },
                    onClick = {
                        selectedPet = "pig"
                        viewModel.setPetType("pig")
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("cow") },
                    onClick = {
                        selectedPet = "cow"
                        viewModel.setPetType("cow")
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("goose") },
                    onClick = {
                        selectedPet = "goose"
                        viewModel.setPetType("goose")
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {showHelp=true}, modifier = Modifier.fillMaxWidth()){
            Text("Help me!")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                resetDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset your progress")
        }

        if(resetDialog) {
            ResetDialog(
                onDismiss = { resetDialog = false },
                onConfirm = {
                    viewModel.reset()
                    gardenViewModel.resetGarden()
                    gardenViewModel.resetInventory()
                    resetDialog = false
                })

        }

        if(showHelp){
            InstructionsDialog { showHelp = false }
        }
    }
}

@Composable
fun ResetDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset game") },
        text = {
            Text("Are you sure you want to reset your progress?")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Reset")
            }
        }
    )
}

@Composable
fun InstructionsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            HelpContent()
               },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun HelpContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = "How to play",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Take care of your pet: feed, play, and let it sleep to keep it happy.")

        Spacer(modifier = Modifier.height(8.dp))

        Text("Stats (hunger, energy, happiness) decrease over time.")

        Spacer(modifier = Modifier.height(8.dp))

        Text("If one or more stats reach 0, the others will decrease faster.")

        Spacer(modifier = Modifier.height(8.dp))

        Text("While you are in the game, stats decrease faster than when you are offline.")

        Spacer(modifier = Modifier.height(8.dp))

        Text("If all stats reach 0 — the game is over.")

        Spacer(modifier = Modifier.height(8.dp))

        Text("You can always reset your progress in Settings.")
    }
}

@Composable
fun GameOverDialog(
    onReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {}, //неможливо закрити
        title = { Text("Game Over") },
        text = { Text("Your pet has died...") },
        confirmButton = {
            Button(onClick = onReset) {
                Text("Restart")
            }
        }
    )
}