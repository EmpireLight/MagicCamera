package com.example.administrator.magiccamera;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.magiccamera.widget.CameraView;

/**
 * Created by Administrator on 2018/6/7.
 */

public class MagicCameraActivity extends AppCompatActivity{
    private static final String TAG = "MagicCameraActivity";

    private CameraView mCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magic_camera_activity);

        initView();
    }

    private void initView() {
        mCameraView = (CameraView) findViewById(R.id.camera_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

