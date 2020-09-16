package com.lattice.audioapp.models;

import com.google.gson.annotations.SerializedName;

public class Song {

    @SerializedName("title")
    String title;

    @SerializedName("artist")
    String artist;

    @SerializedName("image")
    String image;

    @SerializedName("source")
    String source;

    @SerializedName("genre")
    String genre;

    public String getTitle() {
        return title;
    }

    public Song(String title, String artist, String source) {
        this.title = title;
        this.artist = artist;
        this.image = image;
        this.source = source;
        this.genre = genre;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
