package dnu.ffecs.tamagotchipig

import android.widget.ImageButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dnu.ffecs.tamagotchipig.ui.theme.ButtonColor
import dnu.ffecs.tamagotchipig.ui.theme.ButtonDisabled
import dnu.ffecs.tamagotchipig.ui.theme.ButtonStroke
import dnu.ffecs.tamagotchipig.ui.theme.ButtonText
import dnu.ffecs.tamagotchipig.ui.theme.DialogColor
import dnu.ffecs.tamagotchipig.ui.theme.TextDark
import dnu.ffecs.tamagotchipig.ui.theme.TitleText
import java.nio.file.WatchEvent

@Composable
fun GameChooseScreen (viewModel: PetViewModel,
                      onClose: ()->Unit,
                      goHome: ()->Unit,
                      goToMathQuiz:()->Unit,
                      goToLuckyBox:()->Unit,
                      goToGuessNumber:()->Unit,
                      goToTapGame:()->Unit) {
    Column (modifier = Modifier.background(color = ButtonColor).padding(vertical = 10.dp, horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally){

        Text("Mini-Games", style = TitleText)

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GameButton(
                imageRes = R.drawable.math_icon,
                text = "Math Quiz",
                onClick = goToMathQuiz
            )

            Spacer(modifier = Modifier.width(10.dp))

            GameButton(
                imageRes = R.drawable.lucky_icon,
                text = "Lucky Boxes",
                onClick = goToLuckyBox
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GameButton(
                imageRes = R.drawable.guessnum_icon,
                text = "Guess Number",
                onClick = goToGuessNumber
            )

            Spacer(modifier = Modifier.width(10.dp))

            GameButton(
                imageRes = R.drawable.tap_icon,
                text = "Tap Game",
                onClick = goToTapGame
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(modifier = Modifier.fillMaxWidth().padding(5.dp),
            onClick = {onClose()},
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonColor,
                contentColor = TextDark,
                disabledContainerColor = ButtonDisabled,
                disabledContentColor = TextDark
            ),
            border = BorderStroke(2.dp, ButtonStroke)){
            Text("Go Home", style = ButtonText)
        }
    }
}

@Composable
fun GameButton(
    imageRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(text, style = ButtonText)
    }
}