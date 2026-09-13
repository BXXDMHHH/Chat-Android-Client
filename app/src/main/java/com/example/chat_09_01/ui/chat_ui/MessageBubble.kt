package com.example.chat_09_01.ui.chat_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat_09_01.data.Message
import com.example.chat_09_01.ui.Avatar.UserAvatar

@Composable
fun MessageBubble(message: Message) {
    if (message.isSystem) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.text,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        return
    }

    val (bgColor, txtColor, rowAlign) = if (message.isFromMe) {
        Triple(Color(0xFF95EC69), Color.White, Arrangement.End)
    } else {
        Triple(Color(0xFFFFFFFF), Color.Black, Arrangement.Start)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = rowAlign,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isFromMe) {
            UserAvatar(message.username, size = 36.dp)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column {
            if (!message.isFromMe) {
                Text(
                    text = message.username,
                    fontSize = 12.sp,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = bgColor,
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 260.dp)
            ) {
                Text(
                    text = message.text,
                    color = txtColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (message.isFromMe) {
            Spacer(modifier = Modifier.width(8.dp))
            UserAvatar(message.username, size = 36.dp)
        }
    }
}