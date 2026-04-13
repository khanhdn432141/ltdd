package com.example.flashcardapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val front: String,
    val back: String,

    // Các thuộc tính bắt buộc cho thuật toán SM-2
    val repetition: Int = 0,
    val interval: Int = 1,
    val easeFactor: Float = 2.5f,

    val lastReview: Long = 0,
    val nextReview: Long = 0 // Tên biến dùng để đồng bộ với dòng 49 bên trên
)