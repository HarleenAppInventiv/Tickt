package com.example.ticktapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.core.preferences.PreferenceManager
import com.example.ticktapp.ApplicationClass
import com.example.ticktapp.R
import com.example.ticktapp.mvvm.view.builder.HomeBuilderActivity
import com.example.ticktapp.mvvm.view.tradie.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import kotlin.random.Random


class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            Log.i("fcm", remoteMessage.data.toString())
            Log.i("fcm", PreferenceManager.getString(PreferenceManager.USER_TYPE).toString())
        } catch (e: Exception) {
            Log.i("fcm", "fcm");
        }
        if (PreferenceManager.getBoolean(PreferenceManager.IS_LOGIN)) {
            if (PreferenceManager.getString(PreferenceManager.USER_TYPE).equals("2")) {
                if (remoteMessage.data.containsKey("user_type") && remoteMessage.data.get("user_type") != null) {
                    if (remoteMessage.data.get("user_type").equals("2")) {
                        sendNotification(remoteMessage)
                    }
                } else {
                    sendNotification(remoteMessage)
                }
            } else if (PreferenceManager.getString(PreferenceManager.USER_TYPE).equals("1")) {
                if (remoteMessage.data.containsKey("user_type") && remoteMessage.data.get("user_type") != null) {
                    if (remoteMessage.data.get("user_type").equals("1")) {
                        sendTradieNotification(remoteMessage)
                    }
                } else {
                    sendTradieNotification(remoteMessage)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PreferenceManager.putString(PreferenceManager.DEVICE_TOKEN, token)
    }

    private fun sendTradieNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (remoteMessage.data != null && remoteMessage.data.containsKey("notificationType")) {
            intent.putExtra("notificationType", remoteMessage.data.get("notificationType"))
        } else if (remoteMessage.data != null && remoteMessage.data.containsKey("notification_type")) {
            intent.putExtra("notificationType", remoteMessage.data.get("notification_type"))
        }

        if (remoteMessage.data != null && remoteMessage.data.containsKey("jobId")) {
            intent.putExtra("jobId", remoteMessage.data.get("jobId"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("senderId")) {
            intent.putExtra("senderId", remoteMessage.data.get("senderId"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("receiverId")) {
            intent.putExtra("receiverId", remoteMessage.data.get("receiverId"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("jobName")) {
            intent.putExtra("jobName", remoteMessage.data.get("jobName"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("roomId")) {
            intent.putExtra("roomId", remoteMessage.data.get("roomId"))
        }
        try {
            if (remoteMessage.data.containsKey("sender")) {
                intent.putExtra(
                    "senderId",
                    JSONObject(remoteMessage.data.get("sender").toString()).get("user_id")
                        .toString()
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        intent.putExtra("title", remoteMessage.notification?.title)
        intent.putExtra("body", remoteMessage.notification?.body)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("notificationType")
            && remoteMessage.data.get("notificationType").equals("50")
        ) {
            intent.action = "RefreshUI"
            sendBroadcast(intent)

            if (remoteMessage.data.containsKey("userType") &&
                remoteMessage.data.get("userType")
                    .equals(PreferenceManager.getString(PreferenceManager.USER_TYPE))
            ) {
                return
            }
            if (remoteMessage.data.containsKey("jobId") && remoteMessage.data.get("jobId")
                    .equals((application as ApplicationClass).getJobChatId())
            ) {
                return
            }
        }

        if (remoteMessage.data != null && remoteMessage.data.containsKey("notificationType")
            && remoteMessage.data.get("notificationType").equals("25")
        ) {
            intent.action = "RefreshUI"
            sendBroadcast(intent)

            if (remoteMessage.data.containsKey("userType") &&
                remoteMessage.data.get("userType")
                    .equals(PreferenceManager.getString(PreferenceManager.USER_TYPE))
            ) {
                return
            }
            if (remoteMessage.data.containsKey("jobId") && remoteMessage.data.get("jobId")
                    .equals((application as ApplicationClass).getJobChatId())
            ) {
                return
            }
        }
        (application as ApplicationClass).refreshData(intent)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val chalID = getString(R.string.app_name)
        val notificationBuilder = NotificationCompat.Builder(this, chalID)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.app_white_icon)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.notification?.body))
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(chalID, "Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(rand(1, 10000), notificationBuilder.build())
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, HomeBuilderActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (remoteMessage.data != null && remoteMessage.data.containsKey("notificationType")) {
            intent.putExtra("notificationType", remoteMessage.data.get("notificationType"))
        } else if (remoteMessage.data != null && remoteMessage.data.containsKey("notification_type")) {
            intent.putExtra("notificationType", remoteMessage.data.get("notification_type"))
        }

        if (remoteMessage.data != null && remoteMessage.data.containsKey("jobId")) {
            intent.putExtra("jobId", remoteMessage.data.get("jobId"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("senderId")) {
            intent.putExtra("senderId", remoteMessage.data.get("senderId"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("receiverId")) {
            intent.putExtra("receiverId", remoteMessage.data.get("receiverId"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("jobName")) {
            intent.putExtra("jobName", remoteMessage.data.get("jobName"))
        }
        if (remoteMessage.data != null && remoteMessage.data.containsKey("roomId")) {
            intent.putExtra("roomId", remoteMessage.data.get("roomId"))
        }
        try {
            if (remoteMessage.data.containsKey("sender")) {
                intent.putExtra(
                    "senderId",
                    JSONObject(remoteMessage.data.get("sender").toString()).get("user_id")
                        .toString()
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        intent.putExtra("title", remoteMessage.notification?.title)
        intent.putExtra("body", remoteMessage.notification?.body)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (remoteMessage.data != null && remoteMessage.data.containsKey("notificationType")
            && remoteMessage.data.get("notificationType").equals("50")
        ) {
            intent.action = "RefreshUI"
            sendBroadcast(intent)

            if (remoteMessage.data.containsKey("userType") &&
                remoteMessage.data.get("userType")
                    .equals(PreferenceManager.getString(PreferenceManager.USER_TYPE))
            ) {
                return
            }
            if (remoteMessage.data.containsKey("jobId") && remoteMessage.data.get("jobId")
                    .equals((application as ApplicationClass).getJobChatId())
            ) {
                return
            }
        }

        if (remoteMessage.data != null && remoteMessage.data.containsKey("notificationType")
            && remoteMessage.data.get("notificationType").equals("25")
        ) {
            intent.action = "RefreshUI"
            sendBroadcast(intent)

            if (remoteMessage.data.containsKey("userType") &&
                remoteMessage.data.get("userType")
                    .equals(PreferenceManager.getString(PreferenceManager.USER_TYPE))
            ) {
                return
            }
            if (remoteMessage.data.containsKey("jobId") && remoteMessage.data.get("jobId")
                    .equals((application as ApplicationClass).getJobChatId())
            ) {
                return
            }
        }
        (application as ApplicationClass).refreshData(intent)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val chalID = getString(R.string.app_name)
        val notificationBuilder = NotificationCompat.Builder(this, chalID)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.app_white_icon)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(remoteMessage.notification?.body))
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(chalID, "Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(rand(1, 10000), notificationBuilder.build())
    }

    fun rand(from: Int, to: Int): Int {
        return Random.nextInt(to - from) + from
    }
}