package com.shavaizk.materialmusic


import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener


class MainActivity : AppCompatActivity(), PermissionListener, ServiceConnection,
    MediaController.MediaPlayerControl {

    var songList: ArrayList<Song>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var musicService: MusicService? = null
    private var playSongIntent: Intent? = null
    private var musicController: MusicController? = null
    private var serviceBinded: Boolean = false

    var songsFolderList: ArrayList<Uri>? = null
    val musicUtilities: MaterialMusicUtilities = MaterialMusicUtilities()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setMusicController()

        songList = ArrayList()
        songsFolderList = ArrayList()
        Dexter.withContext(this).withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(
                this
            ).check()
    }

    override fun onStart() {
        super.onStart()
        if (playSongIntent == null) {
            playSongIntent = Intent(this, MusicService::class.java)
            bindService(playSongIntent, this, Context.BIND_AUTO_CREATE)
            startService(playSongIntent)


        }
    }

    public fun songPicked(view: View) {
        Toast.makeText(
            applicationContext,
            "Song selected: " + view.getTag().toString(),
            Toast.LENGTH_SHORT
        )
        var songSelected = songList!!.find { it.ID == view.getTag().toString().toLong() }
        musicService!!.position = songList!!.indexOf(songSelected)
        musicService!!.play()
        musicController!!.show()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_end -> {
                stopService(playSongIntent)
                musicService = null
                System.exit(0)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        stopService(playSongIntent)
        musicService = null
        val mNotificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancelAll()
        super.onDestroy()
    }

    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
        songList = musicUtilities.retrieveSongsList(activity = this, songList = songList!!)
        viewManager = LinearLayoutManager(this)
        viewAdapter = SongRecyclerAdapter(songList!!)

        recyclerView = findViewById<RecyclerView>(R.id.song_recycler).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
        this.finishAffinity();
    }

    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
        DialogOnDeniedPermissionListener.Builder.withContext(applicationContext)
            .withTitle("Storage Permission").withMessage(
                "Storage permission is needed to find all of your songs"
            ).withButtonText("Okay").build()

    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        var binder: MusicService.SongServiceBinder = p1 as MusicService.SongServiceBinder
        musicService = binder.getService()
        musicService!!.songs = songList
        serviceBinded = true

    }

    private fun setMusicController() {
        musicController = MusicController(this@MainActivity)

        musicController!!.setPrevNextListeners(
            View.OnClickListener { playNext() },
            View.OnClickListener { playPrev() })



        musicController!!.setMediaPlayer(this)
        musicController!!.setAnchorView(findViewById(R.id.song_recycler))
        musicController!!.isEnabled = true



    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        serviceBinded = false
    }

    fun onTaskRemoved(rootIntent: Intent?) {
        val mNotificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancelAll()
    }

    fun playNext() {
        musicService!!.playNext()
        musicController!!.show(0)

    }

    fun playPrev() {
        musicService!!.playPrev()
        musicController!!.show(0)
    }

    override fun start() {
        musicService!!.startPlayer()
    }

    override fun pause() {
        musicService!!.pause()
    }

    override fun getDuration(): Int {
        if (musicService != null && serviceBinded && musicService!!.playing()) {
            return musicService!!.duration()
        }
        return 0
    }

    override fun getCurrentPosition(): Int {

        if (musicService != null && serviceBinded && musicService!!.playing()) {
            return musicService!!.playerPosition()
        }
        return 0
    }

    override fun seekTo(p0: Int) {
        if (musicService != null && serviceBinded && musicService!!.playing()) {
            return musicService!!.seekTo(p0)
        }
    }

    override fun isPlaying(): Boolean {
        if (musicService != null && serviceBinded) {
            return musicService!!.playing()
        }
        return false
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        TODO("Not yet implemented")
    }


}
