package com.example.flashcardapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.flashcardapp.data.Card
import com.example.flashcardapp.viewmodel.CardViewModel
import com.example.flashcardapp.util.TtsHelper
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScreen(
    deckId: Long,
    deckName: String,
    viewModel: CardViewModel,
    onStudyClick: () -> Unit,
    onStatsClick: () -> Unit,
    onAddCard: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ttsHelper = remember { TtsHelper(context) }
    // Giải phóng TTS khi rời màn hình
    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { ttsHelper.shutdown() } }

    val cards by viewModel.getCardsByDeck(deckId).collectAsState(initial = emptyList())
    val dueCount by viewModel.getDueCardCount(deckId).collectAsState(initial = 0)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCard,
                containerColor = Color(0xFF0097A7), // Màu xanh Cyan đậm
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, null) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // --- 1. HEADER GRADIENT (Xanh Cyan) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF4DD0E1), Color(0xFFE0F7FA))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.White.copy(0.4f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = deckName,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeaderInfoChip(icon = Icons.Default.Style, text = "${cards.size} thẻ")
                        HeaderInfoChip(icon = Icons.Default.Notifications, text = "$dueCount cần ôn")
                    }
                }
            }

            // --- 2. HAI NÚT CHỨC NĂNG (ÔN TẬP & THỐNG KÊ) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-30).dp), // Đẩy nút đè lên phần header
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCardButton(
                    text = "Ôn tập",
                    icon = Icons.Default.PlayArrow,
                    onClick = onStudyClick,
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    contentColor = Color(0xFF0097A7)
                )
                ActionCardButton(
                    text = "Thống kê",
                    icon = Icons.Default.BarChart,
                    onClick = onStatsClick,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0F2F1),
                    contentColor = Color(0xFF00796B)
                )
            }

            // --- 3. DANH SÁCH THẺ ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Tất cả thẻ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Color(0xFFE0F7FA), shape = CircleShape) {
                            Text("${cards.size}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(cards) { card ->
                    ModernCardItem(
                        card = card,
                        onDelete = { viewModel.deleteCard(card) },
                        onSpeak = ttsHelper::speak
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderInfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        color = Color.White.copy(0.2f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color.White)
            Spacer(Modifier.width(4.dp))
            Text(text, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionCardButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier, color: Color, contentColor: Color) {
    Card(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = contentColor)
            Spacer(Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

@Composable
fun ModernCardItem(card: Card, onDelete: () -> Unit, onSpeak: (String) -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(card.front, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(card.back, color = Color.Gray, fontSize = 14.sp)

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Hiển thị số lượt ôn (repetition)
                    CardStatusBadge(text = "Ôn: ${card.repetition}x")
                    // Hiển thị interval (khoảng cách ngày ôn)
                    CardStatusBadge(text = "${card.interval} ngày")
                }
            }

            IconButton(onClick = { onSpeak(card.front + ". " + card.back) }) {
                Icon(Icons.Default.VolumeUp, null, tint = Color(0xFF4DD0E1))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun CardStatusBadge(text: String) {
    Surface(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}