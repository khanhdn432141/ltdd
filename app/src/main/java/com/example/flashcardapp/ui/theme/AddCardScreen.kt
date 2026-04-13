package com.example.flashcardapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.example.flashcardapp.viewmodel.CardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(deckId: Long, viewModel: CardViewModel, onBack: () -> Unit) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    var savedCount by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Header
        Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer))).padding(20.dp)) {
            Column {
                IconButton(onClick = onBack, modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(0.2f))) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(Modifier.height(12.dp))
                Text("Thêm thẻ mới", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                if (savedCount > 0) Text("Đã lưu $savedCount thẻ", color = Color.White.copy(0.85f))
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = front, onValueChange = { front = it }, label = { Text("Mặt trước") }, modifier = Modifier.fillMaxWidth().height(120.dp))
            OutlinedTextField(value = back, onValueChange = { back = it }, label = { Text("Mặt sau") }, modifier = Modifier.fillMaxWidth().height(120.dp))

            if (front.isNotBlank() || back.isNotBlank()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Xem trước", fontWeight = FontWeight.Bold)
                        Text(front.ifBlank { "..." })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Fix Divider
                        Text(back.ifBlank { "..." })
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Nút Lưu + Tiếp
                OutlinedButton(
                    modifier = Modifier.weight(1f).height(52.dp),
                    onClick = {
                        viewModel.addCard(deckId, front.trim(), back.trim())
                        savedCount++
                        front = ""; back = ""
                    },
                    enabled = front.isNotBlank() && back.isNotBlank()
                ) { Text("Lưu + tiếp") }

                // Nút Lưu + Xong
                Button(
                    modifier = Modifier.weight(1f).height(52.dp),
                    onClick = {
                        viewModel.addCard(deckId, front.trim(), back.trim())
                        onBack()
                    },
                    enabled = front.isNotBlank() && back.isNotBlank()
                ) { Text("Lưu + xong") }
            }
        }
    }
}