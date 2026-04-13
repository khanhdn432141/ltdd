package com.example.flashcardapp.util

import com.example.flashcardapp.data.Card
import java.util.concurrent.TimeUnit

fun applySmTwo(card: Card, quality: Int): Card {
    require(quality in 0..5) { "Quality phải từ 0 đến 5" }

    val newRepetition: Int
    val newInterval: Int
    val newEaseFactor: Float

    if (quality < 3) {
        newRepetition = 0
        newInterval = 1
        newEaseFactor = card.easeFactor
    } else {
        newRepetition = card.repetition + 1
        newInterval = when (card.repetition) {
            0    -> 1
            1    -> 6
            else -> (card.interval * card.easeFactor).toInt()
        }
        val delta = 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)
        newEaseFactor = maxOf(1.3f, (card.easeFactor + delta).toFloat())
    }

    val nextReview = System.currentTimeMillis() +
            TimeUnit.DAYS.toMillis(newInterval.toLong())

    // SỬA DÒNG 49 TẠI ĐÂY
    return card.copy(
        interval      = newInterval,
        repetition    = newRepetition,
        easeFactor    = newEaseFactor,
        nextReview    = nextReview // Phải trùng tên với thuộc tính trong Card.kt
    )
}