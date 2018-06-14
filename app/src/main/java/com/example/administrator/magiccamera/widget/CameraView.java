package com.example.administrator.magiccamera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.administrator.magiccamera.Filter.OesFilter;
import com.example.administrator.magiccamera.camera.CameraEngine;
import com.example.administrator.magiccamera.drawer.CameraDrawer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2018/6/12 0012.
 */

public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{
    private final static String TAG = "CameraView";
    private Context mContext;

    private CameraEngine mCamera;

    private OesFilter oesFilter;

    SurfaceTexture mSurface;
    int mTextureID = -1;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        /**初始化OpenGL的相关信息*/
        setEGLContextClientVersion(2);//设置版本
        setRenderer(this);//设置Renderer
        setRenderMode(RENDERMODE_WHEN_DIRTY);//主动调用渲染
        setPreserveEGLContextOnPause(true);//保存Context当pause时
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        oesFilter = new OesFilter(mContext);

        mSurface = new SurfaceTexture(oesFilter.getTextureID());
        mSurface.setOnFrameAvailableListener(this);

        /**初始化相机的管理类*/
        mCamera = new CameraEngine();
        mCamera.close();
        mCamera.open();

        mCamera.setPreviewTexture(mSurface);

    }

    //TODO 若预览大小改变，则
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        mCamera.startPreview();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mSurface.updateTexImage();

        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        oesFilter.setMVPMatrix(mtx);
        oesFilter.draw();

//        mCameraDrawer.draw(mtx);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.requestRender();
    }
}
