package com.sjty.emsbledemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.sjty.emsbledemo.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mMainBinding.getRoot());

        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //请求权限
        XXPermissions.with(this)
                .permission(Constants.PERMISSIONS)
                .request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                Log.e("TAG", "===onGranted:Get all permissions: " + all);
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                Log.e("TAG", "===onGranted:Permanently refuse authorization, please grant manually: " + never);
                // If it is permanently rejected, jump to the application permission system setting page
                //XXPermissions.startPermissionActivity(MainActivity.this, permissions);
            }
        });
    }

    private void initListener() {
        //跳转至单连接页面
        mMainBinding.tvSingle.setOnClickListener(view -> {
            boolean granted = XXPermissions.isGranted(this, Constants.PERMISSIONS);
            if (!granted) {
                Toast.makeText(this,"Permission failed, please grant permission in settings.",Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this,SingleActivity.class));
        });
        //跳转至多连接页面
        mMainBinding.tvMore.setOnClickListener(view -> {
            boolean granted = XXPermissions.isGranted(this, Constants.PERMISSIONS);
            if (!granted) {
                Toast.makeText(this,"Permission failed, please grant permission in settings.",Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this,MoreActivity.class));
        });
    }
}