package com.example.administrator.magiccamera.Filter.base;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.example.administrator.magiccamera.R;
import com.example.administrator.magiccamera.utils.MatrixUtils;
import com.example.administrator.magiccamera.utils.OpenGlUtils;
import com.example.administrator.magiccamera.utils.Rotation;
import com.example.administrator.magiccamera.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Administrator on 2018/6/11 0011.
 */

public class BaseFilter {
    protected Context context;

    /**变换矩阵*/
    private float[] mMVPMatrix;
    /**程序句柄*/
    protected int mProgram;

    /**顶点坐标句柄*/
    protected int mPositionHandle;
    /**纹理坐标句柄*/
    protected int mTextureCoordHandle;
    /**默认纹理贴图句柄*/
    protected int mSampleTexHandle;
    /**总变换矩阵句柄*/
    protected int mMVPMatrixHandle;

    /**顶点坐标Buffer*/
    protected FloatBuffer mVerBuffer;
    /**纹理坐标Buffer*/
    protected FloatBuffer mTexBuffer;

    /**默认使用纹理单元0*/
    protected int textureUnit = 0;
    /**默认使用纹理对象0*/
    protected int textureID = 0;

    // number of coordinates per vertex in this array(x,y)
    private static final int COORDS_PER_VERTEX = 2;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per

    public BaseFilter(Context context) {
        this.context = context;

        initBuffer();
        createProgram();
        getHandle();
        bindTexture();
    }

    private void initBuffer() {
        /**顶点坐标Buffer初始化*/
        mVerBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVerBuffer.put(TextureRotationUtil.CUBE).position(0);

        /**纹理坐标Buffer初始化*/
        mTexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        rotateTexture();
        //        /**默认将s纹理Y轴翻转*/
//        mTexBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);

        mMVPMatrix = MatrixUtils.getOriginalMatrix();
    }

    protected void rotateTexture() {
        mTexBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);
    }

    private void getHandle() {
        if (mProgram != 0) {
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"vMatrix");
            mSampleTexHandle = GLES20.glGetUniformLocation(mProgram,"vTexture");
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "vCoord");
        } else {
            throw new RuntimeException("failed creating program");
        }
    }

    protected void createProgram() {
        mProgram = OpenGlUtils.createProgram(
                OpenGlUtils.readShaderFromRawResource(context, R.raw.base_vertex),
                OpenGlUtils.readShaderFromRawResource(context, R.raw.base_fragment));
    }

    protected void bindTexture() {
        textureID = OpenGlUtils.loadNormalTextureID();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureUnit);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        GLES20.glUniform1i(mSampleTexHandle, textureUnit);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);

//        /**默认4x4矩阵*/
//        mMVPMatrix = MatrixUtils.getOriginalMatrix();

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle,1,false, mMVPMatrix,0);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the <insert shape here> coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mVerBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mTexBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public final void destroy() {
        int[] textures = new int[1];
        textures[0] = textureID;

        GLES20.glDeleteProgram(mProgram);
        GLES20.glDeleteTextures(1, textures,0);
    }

    protected void onDestroy() {
    }

    protected void onSizeChanged(int width, int height) {
    }

    protected void onDrawArraysPre() {}
    protected void onDrawArraysAfter() {}

    /**设置变换矩阵*/
    public final void setMVPMatrix(float[] mMVPMatrix){
        this.mMVPMatrix = mMVPMatrix;
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID (int textureId) {
        this.textureID = textureId;
    }

    public void onDisplaySizeChanged(final int width, final int height) {
//        mOutputWidth = width;
//        mOutputHeight = height;
    }
}
