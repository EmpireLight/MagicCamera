package com.example.administrator.magiccamera;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2018/6/7.
 */

public class MagicCameraActivity extends AppCompatActivity{
    public static final String TAG = "MagicCameraActivity";

    public GLSurfaceView mGLView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new GLSurfaceView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause(); //当Activity暂停时，告诉GLSurfaceView也停止渲染，并释放资源。
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume(); //当Activity恢复时，告诉GLSurfaceView加载资源，继续渲染。
    }
}

