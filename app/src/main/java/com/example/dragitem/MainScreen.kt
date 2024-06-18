package com.example.dragitem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            viewModel.items.forEach { person ->
                DragTarget(
                    dataToDrop = person,
                    viewModel = viewModel
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dp(screenWidth / 5f))
                            .clip(RoundedCornerShape(14.dp))
                            .shadow(4.dp, RoundedCornerShape(14.dp))
                            .background(person.backgroundColor, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = person.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            viewModel.isCurrentlyDragging,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally()
        ) {
            DropItem<PersonUiItem>(modifier = Modifier.size(Dp(screenWidth / 3.5f))) { isInBounds, data ->
                if (data != null) {
                    LaunchedEffect(key1 = data) {
                        viewModel.addPerson(data)
                    }
                }
                if (isInBounds) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, color = Color.Black, shape = RoundedCornerShape(14.dp))
                            .background(Color.Gray.copy(0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add Person",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, color = Color.White, shape = RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(0.6f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add Person",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Added Persons",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                viewModel.addedPersons.forEach { person ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = person.name
                    )
                }
            }
        }
    }
}