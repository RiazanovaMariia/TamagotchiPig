package dnu.ffecs.tamagotchipig

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dnu.ffecs.tamagotchipig.ui.theme.ButtonDisabled
import dnu.ffecs.tamagotchipig.ui.theme.ButtonText
import dnu.ffecs.tamagotchipig.ui.theme.Energy
import dnu.ffecs.tamagotchipig.ui.theme.Happiness
import dnu.ffecs.tamagotchipig.ui.theme.Hunger
import dnu.ffecs.tamagotchipig.ui.theme.TitleText
import dnu.ffecs.tamagotchipig.ui.theme.UsualText

@SuppressLint("LocalContextResourcesRead")
@Composable
fun HomeScreen(viewModel: PetViewModel,
               gardenViewModel: GardenViewModel,
               navigateToGarden: () -> Unit,
               goToMathQuiz:()->Unit,
               goToLuckyBox:()->Unit,
               goToGuessNumber:()->Unit,
               goToTapGame:()->Unit) {

    var showSettings by remember { mutableStateOf(false) }
    var showFoodDialog by remember { mutableStateOf(false) }
    var showGameDialog by remember { mutableStateOf(false) }

    // Стан
    val pet by viewModel.pet.collectAsState()

    var isDead = pet.hunger == 0 &&
            pet.energy == 0 &&
            pet.happiness == 0

    if (isDead) {
        GameOverDialog( onReset = { viewModel.reset()
                                    gardenViewModel.resetGarden()
                                    gardenViewModel.resetInventory()})
    }

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){

            // background
            Image(painter= painterResource(R.drawable.main_background),
                contentDescription = "main background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text(text=stringResource(R.string.app_name), style = TitleText)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBar(
                        value = pet.happiness,
                        color = Happiness,
                        icon = "❤️"
                    )

                    StatBar(
                        value = pet.hunger,
                        color = Hunger,
                        icon = "🍗"
                    )

                    StatBar(
                        value = pet.energy,
                        color = Energy,
                        icon = "⚡"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Days alive: ${pet.daysAlive}", style = UsualText)

                Spacer(modifier = Modifier.height(100.dp))

                val imageName = when {
                    pet.isSleeping -> "${pet.petType}_sleep"
                    else -> pet.petType
                }

                val context = LocalContext.current

                val imageResId = context.resources.getIdentifier(
                    imageName,
                    "drawable",
                    context.packageName
                )

                if (imageResId == 0) {
                    Image(
                        painter = painterResource(id = R.drawable.pig),
                        contentDescription = "Pet",
                        modifier = Modifier.size(300.dp)
                    )
                }

                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Pet",
                    modifier = Modifier.size(300.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Кнопки змін хар-к
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),

                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    CustomButton(
                        text = stringResource(R.string.feed),
                        onClick = { showFoodDialog = true },
                        enabled = !isDead && !pet.isSleeping,
                        modifier = Modifier.width(110.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CustomButton(
                        text = stringResource(R.string.sleep),
                        onClick = { viewModel.toggleSleeping() },
                        enabled = !isDead,
                        modifier = Modifier.width(110.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CustomButton(
                        text = stringResource(R.string.play),
                        onClick = { showGameDialog = true },
                        enabled = !isDead && !pet.isSleeping,
                        modifier = Modifier.width(110.dp)
                    )
                }

                CustomButton(
                    text = "Settings",
                    onClick = { showSettings = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                )

                CustomButton(
                    text = "Go to Garden",
                    onClick = navigateToGarden,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
    // Dialogs
    if (showSettings) {
        SettingsDialog(
            onDismiss = { showSettings = false },
            pet,
            viewModel,
            gardenViewModel
        )
    }

    if (showFoodDialog) {
        FoodDialog(
            onDismiss = { showFoodDialog = false },
            viewModel = viewModel,
            gardenViewModel = gardenViewModel
        )
    }

    if (showGameDialog) {
        Dialog(onDismissRequest = { showGameDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
            ) {
                GameChooseScreen(
                    onClose = { showGameDialog = false},
                    goToMathQuiz = goToMathQuiz,
                    goToLuckyBox = goToLuckyBox,
                    goToGuessNumber = goToGuessNumber,
                    goToTapGame = goToTapGame
                )
            }
        }
    }
}

@Composable
fun StatBar(
    value: Int,
    color: Color,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("$icon $value", style = UsualText)

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(90.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.LightGray)
        ) {

            val animatedProgress by animateFloatAsState(
                targetValue = value / 100f,
                label = ""
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(color)
            )
        }
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,

        enabled = enabled,

        modifier = modifier,

        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = ButtonDisabled,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),

        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(text, style = ButtonText)
    }
}