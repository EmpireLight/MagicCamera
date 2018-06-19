package com.example.administrator.magiccamera.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.example.administrator.magiccamera.Filter.GrayFilter;
import com.example.administrator.magiccamera.Filter.WaterMarkFilter;
import com.example.administrator.magiccamera.Filter.base.OesFilter;
import com.example.administrator.magiccamera.Filter.base.BaseFilter;
import com.example.administrator.magiccamera.Filter.base.FBO;
import com.example.administrator.magiccamera.Filter.base.ProcessFilter;
import com.example.administrator.magiccamera.R;
import com.example.administrator.magiccamera.camera.CameraEngine;
import com.example.administrator.magiccamera.utils.MatrixUtils;
import com.example.administrator.magiccamera.utils.OpenGlUtils;

import java.util.Arrays;

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
    private ProcessFilter showFilter;
    private GrayFilter grayFilter;
    private WaterMarkFilter waterMarkFilter;

    int viewWidth, viewHeight;
    int preWidth, preHeight;

    SurfaceTexture mSurfaceTexture;
    FBO fbo1, fbo2;
    float[] Matrix = new float[16];

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

    private void init() {
        /**初始化相机的管理类*/
        mCamera = new CameraEngine();
        mCamera.open();

        preWidth = mCamera.getPreviewSize().x;
        preHeight = mCamera.getPreviewSize().y;

        Log.d(TAG, "initFilter: imgWidth = " + preWidth + ", imgHeight ="+ preHeight);

        oesFilter = new OesFilter(mContext);
        grayFilter = new GrayFilter(mContext);

        waterMarkFilter = new WaterMarkFilter(mContext);
        waterMarkFilter.setWaterMark(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.watermark));
        waterMarkFilter.setPosition(0, 0, 0, 0);
        Log.e(TAG, "setWaterMark: imgWitdh = "+waterMarkFilter.imgWitdh+" imgHeight = "+waterMarkFilter.imgHeight);

        showFilter = new ProcessFilter(mContext);

        fbo1 = FBO.newInstance();
        fbo2 = FBO.newInstance();

//        if(mCamera.cameraID == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            MatrixUtils.rotate(oesFilter.getMVPMatrix(), 270);
//        } else {
//            MatrixUtils.rotate(oesFilter.getMVPMatrix(), 90);
//        }

        mSurfaceTexture = new SurfaceTexture(oesFilter.getTextureID());
        mSurfaceTexture.setOnFrameAvailableListener(this);

        mCamera.setPreviewTexture(mSurfaceTexture);

        fbo1.create(mCamera.getPreviewSize().x, mCamera.getPreviewSize().y);
        fbo2.create(mCamera.getPreviewSize().x, mCamera.getPreviewSize().y);

        Matrix = Arrays.copyOf(showFilter.getMVPMatrix(), showFilter.getMVPMatrix().length);
        MatrixUtils.rotate(Matrix, 90);
        showFilter.setMVPMatrix(Matrix);

//        Matrix = Arrays.copyOf(waterMarkFilter.getMVPMatrix(), waterMarkFilter.getMVPMatrix().length);
//        //MatrixUtils.flip(Matrix, true, false);
//        MatrixUtils.rotate(Matrix, 0);
//        waterMarkFilter.setMVPMatrix(Matrix);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
    }

    //TODO 若预览大小改变，则
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        viewWidth = width;
        viewHeight = height;
        Log.e(TAG, "onSurfaceChanged: " + width +" " + height);
        mCamera.startPreview();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();

        fbo1.bind();
        oesFilter.draw();//将纹理输出到FB12纹理

        //fbo2.bind();//将纹理输出到FBO2纹理
        waterMarkFilter.draw();
        fbo1.unbind();
        //fbo2.unbind();

        GLES20.glViewport(0,0,viewWidth,viewHeight);

//        fbo1.bind();
//        grayFilter.setTextureID(fbo2.getFrameBufferTextureId());//将FBO1的输出纹理作为输入纹理
//        grayFilter.draw();
//        fbo1.unbind();

        showFilter.setTextureID(fbo1.getFrameBufferTextureId());
        showFilter.draw();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.requestRender();
    }
}
