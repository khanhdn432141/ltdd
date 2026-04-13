package com.example.flashcardapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashcardapp.MainActivity
import com.example.flashcardapp.data.AppDatabase
import kotlinx.coroutines.flow.first // Cần thiết để lấy dữ liệu từ Flow

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {

            val db = AppDatabase.getDatabase(context)
            val dao = db.cardDao()



            val now = System.currentTimeMillis()
            val dueCount = dao.getTotalDueCardCount(now).first()

            showNotification(dueCount)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(dueCount: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Nhắc nhở ôn tập",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc nhở ôn tập flashcard hàng ngày"
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (title, body) = if (dueCount > 0) {
            "Đến giờ ôn tập rồi! 📚" to "Bạn có $dueCount thẻ cần ôn tập hôm nay."
        } else {
            "Nhắc nhở học tập 📖" to "Hãy vào app để học thêm kiến thức mới nhé!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Bạn có thể thay bằng icon app của mình
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "flashcard_reminder"
        const val NOTIFICATION_ID = 1001
    }
}