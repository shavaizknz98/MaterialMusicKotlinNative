package com.shavaizk.materialmusic

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongRecyclerAdapter(private val songs: ArrayList<Song>):
    RecyclerView.Adapter<SongRecyclerAdapter.SongViewHolder>() {

    class SongViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.song_cell, parent, false)) {
        private var songTitle_textView: TextView? = null
        private var songAlbum_textView: TextView? = null
        private var songArtist_textView: TextView? = null
        private var linearLayout: LinearLayout? = null

        init {
            songTitle_textView = itemView.findViewById(R.id.songTitle_textView)
            songArtist_textView = itemView.findViewById(R.id.songArtist_textView)
            songAlbum_textView = itemView.findViewById(R.id.songAlbum_textView)
            linearLayout = itemView.findViewById(R.id.songCellLinearLayout)
        }
        fun bind(song: Song) {
            songTitle_textView?.text = song.title
            songArtist_textView?.text = song.artist
            songAlbum_textView?.text = song.album
            linearLayout?.tag = song.ID
        }
    }


    override fun getItemId(p0: Int): Long {
        TODO("Not yet implemented")
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
         return SongViewHolder(inflater,parent)
    }


    override fun getItemCount(): Int {
        return  songs.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song: Song = songs[position]
        holder.bind(song)
    }

}