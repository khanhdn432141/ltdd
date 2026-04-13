package com.example.flashcardapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory để khởi tạo CardViewModel với tham số Application
 * Giúp khớp với định nghĩa class CardViewModel(application: Application)
 */
class CardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem lớp yêu cầu có phải là CardViewModel hay không
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            // SỬA LỖI: Truyền 'application' thay vì 'repository' để khớp kiểu dữ liệu
            return CardViewModel(application) as T
        }

        // Ném ngoại lệ nếu yêu cầu một ViewModel không hợp lệ
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}