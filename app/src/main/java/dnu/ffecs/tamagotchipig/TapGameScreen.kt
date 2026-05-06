package dnu.ffecs.tamagotchipig

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dnu.ffecs.tamagotchipig.ui.theme.ButtonColor
import dnu.ffecs.tamagotchipig.ui.theme.ButtonStroke
import dnu.ffecs.tamagotchipig.ui.theme.ButtonText
import dnu.ffecs.tamagotchipig.ui.theme.TextDark
import dnu.ffecs.tamagotchipig.ui.theme.TitleText
import kotlinx.coroutines.delay

@Composable
fun TapGameScreen(
    viewModel: PetViewModel,
    goHome: () -> Unit
) {

    var clicks by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(20) }
    var isRunning by remember { mutableStateOf(true) }
    var finished by remember { mutableStateOf(false) }

    fun applyResult() {
        val happinessBonus = (clicks / 10) * 3

        viewModel.changeStats(
            energyDelta = -12,
            hungerDelta = -5,
            happinessDelta = happinessBonus
        )
    }

    // ⏱ таймер
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isRunning = false
            finished = true
            applyResult()
        }
    }

    Scaffold { padding ->

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(R.drawable.game_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(40.dp))

                Text("Tap Game", style = TitleText)

                Spacer(Modifier.height(20.dp))

                // ⏱ таймер
                Text("Time: $timeLeft s", style = ButtonText)

                Spacer(Modifier.height(10.dp))

                // 👆 клики
                Text("Clicks: $clicks", style = ButtonText)

                Spacer(Modifier.height(40.dp))

                // 🎯 кнопка (картинка)
                Image(
                    painter = painterResource(R.drawable.tap_button),
                    contentDescription = "tap button",
                    modifier = Modifier
                        .size(250.dp)
                        .clickable(
                            enabled = isRunning,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            clicks++
                        }
                )

                Spacer(Modifier.height(30.dp))

                if (finished) {

                    val happinessBonus = (clicks / 10) * 3

                    Text(
                        text = "⏰ Time's up!",
                        style = TitleText
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "Clicks: $clicks",
                        style = ButtonText
                    )

                    Text(
                        text = "Happiness +$happinessBonus",
                        style = ButtonText
                    )

                    Text(
                        text = "Energy -12, Hunger -5",
                        style = ButtonText
                    )
                }

                Spacer(Modifier.weight(1f))

                // ❌ выход
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    onClick = goHome,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor,
                        contentColor = TextDark
                    ),
                    border = BorderStroke(2.dp, ButtonStroke)
                ) {
                    Text("Go Home", style = ButtonText)
                }
            }
        }
    }
}