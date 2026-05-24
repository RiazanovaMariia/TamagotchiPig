package dnu.ffecs.tamagotchipig

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dnu.ffecs.tamagotchipig.ui.theme.TextDark
import dnu.ffecs.tamagotchipig.ui.theme.TitleText
import dnu.ffecs.tamagotchipig.ui.theme.UsualText

@Composable
fun GuessNumScreen(
    viewModel: PetViewModel,
    goHome: () -> Unit
) {

    var targetNumber by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("") }
    var attempts by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    val maxAttempts = 5

    fun startGame() {
        targetNumber = (1..100).random()
        userInput = ""
        hint = ""
        attempts = 0
        finished = false
        resultText = ""
        errorText = ""
    }

    fun checkAnswer() {
        val number = userInput.toIntOrNull()

        // не число
        if (number == null) {
            errorText = "Enter a valid number"
            return
        }

        // за діапазоном
        if (number !in 0..100) {
            errorText = "Number must be between 0 and 100"
            return
        }

        attempts++

        when {
            number == targetNumber -> {
                finished = true

                resultText = """
                    🎉 You win! Number was $targetNumber
                    Energy -7
                    Hunger -5
                    Happiness +20
                """.trimIndent()

                viewModel.changeStats(
                    energyDelta = -7,
                    happinessDelta = 20,
                    hungerDelta = -5
                )
            }

            attempts >= maxAttempts -> {
                finished = true

                resultText = """
                    You lost! Number was $targetNumber
                    Energy -7
                    Hunger -5
                    Happiness +0
                """.trimIndent()

                viewModel.changeStats(
                    energyDelta = -7,
                    hungerDelta = -5
                )
            }

            number < targetNumber -> {
                hint = "🔼 Higher"
            }

            else -> {
                hint = "🔽 Lower"
            }
        }

        userInput = ""
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

                Text("Guess the Number", style = TitleText)

                Spacer(Modifier.height(20.dp))

                Text("Guess a number from 0 to 100", style = UsualText)

                Spacer(Modifier.height(20.dp))


                    // поле введення
                    TextField(
                        value = userInput,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() }) {
                                userInput = it
                                errorText = ""
                            }
                        },
                        label = { Text("Enter number", color = TextDark) },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimary),
                        isError = errorText.isNotEmpty(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.primary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                            disabledTextColor = MaterialTheme.colorScheme.onSecondary,
                            cursorColor = TextDark
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !finished
                    )

                    // помилка
                    if (errorText.isNotEmpty()) {
                        Text(
                            text = errorText,
                            color = Color.Red,
                            style = UsualText
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // перевірка
                    CustomButton(
                        text = "Check",
                        onClick = { checkAnswer() },
                        enabled = !finished
                    )

                    Spacer(Modifier.height(15.dp))

                    // підказка
                    if (hint.isNotEmpty()) {
                        Text(text = hint, style = UsualText)
                    }

                    Spacer(Modifier.height(10.dp))

                    Text("Attempts: $attempts / $maxAttempts", style = UsualText)

                    Spacer(Modifier.height(20.dp))

                    // результат
                    Text(
                        text = resultText,
                        style = UsualText,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                Spacer(Modifier.weight(1f))

                // рестарт гри
                if (finished) {

                    CustomButton(
                        text = "Play Again",
                        onClick = { startGame() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    )

                    Spacer(Modifier.height(10.dp))
                }

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