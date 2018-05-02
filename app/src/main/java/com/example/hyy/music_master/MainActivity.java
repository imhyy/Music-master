package com.example.hyy.music_master;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

//import com.example.yujian.music.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
//声明一遍变量
    private ListView lvShow;
    private MusicAdapter musicAdapter;
    private MyDatabaseHelper myDatabaseHelper;
    private List<Music> musicList;
    private int index=-1;
    private Dialog dialogDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //true表示该方法执行完毕后，点击事件不会再向下一个事件处理方法传递了
        //false表示执行完该方法后，点击事件继续向下传递
        //switch语句根据选择按钮的id进行对应运算
        switch (item.getItemId()){
            //选择了add_item
            case R.id.add_item:
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                //调用AddActivity类
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //switch语句根据选择按钮的id进行对应运算
        switch (item.getItemId()){
            case R.id.delete_item:
                //选择删除按钮
                dialogDelete.show();
                break;
            case R.id.update_item:
                //选择更新按钮
                Intent intent = new Intent(MainActivity.this,UpdateActivity.class);
                //调用UpdateActivity类
                intent.putExtra("index",index);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
        //返回选择上下文菜单(长按)
    }

    private void initView(){
        myDatabaseHelper = new MyDatabaseHelper(this);
        lvShow= (ListView) findViewById(R.id.lv_show);
        updateAdapter();//动态生成列表
        registerForContextMenu(lvShow);
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                Intent intent = new Intent(MainActivity.this,PlayActivity.class);
                intent.putExtra("index",index);
                startActivity(intent);
            }
        });
        lvShow.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                return false;
            }
        });
        //定义dialogDelete函数
        dialogDelete = new AlertDialog.Builder(MainActivity.this).
                setTitle("是否确定删除？").
                setIcon(android.R.drawable.ic_dialog_info).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Music music = musicList.get(index);
                        myDatabaseHelper.delete(music.getId());
                        updateAdapter();
                    }
                }).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }

    private void updateAdapter(){//动态生车列表
        musicList = myDatabaseHelper.query();
        musicAdapter = new MusicAdapter(this,musicList);
        lvShow.setAdapter(musicAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateAdapter();
    }
}
