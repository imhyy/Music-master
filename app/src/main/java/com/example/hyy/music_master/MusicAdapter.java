package com.example.hyy.music_master;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;


public class MusicAdapter extends BaseAdapter{
    //声明变量
    private Context context;
    private List<Music> musicList;

    public MusicAdapter(Context context,List<Music> musicList){
        //为当前context赋值为context
        this.context = context;
        //为当前musiclist赋值为musicList
        this.musicList = musicList;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder vh = null;
        if(view==null){
            vh = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_music,null);
            vh.tvItemName = (TextView) view.findViewById(R.id.tv_item_name);
            vh.tvItemSinger = (TextView) view.findViewById(R.id.tv_item_singer);
            vh.tvItemAlbum = (TextView) view.findViewById(R.id.tv_item_album);
            vh.imgItemSound = (ImageView) view.findViewById(R.id.img_item_sound);
            view.setTag(vh);
        }
        else{
            vh = (ViewHolder) view.getTag();
        }
        Music music = musicList.get(position);
        vh.tvItemName.setText(music.getName());
        vh.tvItemSinger.setText(music.getSinger());
        vh.tvItemAlbum.setText(music.getAlbum());
        if(music.getSound()==0){
            vh.imgItemSound.setImageDrawable(null);
        }
        else if(music.getSound()==1){
            vh.imgItemSound.setImageDrawable(context.getResources().getDrawable(R.drawable.hq));
        }
        else{
            vh.imgItemSound.setImageDrawable(context.getResources().getDrawable(R.drawable.sq));
        }
        return view;
    }

    class ViewHolder{
        TextView tvItemName;
        TextView tvItemSinger;
        TextView tvItemAlbum;
        ImageView imgItemSound;
    }
}
