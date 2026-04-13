
package com.example.flashcardapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// HÀM DUY NHẤT GÂY LỖI TRÙNG LẶP - CHỈ GIỮ LẠI BẢN NÀY
@Composable
fun StatRow(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Text("$count thẻ", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MiniChip(text: String, color: Color, textColor: Color = Color.Unspecified) {
    Surface(color = color, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (textColor == Color.Unspecified) MaterialTheme.colorScheme.primary else textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BigStatCard(value: String, label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StatPill(label: String, icon: ImageVector) {
    Surface(color = Color.White.copy(0.2f), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = Color.White)
            Spacer(Modifier.width(4.dp))
            Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun EmptyCardsView() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.AddCard, null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.outline)
        Text("Chưa có thẻ nào", color = MaterialTheme.colorScheme.outline)
    }
}
