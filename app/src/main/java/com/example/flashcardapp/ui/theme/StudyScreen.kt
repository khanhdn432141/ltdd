package com.example.flashcardapp.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcardapp.viewmodel.CardViewModel
import com.example.flashcardapp.util.TtsHelper
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(deckId: Long, viewModel: CardViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val ttsHelper = remember { TtsHelper(context) }

    // Quản lý vòng đời TTS
    DisposableEffect(Unit) {
        onDispose { ttsHelper.shutdown() }
    }

    val cards by viewModel.getCardsByDeck(deckId).collectAsState(initial = emptyList())
    var currentIndex by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Hiệu ứng xoay thẻ
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400)
    )

    // Đọc mặt trước khi đổi thẻ hoặc khi màn hình vừa mở
    LaunchedEffect(currentIndex, cards) {
        if (cards.isNotEmpty() && currentIndex < cards.size) {
            if (isFinished) return@LaunchedEffect // ✅ chặn tại đây
            // Thêm một chút delay để chắc chắn giao diện và TTS đã sẵn sàng
            ttsHelper.stop()
            delay(500)

            ttsHelper.speak(cards[currentIndex].front)
        }
    }

    // Đọc mặt sau khi lật
    LaunchedEffect(flipped) {
        if (isFinished) return@LaunchedEffect // ✅ thêm dòng này
        if (flipped && cards.isNotEmpty() && currentIndex < cards.size) {
            ttsHelper.speak(cards[currentIndex].back)
        }
    }

    // Hàm xử lý khi bấm Dễ/Vừa/Khó
    fun handleNextCard(quality: Int) {
        if (!flipped || currentIndex >= cards.size) return
        val currentCard = cards[currentIndex]

        scope.launch {
            ttsHelper.stop()
            // Lưu kết quả học tập
            viewModel.updateCardLearning(currentCard.id, quality)

            // KIỂM TRA: Nếu còn thẻ tiếp theo
            if (currentIndex < cards.size - 1) {
                flipped = false // Lật về mặt trước
                delay(300)      // Chờ thẻ xoay xong
                currentIndex++  // Chuyển sang thẻ mới
            } else {
                // Nếu là thẻ CUỐI CÙNG: Hiện màn hình kết thúc luôn
                isFinished = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ôn tập", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            if (cards.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Bộ thẻ này hiện đang trống.", color = Color.Gray)
                }
            } else if (!isFinished) {
                val card = cards[currentIndex]
                Column(modifier = Modifier.fillMaxSize()) {
                    // Hiển thị số lượng thẻ còn lại
                    Text(
                        text = "Thẻ ${currentIndex + 1} / ${cards.size}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Flashcard chính
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clickable { if (!flipped) flipped = true },
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rotation <= 90f) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (rotation <= 90f) {
                                // MẶT TRƯỚC
                                Text(
                                    text = card.front,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                // MẶT SAU
                                Text(
                                    text = card.back,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .graphicsLayer { rotationY = 180f }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(30.dp))

                    // Hệ thống nút đánh giá
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val buttonModifier = Modifier.weight(1f).fillMaxHeight()

                        // Nút KHÓ
                        Button(
                            onClick = { handleNextCard(1) },
                            modifier = buttonModifier,
                            enabled = flipped,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Khó", fontWeight = FontWeight.Bold) }

                        // Nút VỪA
                        Button(
                            onClick = { handleNextCard(3) },
                            modifier = buttonModifier,
                            enabled = flipped,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Vừa", fontWeight = FontWeight.Bold) }

                        // Nút DỄ
                        Button(
                            onClick = { handleNextCard(5) },
                            modifier = buttonModifier,
                            enabled = flipped,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Dễ", fontWeight = FontWeight.Bold) }
                    }
                }
            } else {
                // MÀN HÌNH HOÀN THÀNH (CHỈ HIỆN KHI XONG THẺ CUỐI)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉 Tuyệt vời!", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                    Text("Bạn đã hoàn thành bộ thẻ này.", fontSize = 18.sp)
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onBack,
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Quay lại danh sách", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}