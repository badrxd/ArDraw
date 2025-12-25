package com.badr1.ardraw.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.badr1.ardraw.R
import com.badr1.ardraw.screens.DrawImageType.ImageSourceType
import com.badr1.ardraw.screens.DrawOptionScreen
import com.badr1.ardraw.ui.theme.CustomPurple
import com.badr1.ardraw.ui.theme.PurpleBoxColor
import com.badr1.ardraw.viewmodels.SharedDrawImageViewModel

@Composable
fun Header(
    text: String,
    navController: NavController,
    next: Boolean = false,
    onNext: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Icon(
//            modifier = Modifier
//                .size(35.dp)
//                .clickable(onClick = { navController.navigateUp() }),
//            imageVector = Icons.Default.KeyboardArrowLeft,
//            contentDescription = "Go Back",
//        )

        Card(
            modifier = modifier.size(46.dp), // The size of the outer card
            shape = RoundedCornerShape(16.dp), // Optional: for rounded corners
            elevation = 1.dp
        ) {
            Box(
                contentAlignment = Alignment.Center, // Centers the icon inside the 60dp card
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = CustomPurple.copy(alpha = 0.2f),
//                                shape = CircleShape // Optional: makes the background circular
                    )
                    .clickable(onClick = {
                        navController.navigateUp()
                    })
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(29.dp) // The actual size of the icon
                    ,
//                    tint = PurpleBoxColor.copy(alpha = 0.5f)
                    tint = CustomPurple
                )
            }
        }

        Text(
            text,
            style = MaterialTheme.typography.h6,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        if (next) {
            Spacer(Modifier.width(1.dp))
//            Button(
//                onClick = { onNext() },
//                modifier = Modifier
//                    .padding(horizontal = 4.dp, vertical = 0.dp)
//                    .size(46.dp),
//                shape = RoundedCornerShape(16.dp),
//                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = CustomPurple.copy(alpha = 0.2f),
//                    contentColor = CustomPurple,
//
//                    ),
//                elevation = ButtonDefaults.elevation(
//                    defaultElevation = 0.dp,
//                    pressedElevation = 0.dp
//                ),
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.rounded_draw_24),
//                    contentDescription = "Settings",
//                    modifier = Modifier
//                        .size(46.dp)
//                )
//            }
            Card(
                modifier = modifier.size(46.dp), // The size of the outer card
                shape = RoundedCornerShape(16.dp), // Optional: for rounded corners
                elevation = 1.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center, // Centers the icon inside the 60dp card
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = CustomPurple.copy(alpha = 0.2f),
//                                shape = CircleShape // Optional: makes the background circular
                        )
                        .clickable(onClick = {
                            onNext()
                        })
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_draw_24),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .size(20.dp) // The actual size of the icon
                        ,
//                    tint = PurpleBoxColor.copy(alpha = 0.5f)
                        tint = CustomPurple
                    )
                }
            }
        } else {
            Spacer(Modifier.width(46.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DrawOptionScreenPreview() {
    Header(
        text = "badr",
        modifier = Modifier,
        navController = NavController(LocalContext.current),
        next = true,
    )
}
