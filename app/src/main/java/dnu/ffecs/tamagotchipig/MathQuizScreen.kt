package dnu.ffecs.tamagotchipig

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dnu.ffecs.tamagotchipig.ui.theme.ButtonText
import dnu.ffecs.tamagotchipig.ui.theme.TitleText
import dnu.ffecs.tamagotchipig.ui.theme.UsualText

@Composable
fun MathQuizScreen(
    viewModel: PetViewModel,
    goHome: () -> Unit
) {

    var currentIndex by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }

    var question by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableIntStateOf(0) }
    var options by remember { mutableStateOf(listOf<Int>()) }

    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }

    var finished by remember { mutableStateOf(false) }

    fun generateQuestion(): Triple<String, Int, List<Int>> {
        val a = (1..20).random()
        val b = (1..20).random()

        val (q, correct) = when ((1..4).random()) {
            1 -> "$a + $b" to (a + b)
            2 -> "$a - $b" to (a - b)
            3 -> "$a * $b" to (a * b)
            else -> {
                val x = a * b
                "$x / $a" to b
            }
        }

        val wrong = mutableSetOf<Int>()
        while (wrong.size < 3) {
            wrong.add((correct + (-10..10).random()).coerceAtLeast(0))
        }

        val all = (wrong + correct).shuffled()

        return Triple(q, correct, all)
    }

    fun nextQuestion() {
        if (currentIndex >= 5) {
            finished = true

            viewModel.changeStats(
                hungerDelta = -5,
                energyDelta = -7,
                happinessDelta = correctCount * 6
            )
            return
        }

        val (q, ans, opts) = generateQuestion()

        question = q
        correctAnswer = ans
        options = opts

        selectedAnswer = null
        showResult = false
    }

    fun startGame() {
        currentIndex = 0
        correctCount = 0
        finished = false
        nextQuestion()
    }

    fun selectAnswer(answer: Int) {
        selectedAnswer = answer
        showResult = true

        if (answer == correctAnswer) {
            correctCount++
        }
    }

    fun continueGame() {
        currentIndex++
        nextQuestion()
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

                Text("Math Quiz", style = TitleText)

                Spacer(Modifier.height(40.dp))

                Text("Question ${currentIndex + 1} / 5", style = UsualText)

                Spacer(Modifier.height(50.dp))

                if (!finished) {

                    Text(
                        text = question,
                        style = UsualText
                    )

                    Spacer(Modifier.height(20.dp))

                    options.forEach { option ->

                        val isCorrect = option == correctAnswer
                        val isSelected = option == selectedAnswer

                        val color = when {
                            showResult && isCorrect -> Color(0xFF4CAF50)
                            showResult && isSelected -> Color(0xFFF44336)
                            else -> MaterialTheme.colorScheme.primary
                        }

                        Button(
                            onClick = {
                                if (!showResult) selectAnswer(option)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp, horizontal = 15.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = color),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)){
                            Text(option.toString(), style = ButtonText)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    if (showResult) {
                        CustomButton(
                            text = "Next",
                            onClick = { continueGame() }
                        )
                    }

                } else {

                    Text(
                        text = "Good Job! 🎉",
                        style = UsualText
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("✨ Correct: $correctCount / 5", style = UsualText)
                    Text("Add ${correctCount*6} happiness", style = UsualText)
                    Text("Decrease 5 hunger and 7 energy", style = UsualText)

                    Spacer(Modifier.height(20.dp))
                }

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