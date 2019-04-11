package com.xinshen.dynamtheme;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.xinshen.dynamtheme.customView.CustumAdapter;
import com.xinshen.dynamtheme.customView.DraggableGridViewPager;

import java.io.File;

import permissioin.Permissions;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = getClass().getSimpleName();
    private Button btnDefault;

    private Button btnBlue;

    private Button btn_test1;

    private String skinPath1;    //皮肤包路径
    private String skinPath2;
    private DraggableGridViewPager drag;
    private ArrayAdapter<String> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Permissions.requestPermissionAll(this);
        btnDefault = findViewById(R.id.btn_default);
        btnBlue = findViewById(R.id.btn_blue);
        btn_test1 =  findViewById(R.id.btn_test1);
        btnDefault.setOnClickListener(this);
        btnBlue.setOnClickListener(this);
        btn_test1.setOnClickListener(this);
        drag = findViewById(R.id.drag);

        skinPath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "blue-skin.apk";
        Log.e(TAG,"skinPath1="+skinPath1);
        skinPath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.sink";
        Log.e(TAG,"path::"+skinPath2);
        initData();
    }

    private void initData() {
        CustumAdapter adapter = new CustumAdapter(this);
        drag.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_default:
                Log.e(TAG,"btn_default");
                SkinManager.getInstance().restoreDefaultTheme();
                break;
            case R.id.btn_blue:
                Log.e(TAG,"btn_blue");
                SkinManager.getInstance().loadSkin(skinPath1);
                break;
            case R.id.btn_test1:
                Log.e(TAG,"btn_test1");
                SkinManager.getInstance().loadSkin(skinPath2);
                break;
        }
    }

    /**
     * 动态权限获取
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Permissions.changePermissionState(this,permissions[0],true);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
