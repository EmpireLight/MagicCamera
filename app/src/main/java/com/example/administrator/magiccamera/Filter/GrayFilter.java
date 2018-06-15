package com.example.administrator.magiccamera.Filter;

import android.content.Context;

import com.example.administrator.magiccamera.Filter.base.BaseFilter;
import com.example.administrator.magiccamera.R;
import com.example.administrator.magiccamera.utils.OpenGlUtils;
import com.example.administrator.magiccamera.utils.TextureRotationUtil;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class GrayFilter extends BaseFilter{

    public GrayFilter(Context context) {
        super(context);
    }

//    @Override
//    protected void rotateTexture() {
//        super.mTexBuffer.put(TextureRotationUtil.TEXTURE_ROTATED_90).position(0);
//    }

    @Override
    protected void createProgram() {
        mProgram = OpenGlUtils.createProgram(
                OpenGlUtils.readShaderFromRawResource(context, R.raw.gray_vertex),
                OpenGlUtils.readShaderFromRawResource(context, R.raw.gray_fragment));
    }

}
