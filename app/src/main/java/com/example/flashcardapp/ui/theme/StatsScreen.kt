@Composable
fun BigStatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SmallStatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StatsScreen(
    deckId: Long,
    deckName: String,
    viewModel: CardViewModel,
    onBack: () -> Unit
) {
    val cards by viewModel.getCardsByDeck(deckId).collectAsState(initial = emptyList())
    val dueCount by viewModel.getDueCardCount(deckId).collectAsState(initial = 0)

    val totalCards = cards.size
    val masteredCards = cards.count { it.repetition >= 3 }
    val learningCards = cards.count { it.repetition in 1..2 }
    val newCards = cards.count { it.repetition == 0 }
    val avgEF = if (cards.isEmpty()) 0f
    else cards.map { it.easeFactor }.average().toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Thống kê",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        deckName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BigStatCard(
                        value = totalCards.toString(),
                        label = "Tổng thẻ",
                        icon = Icons.Outlined.Style,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    BigStatCard(
                        value = dueCount.toString(),
                        label = "Cần ôn",
                        icon = Icons.Outlined.Notifications,
                        color = ColorForgot,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SmallStatCard(
                        value = newCards.toString(),
                        label = "Thẻ mới",
                        color = ColorEasy,
                        modifier = Modifier.weight(1f)
                    )
                    SmallStatCard(
                        value = learningCards.toString(),
                        label = "Đang học",
                        color = ColorHard,
                        modifier = Modifier.weight(1f)
                    )
                    SmallStatCard(
                        value = masteredCards.toString(),
                        label = "Thuộc",
                        color = ColorGood,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tiến độ thuộc bài")
                            Text("${if (totalCards > 0) masteredCards * 100 / totalCards else 0}%")
                        }

                        Spacer(Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = {
                                if (totalCards == 0) 0f
                                else masteredCards.toFloat() / totalCards
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text("Độ khó trung bình: ${"%.2f".format(avgEF)}")
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Chi tiết từng thẻ")
                Spacer(Modifier.height(8.dp))
            }
        }

        items(cards, key = { it.id }) { card ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(card.front)
                    Text(card.back)
                }
            }
        }
    }
}
