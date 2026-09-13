package com.example.chat_09_01.ui.Avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun UserAvatar(username: String,size: Dp =40.dp) {
        val colors = listOf(
            Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A),
            Color(0xFFFFA726), Color(0xFFAB47BC), Color(0xFF26C6DA)
        )
        val color = remember(username) {
            colors[abs(username.hashCode())% colors.size]
        }
        val initial = username.firstOrNull()?.uppercase() ?: "?"

        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 2.2).sp
            )
        }
}
