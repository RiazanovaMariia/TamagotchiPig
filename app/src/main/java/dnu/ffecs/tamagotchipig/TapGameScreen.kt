package dnu.ffecs.tamagotchipig

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dnu.ffecs.tamagotchipig.ui.theme.TitleText
import dnu.ffecs.tamagotchipig.ui.theme.UsualText
import kotlinx.coroutines.delay

@Composable
fun TapGameScreen(
    viewModel: PetViewModel,
    goHome: () -> Unit
) {

    var clicks by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(20) }
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

    // таймер
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

                // таймер
                Text("Time: $timeLeft s", style = UsualText)

                Spacer(Modifier.height(10.dp))

                // кліки
                Text("Clicks: $clicks", style = UsualText)

                Spacer(Modifier.height(40.dp))

                // кнопка (картинка)
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
                        style = UsualText
                    )

                    Text(
                        text = "Happiness +$happinessBonus",
                        style = UsualText
                    )

                    Text(
                        text = "Energy -12, Hunger -5",
                        style = UsualText
                    )
                }

                Spacer(Modifier.weight(1f))

                // вихід
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