package com.lattice.audioapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Results {

    @SerializedName("music")
    List<Song> songList;

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}
