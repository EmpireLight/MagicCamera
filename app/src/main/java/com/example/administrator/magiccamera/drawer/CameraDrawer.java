package com.example.administrator.magiccamera.drawer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.example.administrator.magiccamera.Filter.base.OesFilter;
import com.example.administrator.magiccamera.R;
import com.example.administrator.magiccamera.utils.MatrixUtils;
import com.example.administrator.magiccamera.utils.OpenGlUtils;
import com.example.administrator.magiccamera.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 摄像头的绘制类
 * Created by Administrator on 2018/6/11 0011.
 */

public class CameraDrawer {

    private Context context;

    /**显示画面的filter*/
    private OesFilter showFilter;

    private SurfaceTexture mSurfaceTextrue;
    /**预览数据的宽高*/
    private int mPreviewWidth=0, mPreviewHeight=0;
    /**控件的宽高*/
    private int width = 0,height = 0;
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private float[] SM = new float[16];     //用于显示的变换矩阵

    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int mSampleTexHandle;
    private int mMVPMatrixHandle;//总变换矩阵

    /**程序句柄*/
    protected int mProgram;

    /**顶点坐标Buffer*/
    protected FloatBuffer mVerBuffer;
    /**纹理坐标Buffer*/
    protected FloatBuffer mTexBuffer;

    private int textureID;

    // number of coordinates per vertex in this array(x,y)
    private static final int COORDS_PER_VERTEX = 2;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public CameraDrawer(final Context context) {
        this.context = context;
        //showFilter = new OesFilter(context);
        init();
    }

    public CameraDrawer(final Context context, int textureID) {
        this.context = context;
        this.textureID = textureID;
        //showFilter = new OesFilter(context);
        init();
    }

    public void init() {
        /**顶点坐标Buffer初始化*/
        mVerBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVerBuffer.put(TextureRotationUtil.CUBE).position(0);

        /**纹理坐标Buffer初始化*/
        //mTexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
        mTexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        /**默认将纹理Y轴翻转*/
        //mTexBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);
        //mTexBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
        mTexBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90).position(0);

        mProgram = OpenGlUtils.createProgram(
                OpenGlUtils.readShaderFromRawResource(context, R.raw.oes_base_vertex),
                OpenGlUtils.readShaderFromRawResource(context, R.raw.oes_base_fragment));

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"vMatrix");
        mSampleTexHandle = GLES20.glGetUniformLocation(mProgram,"vTexture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "vCoord");

        textureID = OpenGlUtils.loadExternalOESTextureID();
    }

    public void draw(float[] mMVPMatrix) {
        GLES20.glUseProgram(mProgram);

        mMVPMatrix = MatrixUtils.getOriginalMatrix();

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false, mMVPMatrix,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);
        GLES20.glUniform1i(mSampleTexHandle, 0);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mVerBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mTexBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
    }
    public int getTextureID() {
        return textureID;
    }

    public SurfaceTexture getSurfaceTextrue() {
        return mSurfaceTextrue;
    }
}
