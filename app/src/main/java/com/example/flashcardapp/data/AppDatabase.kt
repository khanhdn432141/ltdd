package com.example.flashcardapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Khai báo cả 2 Entity: Card và Deck
@Database(entities = [Card::class, Deck::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Khai báo các DAO để CardViewModel có thể gọi dữ liệu
    abstract fun cardDao(): CardDao
    abstract fun deckDao(): DeckDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flashcard_database"
                )
                    // Nếu Trung thay đổi cấu trúc Database (thêm cột, thêm bảng),
                    // dòng này giúp app không bị crash mà tự xóa data cũ tạo lại cái mới.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}