package android.example.com.squawker.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.example.com.squawker.MainActivity
import android.example.com.squawker.R
import android.example.com.squawker.provider.SquawkContract
import android.example.com.squawker.provider.SquawkProvider
import android.media.RingtoneManager
import android.os.AsyncTask
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by pmvb on 17-08-12.
 */
class SquawkFirebaseMessagingService: FirebaseMessagingService() {

    companion object {
        val TAG = SquawkFirebaseMessagingService::class.java.simpleName

        val JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR
        val JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY
        val JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE
        val JSON_KEY_DATE = SquawkContract.COLUMN_DATE

        val NOTIFICATION_MAX_LENGTH = 30
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        Log.e(TAG, "From: ${message?.from}")

        val data = message?.data
        if (data != null && data.size > 0) {
            Log.e(TAG, "Message payload: $data")
            sendNotification(data)
            insertSquawk(data)
        }
    }

    private fun sendNotification(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val author = data[JSON_KEY_AUTHOR]
        var message = data[JSON_KEY_MESSAGE]!!
        if (message.length > NOTIFICATION_MAX_LENGTH) {
            message = message.substring(0..NOTIFICATION_MAX_LENGTH) + "\u2026"
        }
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(getString(R.string.notification_message, author))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)

        var manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }

    private fun insertSquawk(data: Map<String, String>) {
        val uri = SquawkProvider.SquawkMessages.CONTENT_URI
        Log.e("SquawkMessagingService", uri.toString())

        val insertTask = object: AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg p0: Unit?) {
                val values = ContentValues()
                values.put(SquawkContract.COLUMN_AUTHOR, data[JSON_KEY_AUTHOR])
                values.put(SquawkContract.COLUMN_AUTHOR_KEY, data[JSON_KEY_AUTHOR_KEY])
                values.put(SquawkContract.COLUMN_MESSAGE, data[JSON_KEY_MESSAGE])
                values.put(SquawkContract.COLUMN_DATE, data[JSON_KEY_DATE])

                contentResolver.insert(uri, values)
            }
        }
        insertTask.execute()
    }
}