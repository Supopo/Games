package com.xxx.newgames.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    titleTrailing: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    GameBackground {
        Column(modifier = Modifier.statusBarsPadding()) {
            GameTopBar(
                title = title,
                onBack = onBack,
                actions = actions,
                titleTrailing = titleTrailing,
            )
            content()
        }
    }
}

@Composable
fun GameTopBar(
    title: String,
    onBack: () -> Unit,
    backEnabled: Boolean = true,
    titleInBar: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    titleTrailing: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .heightIn(min = 48.dp),
    ) {
        Text(
            text = "返回",
            color = Color.White,
            fontSize = GameUi.backSize,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable(enabled = backEnabled, role = Role.Button, onClick = onBack)
                .padding(horizontal = 8.dp, vertical = 12.dp),
        )
        if (titleInBar) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 72.dp),
            )
        }
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
    if (!titleInBar) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 23.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = GameUi.titleSize,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f, fill = false),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = titleTrailing,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun GameActionChip(
    text: String,
    onClick: (() -> Unit)? = null,
    color: Color = Color(0xFFEC5F98),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    horizontalPadding: Dp = 14.dp,
) {
    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else .5f)
            .background(color, RoundedCornerShape(18.dp))
            .then(if (onClick != null) Modifier.clickable(enabled = enabled, role = Role.Button, onClick = onClick) else Modifier)
            .heightIn(min = GameUi.actionHeight)
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = Color.White, fontSize = GameUi.actionSize)
    }
}
