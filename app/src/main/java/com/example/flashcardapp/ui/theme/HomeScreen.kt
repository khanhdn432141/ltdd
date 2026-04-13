package com.example.flashcardapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardapp.data.Deck
import com.example.flashcardapp.viewmodel.CardViewModel

@Composable
fun HomeScreen(
    viewModel: CardViewModel,
    onDeckClick: (Long, String) -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val decks by viewModel.allDecks.collectAsState()

    // TRẠNG THÁI ĐIỀU KHIỂN DIALOG TẠO BỘ THẺ
    var showAddDialog by remember { mutableStateOf(false) }
    var newDeckName by remember { mutableStateOf("") }
    var newDeckDesc by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true }, // Khi nhấn sẽ hiện Dialog
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Tạo bộ thẻ") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    HomeHeader(
                        deckCount = decks.size,
                        onSettingsClick = onSettingsClick,
                        onLogout = onLogout
                    )
                }

                if (decks.isEmpty()) {
                    item {
                        Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Chưa có bộ thẻ nào. Hãy nhấn + để tạo!", color = Color.Gray)
                        }
                    }
                }

                items(decks) { deck ->
                    DeckItem(
                        deck = deck,
                        onClick = { onDeckClick(deck.id, deck.name) },
                        onDelete = { viewModel.deleteDeck(deck.id) }
                    )
                }
            }

            // DIALOG NHẬP THÔNG TIN BỘ THẺ MỚI
            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("Tạo bộ thẻ mới", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = newDeckName,
                                onValueChange = { newDeckName = it },
                                label = { Text("Tên bộ thẻ (Ví dụ: Tiếng Anh)") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newDeckDesc,
                                onValueChange = { newDeckDesc = it },
                                label = { Text("Mô tả ngắn") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newDeckName.isNotBlank()) {
                                    viewModel.addDeck(newDeckName.trim(), newDeckDesc.trim())
                                    showAddDialog = false
                                    newDeckName = ""; newDeckDesc = ""
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Tạo ngay")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeHeader(deckCount: Int, onSettingsClick: () -> Unit, onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(
                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
            ))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Flashcard", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                Row {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, null, tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                    }
                }
            }
            Text("Học thông minh hơn mỗi ngày", color = Color.White.copy(0.8f))
            Spacer(Modifier.height(16.dp))
            Surface(color = Color.White.copy(0.2f), shape = RoundedCornerShape(20.dp)) {
                Text("$deckCount bộ thẻ", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckItem(deck: Deck, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(deck.name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(deck.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(deck.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}