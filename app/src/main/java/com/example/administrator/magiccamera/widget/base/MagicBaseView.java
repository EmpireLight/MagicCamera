package com.example.administrator.magiccamera.widget.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.administrator.magiccamera.utils.OpenGlUtils;
import com.example.administrator.magiccamera.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2018/6/7.
 */

public class MagicBaseView extends GLSurfaceView implements GLSurfaceView.Renderer{

    /**
     * SurfaceTexure纹理id
     */
    protected int textureId = OpenGlUtils.NO_TEXTURE;

    /**
     * 顶点坐标
     */
    protected final FloatBuffer gLCubeBuffer;

    /**
     * 纹理坐标
     */
    protected final FloatBuffer gLTextureBuffer;

    /**
     * GLSurfaceView的宽高
     */
    protected int surfaceWidth, surfaceHeight;

    /**
     * 图像宽高
     */
    protected int imageWidth, imageHeight;

    public MagicBaseView(Context context) {
        this(context, null);
    }

    public MagicBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 创建顶点坐标和纹理坐标
        gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        //配置OpenGL ES，主要是版本设置和设置Renderer，Renderer用于执行OpenGL的绘制
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glDisable(GL10.GL_DITHER);////关闭抗抖动
        GLES20.glClearColor(0.0f,0.0f, 0.0f, 0.0f);//设置清屏颜色
        GLES20.glEnable(GL10.GL_CULL_FACE);//开启剔除效果 参考：https://www.jianshu.com/p/ee04165f2a02
        GLES20.glEnable(GL10.GL_DEPTH_TEST);//开启深度缓冲测试
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width, height);
        surfaceWidth = width;
        surfaceHeight = height;
        //onFilterChanged();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
