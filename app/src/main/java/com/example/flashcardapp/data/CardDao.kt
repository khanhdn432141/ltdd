package com.example.flashcardapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards")
    fun getAllCards(): Flow<List<Card>>

    // THÊM HÀM NÀY ĐỂ FIX LỖI Ở CARDVIEWMODEL
    @Query("SELECT * FROM cards WHERE id = :cardId")
    fun getCardById(cardId: Long): Flow<Card?>

    @Query("SELECT * FROM cards WHERE deckId = :deckId")
    fun getCardsByDeckId(deckId: Long): Flow<List<Card>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    @Query("DELETE FROM cards WHERE deckId = :deckId")
    suspend fun deleteCardsByDeckId(deckId: Long)

    @Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId AND nextReview <= :now")
    fun getDueCardCount(deckId: Long, now: Long): Flow<Int>
    @Query("SELECT COUNT(*) FROM cards WHERE nextReview <= :now")
    fun getTotalDueCardCount(now: Long): Flow<Int>
}// Note: EOF marker above closes the file; actual append below: