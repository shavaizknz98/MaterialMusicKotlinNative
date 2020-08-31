package com.shavaizk.materialmusic

import android.app.Activity
import android.database.Cursor
import android.provider.MediaStore.Audio.Media
import android.util.Log


class MaterialMusicUtilities {


    public fun retrieveSongsList(activity: Activity, songList: ArrayList<Song>): ArrayList<Song> {

        val projection = arrayOf(
            Media._ID,
            Media.ARTIST,
            Media.TITLE,
            Media.ALBUM
        )
        val selection = Media.IS_MUSIC + " != 0"
        val musicCursor: Cursor = activity.contentResolver.query(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )!!



        if (musicCursor != null) {
            musicCursor.moveToFirst()
            var titleColumn: Int = musicCursor.getColumnIndex(Media.TITLE)
            var idColumn: Int = musicCursor.getColumnIndex(Media._ID)
            var artistColumn: Int = musicCursor.getColumnIndex(Media.ARTIST)
            var albumColumns: Int = musicCursor.getColumnIndex(Media.ALBUM)
            do {

                var songID: Long = musicCursor.getLong(idColumn)
                var songTitle: String = musicCursor.getString(titleColumn)
                var songAlbum: String = musicCursor.getString(albumColumns)
                var songArtist: String = musicCursor.getString(artistColumn)

                Log.d("MMMaterialMusicUtilities", "Adding Song: " + songTitle)

                songList.add(
                    Song(
                        songID,
                        songTitle,
                        songAlbum,
                        songArtist
                    )

                )


            } while (musicCursor.moveToNext())

        }
        songList.sort()
        return songList
    }


}