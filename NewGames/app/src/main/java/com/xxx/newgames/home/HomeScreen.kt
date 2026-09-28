package com.xxx.newgames.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxx.newgames.ui.components.GameBackground
import com.xxx.newgames.ui.components.GameMenuCard
import com.xxx.newgames.ui.components.GameUi

@Composable
fun HomeScreen(
    onOpen: (HomeGame) -> Unit,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val games = viewModel.games
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    GameBackground {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_game_grid"),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = statusBarTop + 12.dp,
                end = 20.dp,
                bottom = 24.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeHeader(gameCount = games.size)
            }
            itemsIndexed(
                items = games,
                key = { _, game -> game.route.path },
                span = { index, _ ->
                    if (index == 0) GridItemSpan(maxLineSpan) else GridItemSpan(1)
                },
            ) { index, game ->
                GameMenuCard(
                    title = game.title,
                    subtitle = game.subtitle,
                    icon = game.icon,
                    number = (index + 1).toString().padStart(2, '0'),
                    colors = game.colors,
                    featured = index == 0,
                    wide = false,
                    onClick = { onOpen(game) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(gameCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = .13f))
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                ) {
                    Text("NG", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
                Text(
                    text = "NEW GAMES",
                    color = Color.White.copy(alpha = .78f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }
            Text(
                text = "$gameCount 款游戏  ·  即点即玩",
                color = Color.White.copy(alpha = .85f),
                fontSize = 11.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = .09f))
                    .padding(horizontal = 11.dp, vertical = 7.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "今晚，玩点不一样的",
                color = Color.White,
                fontSize = GameUi.titleSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-.5).sp,
            )
            Text(
                text = "挑个小游戏，让快乐马上开始。",
                color = Color.White.copy(alpha = .68f),
                fontSize = 14.sp,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("从这里开始", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text("SWIPE TO EXPLORE", color = Color.White.copy(alpha = .5f), fontSize = 9.sp, letterSpacing = 1.2.sp)
        }
    }
}
