package com.example.hyy.music_master;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity {

    private RoundImageView imgCover;//图片
    private ImageView imgStart;//开始按钮
    private ImageView imgPrev;//上一首
    private ImageView imgNext;//下一首
    private ImageView imgPlayType;//种类
    private TextView tvName;//名字
    private TextView tvSinger;//歌手
    private TextView tvSound;//
    private TextView tvNowTime;//正在播放时间
    private TextView tvAllTime;//播放总时间
    private SeekBar seekBar;
    private boolean isStop=true;    //判断是否音乐播放状态
    private MusicService musicService;
    private boolean mBound=false;
    private String path="";
    private int index=-1;     //当前所播放的音乐索引
    private MyDatabaseHelper myDatabaseHelper;
    private List<Music> musicList;
    private int musicNowTime;
    private Timer mTimer;
    private int type = 0;  //播放类型，循环播放0，单曲播放1，随机播放2；
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
     initView();
        Intent intent=new Intent(this,MusicService.class);
        bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(PlayActivity.this,"服务开启",Toast.LENGTH_SHORT).show();
    }

    //初始化控件属性
    private void initView(){
        //为变量赋值,关联控件
        Intent intent = getIntent();
        index = intent.getIntExtra("index",-1);
        imgCover = (RoundImageView)findViewById(R.id.img_cover);
        myDatabaseHelper = new MyDatabaseHelper(this);
        musicList = myDatabaseHelper.query();
        tvName = (TextView) findViewById(R.id.tv_name);
        tvSinger = (TextView) findViewById(R.id.tv_singer);
        tvSound = (TextView) findViewById(R.id.tv_sound);
        tvNowTime = (TextView) findViewById(R.id.tv_now_time);
        tvAllTime = (TextView) findViewById(R.id.tv_all_time);
        imgStart = (ImageView) findViewById(R.id.img_start);
        imgPrev = (ImageView) findViewById(R.id.img_prev);
        imgNext = (ImageView) findViewById(R.id.img_next);
        imgPlayType = (ImageView) findViewById(R.id.img_play_type);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);//拖动条
        imgStart.setOnClickListener(new MyOnClickListener());
        imgPrev.setOnClickListener(new MyOnClickListener());
        imgNext.setOnClickListener(new MyOnClickListener());
        imgPlayType.setOnClickListener(new MyOnClickListener());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicService.seekTo(seekBar.getProgress());
            }
        });
    }

    //初始化服务内的媒体类以及有关媒体类get到的信息设置
    private void playPerpare(){
        Music music = musicList.get(index);
        path = music.getPath();
        tvName.setText(music.getName());
        tvSinger.setText(music.getSinger());
        int sound = music.getSound();
        if(sound==0){
            //根据sound 的值来判断输出的音质标识为 标准 HQ SQ 中的哪一个
            tvSound.setText("  标准  ");
        }
        else if(sound==1){
            tvSound.setText("  HQ  ");
        }
        else {
            tvSound.setText("  SQ  ");
        }
        long albumID = -1;
        long songID = -1;
        Cursor cursor = getArtwork();
        if(cursor != null){
            //如果cursor不为空则
            songID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            //歌曲的ID为数据库中_ID的值
            albumID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            //albumID的值为数据库中ALBUM_ID
        }
        imgCover.setImageBitmap(getArtworkFromFile(songID,albumID));
        //图片获取对应的songID,albumID的图片
        musicService.prepare(path);
        //音乐服务的预备路径
        final int musicAllTime = musicService.getDuration();
        //定义音乐的时间长度
        seekBar.setMax(musicAllTime);
        //进度条
        tvAllTime.setText(correctTime(musicAllTime/60000)+":"+correctTime(musicAllTime/1000%60));
        //播放的文件时间总长度且转换时间单位
        mTimer = new Timer();
        //mTimer 赋值为 new Timer()
        TimerTask mTimerTask = new TimerTask(){

            @Override
            public void run() {
                musicNowTime = musicService.getCurrentPosition();
                //当前播放到的时间为music Service用getCurrentPosition函数获取的当前时间的位置信息
                seekBar.setProgress(musicNowTime);
                //进度条的时间为musicNowTime
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvNowTime.setText(correctTime(musicNowTime/60000)+":"+correctTime(musicNowTime/1000%60));
                        //当前播放的文件时间长度且转换时间单位
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask,0,10);
        musicStart();
    }

    //获取当前mp3文件的信息
    private Cursor getArtwork(){
        //try catch捕获异常
        try {
            String nowpath = null;
            //定义变量nowpath为空
            int i = 0;
            //获取数据库中文件路径位置
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if(cursor.moveToFirst()) {
                //如果指向查询结果的第一个位置
                do {
                    nowpath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    if(nowpath.equals(path)){
                        //如果当前路径等于获取路径则结束
                        break;
                    }
                    i++;
                    //i+1
                    if(i==cursor.getCount()){
                        //如果i等于序列个数则返回值为空
                        return null;
                    }
                }while (cursor.moveToNext());
                //当cursor指向下一个位置
            }
            return cursor;
            //返回cursor
        }catch (Exception e){
            return null;
        }
    }

    //获取当前mp3的封面
    private Bitmap getArtworkFromFile(long songID,long albumID){
        Bitmap bm=null;
        //try catch 捕获异常状态
        try {
            if(songID<0&&albumID<0){
                //当songID和albumID均小于0时bm 被赋值为默认backgrounde(bg)颜色
                bm = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.bg));
            }
            else {
                if (albumID < 0) {
                    Uri uri = Uri.parse("content://media/external/audio/media/" + songID + "/albumart");
                    //为uri赋值为当前音乐的路径
                    ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
                    //打开路径文件用getContentResolver获取内容
                    if (pfd != null) {
                        //如果pfd不为空
                        bm = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                    }
                } else {
                    Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumID);
                    //根据albumID为urr赋值
                    ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
                    if (pfd != null) {
                        //如果pfd不为空
                        bm = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());//获取到图片
                    }
                }
            }
        } catch (FileNotFoundException e) {
            //没有找到路径的处理防止闪退
            e.printStackTrace();
        }
        if(bm==null){
            bm = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.bg));
        }
        return bm;
    }

    //纠正显示的时间格式
    private String correctTime(int time){
        if(time/10==0){
            return "0"+time;
        }else {
            return time+"";
        }
    }

    //控件的单击事件
    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //利用switch来选择按钮执行对应操作
            switch (v.getId()){
                case R.id.img_start:

                    if(isStop){
                        musicStart();
                        //如果没有播放则播放
                    }
                    else{
                        musicStop();
                        //如果播放则停止
                    }
                    break;
                case R.id.img_prev:
                    //上一曲
                    prevMusic();
                    break;
                case R.id.img_next:
                    //下一曲
                    nextMusic();
                    break;
                case R.id.img_play_type:
                    //切换播放模式
                    if(type==2){
                        type = 0;
                    }
                    else {
                        type++;
                    }
                    switch (type){
                        case 0:
                            imgPlayType.setImageDrawable(getResources().getDrawable(R.drawable.xunhuan));
                            //列表循环
                            break;
                        case 1:
                            imgPlayType.setImageDrawable(getResources().getDrawable(R.drawable.danqu));
                            //单曲循环
                            break;
                        case 2:
                            imgPlayType.setImageDrawable(getResources().getDrawable(R.drawable.suiji));
                            //随机播放
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
    }

    //播放音乐
    private void musicStart(){
        imgStart.setImageDrawable(getResources().getDrawable(R.drawable.player_stop));
        musicService.start();
        imgCover.startRun();
        isStop=false;
    }

    //暂停音乐
    private void musicStop(){
        imgStart.setImageDrawable(getResources().getDrawable(R.drawable.player_start));
        musicService.pause();
        imgCover.stopRun();
        isStop=true;
    }

    //切换成上一首歌
    private void prevMusic(){
        mTimer.cancel();
        if(index == 0){
            index = musicList.size()-1;
        }
        else {
            index--;
    }
        playPerpare();
    }

    //切换成下一首歌按index计数来播放音乐
    private void nextMusic(){
        mTimer.cancel();
        if(index == musicList.size()-1){
            index = 0;
        }
        else {
            index++;
        }
        playPerpare();
    }

    //当前活动结束后的响应
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBound){
            musicService.stop();
            unbindService(mServiceConnection);
            mBound=false;
            isStop=true;
        }
        Toast.makeText(PlayActivity.this,"服务关闭",Toast.LENGTH_SHORT).show();
    }


    //问服务连接是否开了线程   与服务建立连接自动播放的线程
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder musicBinder= (MusicService.MusicBinder) service;
            musicService = musicBinder.getService();
            mBound=true;
            playPerpare();
            //这个响应事件不能放initView()中的原因是musicService要在服务开启后才能调用
            //不放playPerpare()中是不用重复执行，所以才放这边的。
            musicService.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    switch (type){
                        case 0:
                            nextMusic();//列表循环
                            break;
                        case 1:
                            mTimer.cancel();//单曲循环
                            playPerpare();
                            break;
                        case 2:
                            mTimer.cancel();
                            index = random.nextInt(musicList.size());//生成随机数
                            playPerpare();
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
