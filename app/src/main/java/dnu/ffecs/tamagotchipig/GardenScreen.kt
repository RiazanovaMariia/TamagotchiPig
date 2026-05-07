package dnu.ffecs.tamagotchipig

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dnu.ffecs.tamagotchipig.ui.theme.ButtonColor
import dnu.ffecs.tamagotchipig.ui.theme.ButtonDisabled
import dnu.ffecs.tamagotchipig.ui.theme.ButtonStroke
import dnu.ffecs.tamagotchipig.ui.theme.ButtonText
import dnu.ffecs.tamagotchipig.ui.theme.LightText
import dnu.ffecs.tamagotchipig.ui.theme.TextDark
import dnu.ffecs.tamagotchipig.ui.theme.TextLight
import dnu.ffecs.tamagotchipig.ui.theme.TitleText
import dnu.ffecs.tamagotchipig.ui.theme.UsualText
import kotlinx.coroutines.delay

@Composable
fun GardenScreen(goHome: () -> Unit,
                 viewModel: GardenViewModel){
    val gardenState by viewModel.garden.collectAsState()

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){ //padding?

            // background
            Image(painter= painterResource(R.drawable.garden_background),
                  contentDescription = "garden background",
                  modifier = Modifier.fillMaxSize(),
                  contentScale = ContentScale.Crop)

            Column (modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center){

                Spacer(Modifier.height(50.dp))

                Text("GARDEN", style = TitleText)

                Spacer(Modifier.height(75.dp))

                gardenState.beds.forEachIndexed { rowIndex, row ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEachIndexed { colIndex, bed ->
                            GardenBed(
                                bed = bed,
                                onPlant = { viewModel.plant(rowIndex, colIndex) },
                                onHarvest = { viewModel.harvest(rowIndex, colIndex) },
                                gardenViewModel = viewModel
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                }

                CustomButton(
                    text = "Go Home",
                    onClick = goHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun GardenBed(
    bed: GardenBedState,
    onPlant: () -> Unit,
    onHarvest: () -> Unit,
    gardenViewModel: GardenViewModel
) {
    val timeLeft = remember(bed.plantedAt, bed.isPlanted) {
        mutableStateOf(0L)
    }

    LaunchedEffect(bed.plantedAt, bed.isPlanted) {
        while (bed.isPlanted && bed.plantedAt != null) {
            timeLeft.value = gardenViewModel.getTimeLeft(bed)
            delay(1000)
        }
    }

    val totalSeconds = timeLeft.value / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = bed.food.title,
            style = UsualText
        )

        // таймер
        if (bed.isPlanted && !bed.isReady) {
            Text(
                text = if (minutes > 0 || seconds > 0)
                    String.format("%d:%02d", minutes, seconds)
                else
                    "Ready!",
                style = LightText
            )
        } else  if (bed.isReady){
            Text( "Ready !", style = LightText)
        } else {
            Text( "Plant me", style = LightText)
        }

        Box(
            modifier = Modifier
                .size(75.dp),
            contentAlignment = Alignment.Center
        ) {
            val imageRes = when {
                bed.isReady && bed.food.id == "carrot" -> R.drawable.carrot_garden_bed
                bed.isReady && bed.food.id == "cabbage" -> R.drawable.cabbage_garden_bed
                bed.isReady && bed.food.id == "corn" -> R.drawable.corn_garden_bed
                bed.isPlanted -> R.drawable.sprout_garden_bed
                else -> R.drawable.empty_garden_bed
            }

            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

        }

        val (buttonText, enabled) = when {
            !bed.isPlanted -> "🌱 Plant" to true
            bed.isReady -> "🌼 Take" to true
            else -> "⏳ Grow" to false
        }

        Button( modifier = Modifier.width(100.dp),
            onClick = {
                if (!bed.isPlanted) {
                    onPlant()
                } else if (bed.isReady) {
                    onHarvest()
                }
            },
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = ButtonDisabled,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
        ) {
            Text(buttonText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

data class GardenBedState(
    val food: Food,
    val isPlanted: Boolean = false,
    val isReady: Boolean = false,
    val plantedAt: Long? = null,
)

data class GardenState(
    val beds: List<List<GardenBedState>>
)
