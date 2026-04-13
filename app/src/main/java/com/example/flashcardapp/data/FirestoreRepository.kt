package com.example.flashcardapp.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userId: String? get() = auth.currentUser?.uid

    // --- 1. PULL: Tải dữ liệu từ Cloud về máy ---
    suspend fun pullDecksFromCloud(deckDao: DeckDao, cardDao: CardDao) {
        val uid = userId ?: return
        try {
            val deckSnapshot = firestore.collection("users").document(uid)
                .collection("decks").get().await()

            for (doc in deckSnapshot.documents) {
                // Thêm try-catch nhỏ bên trong để nếu 1 card lỗi không làm văng cả app
                try {
                    val deck = doc.toObject(Deck::class.java) ?: continue
                    deckDao.insertDeck(deck)

                    val cardSnapshot = doc.reference.collection("cards").get().await()
                    for (cardDoc in cardSnapshot.documents) {
                        val card = cardDoc.toObject(Card::class.java) ?: continue
                        cardDao.insertCard(card)
                    }
                } catch (e: Exception) {
                    Log.e("Firestore", "Lỗi dòng dữ liệu cụ thể: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Lỗi tải dữ liệu: ${e.message}")
        }
    }

    // --- 2. OBSERVE: Lắng nghe thay đổi real-time và cập nhật local DB ---
    // FIX #7: Nhận scope từ ViewModel để launch coroutine đúng cách
    fun observeCloudDecks(deckDao: DeckDao, cardDao: CardDao, scope: CoroutineScope) {
        val uid = userId ?: return
        firestore.collection("users").document(uid).collection("decks")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Lỗi observe: ${e.message}")
                    return@addSnapshotListener
                }
                snapshot?.documents?.forEach { doc ->
                    val deck = doc.toObject(Deck::class.java) ?: return@forEach
                    // FIX: Launch coroutine để ghi vào Room
                    scope.launch(Dispatchers.IO) {
                        deckDao.insertDeck(deck)
                    }
                }
            }
    }

    // --- 3. PUSH: Đẩy dữ liệu lên Cloud ---
    fun syncDeckToCloud(deck: Deck) {
        val uid = userId ?: return
        firestore.collection("users").document(uid)
            .collection("decks").document(deck.id.toString())
            .set(deck, SetOptions.merge())
    }

    fun syncCardToCloud(card: Card) {
        val uid = userId ?: return
        firestore.collection("users").document(uid)
            .collection("decks").document(card.deckId.toString())
            .collection("cards").document(card.id.toString())
            .set(card, SetOptions.merge())
    }

    // --- 4. XÓA ---
    fun deleteDeckFromCloud(deckId: String) {
        val uid = userId ?: return
        firestore.collection("users").document(uid)
            .collection("decks").document(deckId).delete()
    }

    // FIX #2: Truyền đúng deckId để xóa card trong sub-collection đúng chỗ
    fun deleteCardFromCloud(deckId: Long, cardId: Long) {
        val uid = userId ?: return
        firestore.collection("users").document(uid)
            .collection("decks").document(deckId.toString())
            .collection("cards").document(cardId.toString())
            .delete()
    }
}
