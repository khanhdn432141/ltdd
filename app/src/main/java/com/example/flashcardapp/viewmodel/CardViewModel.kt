package com.example.flashcardapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcardapp.data.*
import com.example.flashcardapp.util.applySmTwo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val cardDao = db.cardDao()
    private val deckDao = db.deckDao()

    // Khởi tạo Firestore Repository (Hãy chắc chắn file FirestoreRepository.kt của bạn đã đúng)
    private val firestoreRepo = FirestoreRepository()

    val allDecks: StateFlow<List<Deck>> = deckDao.getAllDecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- FIX #1: Dùng đúng thuật toán SM-2 ---
    /**
     * Cập nhật trạng thái học của thẻ theo SM-2 chuẩn.
     * quality: 1 = Khó, 3 = Vừa, 5 = Dễ
     */
    fun updateCardLearning(cardId: Long, quality: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Lấy thẻ hiện tại
            val card = cardDao.getCardById(cardId).firstOrNull() ?: return@launch

            // 2. Áp dụng SM-2: cập nhật đúng interval, repetition, easeFactor, nextReview
            val updatedCard = applySmTwo(card, quality).copy(
                lastReview = System.currentTimeMillis()
            )

            // 3. Lưu vào Room
            cardDao.updateCard(updatedCard)

            // 4. Đồng bộ lên Firestore
            firestoreRepo.syncCardToCloud(updatedCard)
        }
    }

    fun getCardsByDeck(deckId: Long): Flow<List<Card>> {
        return if (deckId == 0L) cardDao.getAllCards() else cardDao.getCardsByDeckId(deckId)
    }

    fun getDueCardCount(deckId: Long): Flow<Int> {
        return cardDao.getDueCardCount(deckId, System.currentTimeMillis())
    }

    // --- CÁC HÀM QUẢN LÝ DỮ LIỆU ---

    fun addDeck(name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val deck = Deck(name = name, description = description)
            val newId = deckDao.insertDeck(deck)
            firestoreRepo.syncDeckToCloud(deck.copy(id = newId))
        }
    }

    fun addCard(deckId: Long, front: String, back: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val card = Card(deckId = deckId, front = front, back = back)
            val newId = cardDao.insertCard(card)
            firestoreRepo.syncCardToCloud(card.copy(id = newId))
        }
    }

    fun deleteDeck(deckId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // FIX #2: Xóa từng thẻ trên Firestore trước khi xóa deck
            val cards = cardDao.getCardsByDeckId(deckId).firstOrNull() ?: emptyList()
            cards.forEach { firestoreRepo.deleteCardFromCloud(it.deckId, it.id) }

            deckDao.deleteDeckById(deckId)
            cardDao.deleteCardsByDeckId(deckId)
            firestoreRepo.deleteDeckFromCloud(deckId.toString())
        }
    }

    // FIX #2: Truyền đủ deckId + cardId để xóa đúng sub-collection trên Firestore
    fun deleteCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            cardDao.deleteCard(card)
            firestoreRepo.deleteCardFromCloud(card.deckId, card.id)
        }
    }

    // --- ĐỒNG BỘ CLOUD ---
    fun pullFromCloud() {
        viewModelScope.launch(Dispatchers.IO) { firestoreRepo.pullDecksFromCloud(deckDao, cardDao) }
    }

    // FIX #7: Truyền viewModelScope để observeCloudDecks có thể launch coroutine
    fun observeCloudData() {
        firestoreRepo.observeCloudDecks(deckDao, cardDao, viewModelScope)
    }
}