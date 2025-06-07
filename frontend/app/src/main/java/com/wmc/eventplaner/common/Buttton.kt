package com.wmc.eventplaner.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
        .height(56.dp),
    backgroundColor: Color = Color(0xFF009A60),
    textColor: Color = Color.White,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier,
        enabled =enabled

    ) {
        Text(text = text, color =  textColor, fontWeight = FontWeight.Bold)
    }
}
