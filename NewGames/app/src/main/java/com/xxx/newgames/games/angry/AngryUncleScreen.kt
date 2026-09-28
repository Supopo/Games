package com.xxx.newgames.games.angry

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxx.newgames.R
import com.xxx.newgames.ui.components.GameActionChip
import com.xxx.newgames.ui.components.GamePrimaryButton
import com.xxx.newgames.ui.components.GameScaffold

@Composable
fun AngryUncleScreen(onBack: () -> Unit, viewModel: AngryUncleViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val leave: () -> Unit = {
        if (state.cleared) viewModel.restart()
        onBack()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        GameScaffold(
            title = "愤怒的皮卡丘",
            onBack = leave,
            actions = {
                listOf(2, 3, 5, 7).forEach { multiplier ->
                    GameActionChip("x$multiplier", { viewModel.setMultiplier(multiplier) })
                }
            },
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.cards, key = { it }) { card ->
                        Image(
                            painter = painterResource(R.drawable.ic_launcher),
                            contentDescription = "愤怒的皮卡丘卡片",
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.pick(card) },
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }
        if (state.cleared) {
            val sink = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = .45f))
                    .clickable(interactionSource = sink, indication = null, onClick = {}),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.angry_pkq),
                        contentDescription = "愤怒的皮卡丘",
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    GamePrimaryButton(
                        text = "再来一局",
                        onClick = viewModel::restart,
                        modifier = Modifier.padding(top = 14.dp),
                        width = 160.dp,
                    )
                }
            }
        }
    }
}
