package com.example.hyy.music_master;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


// 添加歌曲的功能
public class AddActivity extends AppCompatActivity {
    //声明变量(仅在当前类有效)
    private EditText etAddName;
    private EditText etAddSinger;
    private EditText etAddAlbum;
    private EditText etAddPath;
    private Spinner spAddSound;
    private Button btnAddExecute;
    private Button btnAddCancel;
    private ArrayAdapter<String> adapter;
    private MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
    }

    private void initView(){
        //为了让变量能与界面中的控件关联
        myDatabaseHelper = new MyDatabaseHelper(AddActivity.this);
        etAddName = (EditText) findViewById(R.id.et_add_name);
        etAddSinger = (EditText) findViewById(R.id.et_add_singer);
        etAddAlbum = (EditText) findViewById(R.id.et_add_album);
        etAddPath= (EditText) findViewById(R.id.et_add_path);
        spAddSound = (Spinner) findViewById(R.id.sp_add_sound);
        btnAddExecute = (Button) findViewById(R.id.btn_add_execute);
        btnAddCancel = (Button) findViewById(R.id.btn_add_cancel);

        //为btnAddExecute,btnAddCancel添加新的监听事件
        btnAddExecute.setOnClickListener(new MyOnClickListener());
        btnAddCancel.setOnClickListener(new MyOnClickListener());

        //选择音质的时候出现的下拉菜单
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"标准品质","高品质","无损品质"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAddSound.setAdapter(adapter);
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //switch语句根据选择的不同执行相对应的步骤
            switch (v.getId()){
                //选择了添加按钮
                case R.id.btn_add_execute:
                    //if语句中用了||来满足所有条件才可以添加
                    if(TextUtils.isEmpty(etAddName.getText())||TextUtils.isEmpty(etAddSinger.getText())
                            ||TextUtils.isEmpty(etAddAlbum.getText())||TextUtils.isEmpty(etAddPath.getText())){
                        //如果上面条件之中的一个不满足则无法添加并且提示请完善信息
                        Toast.makeText(AddActivity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //如果满足则插入数据库并且输出提示添加成功
                        Music music = new Music();
                        music.setName(etAddName.getText().toString());
                        music.setSinger(etAddSinger.getText().toString());
                        music.setAlbum(etAddAlbum.getText().toString());
                        music.setPath(etAddPath.getText().toString());
                        music.setSound((int) spAddSound.getSelectedItemId());
                        myDatabaseHelper.add(music);
                        Toast.makeText(AddActivity.this,"添加成功",Toast.LENGTH_SHORT).show();
                    }
                    AddActivity.this.finish();
                    break;
                //选择了取消按钮则关闭此页
                case R.id.btn_add_cancel:
                    AddActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    }
}
