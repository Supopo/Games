package com.xxx.newgames.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    number: String,
    colors: List<Color>,
    featured: Boolean,
    wide: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(if (featured) 26.dp else 22.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (featured) 176.dp else if (wide) 118.dp else 148.dp)
            .clip(shape)
            .background(Brush.linearGradient(colors))
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = if (featured) 18.dp else 12.dp,
                    end = if (featured) 20.dp else 12.dp,
                )
                .size(if (featured) 84.dp else 42.dp)
                .background(Color.White.copy(alpha = .12f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = .96f),
                modifier = Modifier.size(if (featured) 42.dp else 22.dp),
            )
        }

        if (featured) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 17.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "人气玩法  ·  $number",
                    color = Color.White.copy(alpha = .88f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = .82f),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        } else if (wide) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 19.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text("$number  ·  轻松一下", color = Color.White.copy(alpha = .82f), fontSize = 12.sp)
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    subtitle,
                    color = Color.White.copy(alpha = .78f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, top = 15.dp, end = 14.dp, bottom = 15.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(number, color = Color.White.copy(alpha = .78f), fontSize = 12.sp, letterSpacing = 1.sp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        subtitle,
                        color = Color.White.copy(alpha = .8f),
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
