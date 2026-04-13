package com.example.flashcardapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY id DESC")
    fun getAllDecks(): Flow<List<Deck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck): Long

    @Delete
    suspend fun deleteDeck(deck: Deck)

    // Xóa bộ thẻ theo ID để fix lỗi trong ViewModel
    @Query("DELETE FROM decks WHERE id = :deckId")
    suspend fun deleteDeckById(deckId: Long)

    @Update
    suspend fun updateDeck(deck: Deck)
}