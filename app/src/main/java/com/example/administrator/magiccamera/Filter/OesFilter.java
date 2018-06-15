package com.example.administrator.magiccamera.Filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.example.administrator.magiccamera.Filter.base.BaseFilter;
import com.example.administrator.magiccamera.R;
import com.example.administrator.magiccamera.utils.OpenGlUtils;
import com.example.administrator.magiccamera.utils.TextureRotationUtil;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class OesFilter extends BaseFilter{

    public OesFilter(Context context) {
        super(context);
    }

    @Override
    protected void rotateTexture() {
        mTexBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90).position(0);
    }

    @Override
    protected void createProgram() {
        mProgram = OpenGlUtils.createProgram(
                OpenGlUtils.readShaderFromRawResource(context, R.raw.oes_base_vertex),
                OpenGlUtils.readShaderFromRawResource(context, R.raw.oes_base_fragment));
    }

    @Override
    protected void bindTexture() {
        textureID = OpenGlUtils.loadExternalOESTextureID();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);
        GLES20.glUniform1i(mSampleTexHandle, textureUnit);
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
