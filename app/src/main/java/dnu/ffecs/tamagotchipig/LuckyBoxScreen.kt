package dnu.ffecs.tamagotchipig

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dnu.ffecs.tamagotchipig.ui.theme.ButtonColor
import dnu.ffecs.tamagotchipig.ui.theme.ButtonStroke
import dnu.ffecs.tamagotchipig.ui.theme.ButtonText
import dnu.ffecs.tamagotchipig.ui.theme.TextDark
import dnu.ffecs.tamagotchipig.ui.theme.TitleText

@Composable
fun LuckyBoxScreen(
    viewModel: PetViewModel,
    goHome: () -> Unit
) {

    var idList by remember { mutableStateOf(listOf(1, 2, 3, 4)) }
    var selectedBox by remember { mutableStateOf<Int?>(null) }
    var resultText by remember { mutableStateOf("") }

    fun startGame() {
        idList = listOf(1, 2, 3, 4).shuffled()
        selectedBox = null
        resultText = ""
    }

    LaunchedEffect(Unit) {
        startGame()
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

                Spacer(Modifier.height(50.dp))

                Text("Lucky Boxes", style = TitleText)

                Spacer(Modifier.height(20.dp))

                Text("Choose any box to check your luck", style = ButtonText)

                Spacer(Modifier.height(75.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LuckyBox(
                        idList[0],
                        "Lucky Box №1",
                        viewModel,
                        selectedBox,
                        onSelected = { id, text ->
                            selectedBox = id
                            resultText = text
                        }
                    )
                    LuckyBox(
                        idList[1],
                        "Lucky Box №2",
                        viewModel,
                        selectedBox,
                        onSelected = { id, text ->
                            selectedBox = id
                            resultText = text
                        }
                    )
                }

                Spacer(Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LuckyBox(
                        idList[2],
                        "Lucky Box №3",
                        viewModel,
                        selectedBox,
                        onSelected = { id, text ->
                            selectedBox = id
                            resultText = text
                        }
                    )
                    LuckyBox(
                        idList[3],
                        "Lucky Box №4",
                        viewModel,
                        selectedBox,
                        onSelected = { id, text ->
                            selectedBox = id
                            resultText = text
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // result
                if (resultText.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))
                    Text(resultText, style = ButtonText,
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }

                Spacer(Modifier.weight(1f))

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

@Composable
fun LuckyBox(
    id: Int,
    text: String,
    viewModel: PetViewModel,
    selectedBox: Int?,
    onSelected: (Int, String) -> Unit
) {

    val isDisabled = selectedBox != null

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .size(130.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .clickable(enabled = !isDisabled) {

                    val result = when (id) {
                        1 -> {
                            viewModel.changeStats(-15, -15, -5)
                            "😢 Bad luck: -15 hunger, -15 energy, -5 happiness"
                        }
                        2 -> {
                            viewModel.changeStats(5, 2, 0)
                            "🙂 Small bonus! +5 hunger, +2 energy, 0 happiness"
                        }
                        3 -> {
                            viewModel.changeStats(-2, -7, 15)
                            "😊 Nice! +15 happiness, -2 hunger, -7 energy"
                        }
                        4 -> {
                            viewModel.changeStats(5, 2, 40)
                            "🎉 JACKPOT! +40 happiness, +5 hunger, +2 energy"
                        }
                        else -> ""
                    }

                    onSelected(id, result)
                },
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(R.drawable.lucky_box),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(text, style = ButtonText)
    }
}
