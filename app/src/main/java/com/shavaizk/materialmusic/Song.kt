package com.shavaizk.materialmusic

class Song : Comparable<Song>{
    public var ID: Long? = null
    public var title: String? = ""
    public var album: String? = ""
    public var artist: String? = ""


    constructor(ID: Long?, title: String?, album: String?, artist: String?) {
        this.ID = ID
        this.title = title
        this.album = album
        this.artist = artist
    }

    override fun compareTo(other: Song): Int {
        return this.title!!.toLowerCase().compareTo(other.title!!.toLowerCase())
    }


}