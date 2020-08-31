package com.shavaizk.materialmusic

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MusicNotification {

    public val CHANNEL_ID: String = "MUSIC_CONTROLS_CHANNEL"
    public final val ACTIONPREVIOUS: String = "actionprevious"
    public final val ACTIONPLAY: String = "actionplay"
    public final val ACTIONNEXT: String = "actionnext"
    public var draw_play_pause: Int? = null


    public final var drawable_previous: Int? = null
    public var notification: Notification? = null

    public fun createNotification(context: Context, song: Song, isPlaying: Boolean) {
        var notificationManagerCompat: NotificationManagerCompat =
            NotificationManagerCompat.from(context)
        var mediaSessionCompat: MediaSessionCompat = MediaSessionCompat(context, "MaterialMusicSession")
        var icon: Bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_media_play_dark)


        var pendingIntentPreviousSong: PendingIntent
        var intentPrevious: Intent =
            Intent(context, NotificationActionBR::class.java).setAction(ACTIONPREVIOUS)
        pendingIntentPreviousSong = PendingIntent.getBroadcast(
            context,
            0,
            intentPrevious,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (isPlaying) {
            draw_play_pause = R.drawable.baseline_pause_black_24dp
        }else{
            draw_play_pause = R.drawable.baseline_play_arrow_black_24dp
        }

        var pendingIntentPlaySong: PendingIntent
        var intentPlay: Intent =
            Intent(context, NotificationActionBR::class.java).setAction(ACTIONPLAY)
        pendingIntentPlaySong = PendingIntent.getBroadcast(
            context,
            0,
            intentPlay,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var pendingIntentNextSong: PendingIntent
        var intentNext: Intent =
            Intent(context, NotificationActionBR::class.java).setAction(ACTIONNEXT)
        pendingIntentNextSong = PendingIntent.getBroadcast(
            context,
            0,
            intentNext,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(draw_play_pause!!)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setLargeIcon(icon)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                R.drawable.baseline_skip_previous_black_24dp,
                "Previous",
                pendingIntentPreviousSong
            )
            .addAction(draw_play_pause!!, "Play", pendingIntentPlaySong)
            .addAction(R.drawable.baseline_skip_next_black_24dp, "Next", pendingIntentNextSong)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
            .build()

        notificationManagerCompat.notify(1, notification!!)
    }
}