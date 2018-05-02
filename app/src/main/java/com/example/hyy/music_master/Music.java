package com.example.hyy.music_master;

public class Music {
    //声明变量(仅在当前class有效)
    private int id;
    private String name;
    private String singer;
    private String album;
    private String path;
    private int sound;

    public Music(){

    }

    public Music(int id, String name, String singer, String album, String path, int sound) {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.path = path;
        this.sound = sound;
    }

    public int getId() {
        //获取Id
        //返回id
        return id;
    }

    public void setId(int id) {
        this.id = id;
        //为id赋值为当前的id
    }

    public String getName() {
        //获取Name
        //返回name
        return name;

    }

    public void setName(String name) {
        this.name = name;
        //为name赋值为当前的name
    }

    public String getSinger() {
        //获取Singer
        //返回singer
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
        //为singer赋值为当前的singer
    }

    public String getAlbum() {
        //获取Album
        //返回album
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
        //为album赋值为当前的album
    }

    public String getPath() {
        //获取Path
        //返回path
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        //为path赋值为当前的path
    }

    public int getSound() {
        //获取Sound
        //返回sound
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
        //为sound赋值为当前的sound
    }
}
