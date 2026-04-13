package com.example.flashcardapp.data

import kotlinx.coroutines.flow.Flow

// FIX #3: Interface chuẩn, khớp với các hàm thực tế trong CardDao/DeckDao
interface CardRepository {
    fun getDecks(): Flow<List<Deck>>
    fun getCardsByDeck(deckId: Long): Flow<List<Card>>
    fun getDueCardCount(deckId: Long): Flow<Int>

    suspend fun insertCard(card: Card)
    suspend fun insertDeck(deck: Deck)
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
    suspend fun deleteDeck(deckId: Long)
}
