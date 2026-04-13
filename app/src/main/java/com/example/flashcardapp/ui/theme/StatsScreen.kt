package com.example.flashcardapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardapp.viewmodel.CardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    deckId: Long,
    viewModel: CardViewModel,
    onBack: () -> Unit
) {
    // Lấy danh sách thẻ từ Database
    val allCards by viewModel.getCardsByDeck(deckId).collectAsState(initial = emptyList())

    // --- LOGIC TÍNH TOÁN ---
    val totalCards = allCards.size
    val masteredCount = allCards.count { it.repetition >= 1 }
    val learningCount = allCards.count { it.repetition == 0 && it.interval > 0.0 }
    val difficultCount = allCards.count { it.easeFactor < 2.0 && it.interval > 0.0 }

    val progressValue = if (totalCards > 0) masteredCount.toFloat() / totalCards else 0f
    val progressPercent = (progressValue * 100).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phân tích học tập", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hàng tóm tắt (Tổng số & % Ghi nhớ)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    label = "Tổng số thẻ",
                    value = "$totalCards",
                    containerColor = Color(0xFFE3F2FD),
                    contentColor = Color(0xFF1976D2),
                    icon = Icons.Default.Style
                )
                StatSmallCard(
                    modifier = Modifier.weight(1f),
                    label = "Ghi nhớ tốt",
                    value = "$progressPercent%",
                    containerColor = Color(0xFFE8F5E9),
                    contentColor = Color(0xFF388E3C),
                    icon = Icons.Default.CheckCircle
                )
            }

            // THẺ TRẠNG THÁI (ĐÃ FIX THANH 3 MÀU)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Trạng thái ghi nhớ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))

                    // Thanh tiến trình phân đoạn 3 màu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(Color(0xFFEEEEEE), RoundedCornerShape(6.dp))
                    ) {
                        if (totalCards > 0) {
                            // Đoạn màu Xanh (Dễ)
                            if (masteredCount > 0) {
                                Box(Modifier.fillMaxHeight().weight(masteredCount.toFloat()).background(Color(0xFF66BB6A)))
                            }
                            // Đoạn màu Vàng (Vừa)
                            if (learningCount > 0) {
                                Box(Modifier.fillMaxHeight().weight(learningCount.toFloat()).background(Color(0xFFFFA726)))
                            }
                            // Đoạn màu Đỏ (Khó)
                            if (difficultCount > 0) {
                                Box(Modifier.fillMaxHeight().weight(difficultCount.toFloat()).background(Color(0xFFEF5350)))
                            }
                            // Khoảng trống cho thẻ chưa học (nếu có)
                            val remaining = totalCards - masteredCount - learningCount - difficultCount
                            if (remaining > 0) {
                                Box(Modifier.fillMaxHeight().weight(remaining.toFloat()).background(Color(0xFFE0E0E0)))
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    StatusRow(Color(0xFF66BB6A), "Đã thuộc (Dễ)", "$masteredCount thẻ")
                    StatusRow(Color(0xFFFFA726), "Đang học (Vừa)", "$learningCount thẻ")
                    StatusRow(Color(0xFFEF5350), "Cần ôn lại (Khó)", "$difficultCount thẻ")
                }
            }

            // MỨC ĐỘ THÀNH THẠO (ĐÃ FIX CĂN GIỮA)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Mức độ thành thạo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(24.dp))

                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = progressValue,
                            modifier = Modifier.size(140.dp),
                            strokeWidth = 12.dp,
                            color = Color(0xFF4FC3F7),
                            trackColor = Color(0xFFE1F5FE),
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$progressPercent%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )
                            Text(
                                text = "Hoàn thành",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Mục tiêu: Đưa toàn bộ thẻ về trạng thái 'Đã thuộc'",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StatSmallCard(
    modifier: Modifier,
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = contentColor)
            Text(label, fontSize = 12.sp, color = contentColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun StatusRow(color: Color, label: String, count: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(12.dp).background(color, CircleShape))
        Spacer(Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 15.sp)
        Text(count, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
