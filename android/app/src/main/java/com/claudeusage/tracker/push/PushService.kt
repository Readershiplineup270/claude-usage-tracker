package com.claudeusage.tracker.push

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.claudeusage.tracker.Crypto
import com.claudeusage.tracker.Prefs
import com.claudeusage.tracker.R
import com.claudeusage.tracker.RelayClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Receives FCM **data** messages (never notification messages, so Google never sees
 * plaintext), decrypts them with the pairing key, and posts a local notification.
 */
class PushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        val p = Prefs.load(this) ?: return
        CoroutineScope(Dispatchers.IO).launch { runCatching { RelayClient(p).registerPushToken(token) } }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val p = Prefs.load(this) ?: return
        val nonce = message.data["nonce"] ?: return
        val ct = message.data["ct"] ?: return
        val plain = Crypto.openString(p.e2eeKeyB64, nonce, ct) ?: return
        val o = runCatching { JSONObject(plain) }.getOrNull() ?: return
        showNotification(
            title = o.optString("title", "Claude Usage"),
            body = o.optString("body", ""),
            tag = o.optString("tag", ""),
        )
    }

    private fun showNotification(title: String, body: String, tag: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return

        val n = NotificationCompat.Builder(this, "usage_alerts")
            .setSmallIcon(R.drawable.ic_stat_usage)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val id = (if (tag.isNotEmpty()) tag else title).hashCode()
        NotificationManagerCompat.from(this).notify(id, n)
    }
}
