package com.example.hyy.music_master;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.List;
//更新已有歌曲数据库
public class UpdateActivity extends AppCompatActivity {
//声明变量
    private EditText etUpdateName;
    private EditText etUpdateSinger;
    private EditText etUpdateAlbum;
    private EditText etUpdatePath;
    private Spinner spUpdateSound;
    private Button btnUpdateExecute;
    private Button btnUpdateCancel;
    private ArrayAdapter<String> adapter;
    private MyDatabaseHelper myDatabaseHelper;
    private int index = -1;
    private Music music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initView();
    }

    private void initView(){
        //关联变量与控件
        myDatabaseHelper = new MyDatabaseHelper(UpdateActivity.this);
        etUpdateName = (EditText) findViewById(R.id.et_update_name);
        etUpdateSinger = (EditText) findViewById(R.id.et_update_singer);
        etUpdateAlbum = (EditText) findViewById(R.id.et_update_album);
        etUpdatePath= (EditText) findViewById(R.id.et_update_path);
        spUpdateSound = (Spinner) findViewById(R.id.sp_update_sound);
        btnUpdateExecute = (Button) findViewById(R.id.btn_update_execute);
        btnUpdateCancel = (Button) findViewById(R.id.btn_update_cancel);
        btnUpdateExecute.setOnClickListener(new MyOnClickListener());
        btnUpdateCancel.setOnClickListener(new MyOnClickListener());
        //选择音质的下拉菜单
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"标准品质","高品质","无损品质"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUpdateSound.setAdapter(adapter);
        Intent intent = getIntent();
        index = intent.getIntExtra("index",-1);
        List<Music> musicList = myDatabaseHelper.query();
        music = musicList.get(index);
        //更新的值
        etUpdateName.setText(music.getName());
        etUpdateSinger.setText(music.getSinger());
        etUpdateAlbum.setText(music.getAlbum());
        etUpdatePath.setText(music.getPath());
        spUpdateSound.setSelection(music.getSound());
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //switch语句根据选择的不同执行相对应的步骤
            switch (v.getId()){
                case R.id.btn_update_execute:
                    //选择了更新按钮
                    //if语句中用了||来满足所有条件才可以添加
                    if(TextUtils.isEmpty(etUpdateName.getText())||TextUtils.isEmpty(etUpdateSinger.getText())
                            ||TextUtils.isEmpty(etUpdateAlbum.getText())||TextUtils.isEmpty(etUpdatePath.getText())){
                        Toast.makeText(UpdateActivity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
                        //如果上面条件之中的一个不满足则无法添加并且提示请完善信息
                    }
                    else {
                        Music updateMusic = new Music();
                        updateMusic.setId(music.getId());
                        updateMusic.setName(etUpdateName.getText().toString());
                        updateMusic.setSinger(etUpdateSinger.getText().toString());
                        updateMusic.setAlbum(etUpdateAlbum.getText().toString());
                        updateMusic.setPath(etUpdatePath.getText().toString());
                        updateMusic.setSound((int) spUpdateSound.getSelectedItemId());
                        myDatabaseHelper.update(updateMusic);
                        Toast.makeText(UpdateActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                        //如果满足则更新数据库并且输出提示更新成功
                    }
                    UpdateActivity.this.finish();
                    break;
                case R.id.btn_update_cancel:
                    //选择了取消按钮
                    UpdateActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    }
}
