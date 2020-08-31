package com.shavaizk.materialmusic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager


class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private val player: MediaPlayer? = MediaPlayer()
    public var songs: ArrayList<Song>? = null
    private final var songServiceBinder: SongServiceBinder? = SongServiceBinder()
    public var position: Int? = null
    private var audioAttributes: AudioAttributes? = null
    public var songTitle: String? = null
    public var musicNotification: MusicNotification? = null
    private var notificationManager: NotificationManager? = null
    private var musicServiceBR: MusicServiceBR = MusicServiceBR()



    override fun onCreate() {
        super.onCreate()
        position = 0
        setMediaPlayer()
        musicNotification = MusicNotification()
        createChannel()
        registerReceiver(musicServiceBR, IntentFilter("TRACKS_TRACKS"))
    }

    public fun setMediaPlayer() {
        audioAttributes =
            AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        player!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        player!!.setAudioAttributes(audioAttributes)
        player!!.setOnPreparedListener(this)
        player!!.setOnCompletionListener(this)
        player!!.setOnErrorListener(this)


    }

    override fun onBind(intent: Intent): IBinder {
        return songServiceBinder!!
    }

    override fun onUnbind(intent: Intent?): Boolean {
        player!!.stop()
        player!!.release()
        notificationManager!!.cancelAll()
        unregisterReceiver(musicServiceBR)
        return super.onUnbind(intent)

    }

    override fun onDestroy() {
        player!!.stop()
        player!!.release()
        notificationManager!!.cancelAll()
        unregisterReceiver(musicServiceBR)
        super.onDestroy()
    }

    override fun onPrepared(p0: MediaPlayer?) {
        p0!!.start()

    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun onCompletion(p0: MediaPlayer?) {
        playNext()
    }





    public fun play() {
        player!!.reset()
        var currentSong = songs!!.get(position!!.toInt())
        var currentSongID = currentSong.ID
        var songURI = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            currentSongID!!
        )
        songTitle = currentSong.title

        player.setDataSource(applicationContext, songURI)
        player.prepareAsync()
        musicNotification!!.createNotification(applicationContext, currentSong, true)


    }

    public fun playerPosition(): Int {
        return player!!.currentPosition
    }

    public fun getSongDuration(): Int {
        return player!!.duration
    }

    public fun playing(): Boolean {
        return player!!.isPlaying
    }

    public fun pause() {
        var currentSong = songs!!.get(position!!.toInt())
        player!!.pause()
        musicNotification!!.createNotification(applicationContext, currentSong, false)
    }

    public fun seekTo(milliSec: Int) {
        player!!.seekTo(milliSec)
    }

    public fun startPlayer() {
        player!!.start()
    }

    public fun playPrev() {
        if (position != 0) {
            position = position?.minus(1)
        } else {
            position = songs!!.size - 1
        }
        var currentSong = songs!!.get(position!!.toInt())
        musicNotification!!.createNotification(applicationContext, currentSong, playing())
        play()


    }

    public fun duration(): Int {
        return player!!.duration
    }

    public fun playNext() {
        if (position != songs!!.size - 1) {
            position = position?.plus(1)
        } else {
            position = 0
        }
        var currentSong = songs!!.get(position!!.toInt())
        musicNotification!!.createNotification(applicationContext, currentSong, playing())
        play()
    }

    public fun createChannel() {

        var notificationChannel: NotificationChannel = NotificationChannel(
            musicNotification!!.CHANNEL_ID,
            "Material Music",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager = getSystemService(NotificationManager::class.java)

        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }

    inner class MusicServiceBR : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            var action: String? = p1!!.extras!!.getString("actionname")

            if (action == MusicNotification().ACTIONPREVIOUS) {
                playPrev()
            } else if (action == MusicNotification().ACTIONPLAY) {
                if (playing()) {
                    pause()
                } else {
                    play()
                }
            } else if (action == MusicNotification().ACTIONNEXT) {
                playNext()
            }

        }
    }

    public inner class SongServiceBinder() : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

}

