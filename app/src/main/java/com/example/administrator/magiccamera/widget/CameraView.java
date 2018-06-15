package com.example.administrator.magiccamera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.example.administrator.magiccamera.Filter.GrayFilter;
import com.example.administrator.magiccamera.Filter.OesFilter;
import com.example.administrator.magiccamera.Filter.base.BaseFilter;
import com.example.administrator.magiccamera.Filter.base.FBO;
import com.example.administrator.magiccamera.camera.CameraEngine;
import com.example.administrator.magiccamera.drawer.CameraDrawer;
import com.example.administrator.magiccamera.utils.MatrixUtils;

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
    private BaseFilter baseFilter;
    private GrayFilter grayFilter;

    int viewWidth, viewHeight;
    int imgWidth, imgHeight;

    SurfaceTexture mSurfaceTexture;
    FBO fbo;

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

    private void initFilter() {
        /**初始化相机的管理类*/
        mCamera = new CameraEngine();
        mCamera.open();

        imgWidth = mCamera.getPreviewSize().x;
        imgHeight = mCamera.getPreviewSize().y;

        Log.e(TAG, "initFilter: imgWidth = " + imgWidth + ", imgHeight ="+ imgHeight);

        oesFilter = new OesFilter(mContext);
        grayFilter = new GrayFilter(mContext);

        mSurfaceTexture = new SurfaceTexture(oesFilter.getTextureID());
        mSurfaceTexture.setOnFrameAvailableListener(this);

        fbo = FBO.newInstance();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initFilter();

        mCamera.setPreviewTexture(mSurfaceTexture);
    }

    //TODO 若预览大小改变，则
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Log.d(TAG, "onSurfaceChanged: " + width +" " + height);
        mCamera.startPreview();
    }

    float[] matrix=new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mSurfaceTexture.updateTexImage();
//todo 矩阵变换方向有问题
        MatrixUtils.rotate(oesFilter.getMVPMatrix(), 90);

        oesFilter.draw();//画到FBO纹理

        grayFilter.bindTexture(fbo.getFrameBufferTextureId());

/*
        MatrixUtils.getShowMatrix(matrix, this.imgWidth, this.imgHeight, this.viewWidth, this.viewHeight);
*/

//        MatrixUtils.rotate(grayFilter.getMVPMatrix(), 90);
//        grayFilter.setMVPMatrix(matrix);

        grayFilter.draw();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.requestRender();
    }
}
