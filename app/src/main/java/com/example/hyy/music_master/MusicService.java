package com.example.hyy.music_master;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;


public class MusicService extends Service{
    //声明变量
    private final IBinder iBinder = new MusicBinder();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    public class MusicBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public void prepare(String path){
        mediaPlayer.reset();
        //用try catch 捕获异常错误
        try {
            //尝试寻找多媒体文件路径
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            //寻找失败弹出提示
            e.printStackTrace();
            Toast.makeText(this,"未找到该音乐文件",Toast.LENGTH_SHORT).show();
        }
    }

    public void start(){
        mediaPlayer.start();
        //播放音乐
    }

    public void stop(){
        mediaPlayer.stop();
        //停止音乐
    }

    public void pause(){
        mediaPlayer.pause();
        //暂停音乐
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
        //获取媒体总长度
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
        //为了获取进度条当前位置
    }

    public void seekTo(int msec){
        mediaPlayer.seekTo(msec);
        //调整进度条快进
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener){
        //设置mediaplayer的监听为了实现自动播放
        mediaPlayer.setOnCompletionListener(onCompletionListener);
    }

}
