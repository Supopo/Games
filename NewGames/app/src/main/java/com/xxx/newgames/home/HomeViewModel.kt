package com.xxx.newgames.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Park
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.xxx.newgames.navigation.AppRoute
import com.xxx.newgames.ui.theme.OrangeEnd
import com.xxx.newgames.ui.theme.OrangeStart
import com.xxx.newgames.ui.theme.PinkEnd
import com.xxx.newgames.ui.theme.PinkStart
import com.xxx.newgames.ui.theme.PurpleEnd
import com.xxx.newgames.ui.theme.PurpleStart
import com.xxx.newgames.ui.theme.VioletEnd
import com.xxx.newgames.ui.theme.VioletStart

data class HomeGame(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: AppRoute,
    val colors: List<androidx.compose.ui.graphics.Color>,
)

class HomeViewModel : ViewModel() {
    val games = listOf(
        HomeGame("真心话大冒险", "转动指针，看看谁中招", Icons.Rounded.Casino, AppRoute.TRUTH_OR_DARE, listOf(OrangeStart, OrangeEnd)),
        HomeGame("Games100", "抽一张牌，玩一场聚会游戏", Icons.Rounded.LocalBar, AppRoute.DRINKING_CARDS, listOf(OrangeStart, PinkEnd)),
        HomeGame("愤怒的皮卡丘", "小心炸弹，挑战你的手速", Icons.Rounded.Bolt, AppRoute.ANGRY_UNCLE, listOf(PurpleStart, PurpleEnd)),
        HomeGame("逛三园", "轻松一局，看看今天的运气", Icons.Rounded.Park, AppRoute.PARKS, listOf(VioletStart, VioletEnd)),
        HomeGame("谁是卧底", "找出藏在人群里的卧底", Icons.Rounded.Face, AppRoute.WHO_IS, listOf(PinkStart, PinkEnd)),
    )
}
