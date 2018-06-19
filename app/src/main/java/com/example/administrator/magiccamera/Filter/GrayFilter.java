package com.example.administrator.magiccamera.Filter;

import android.content.Context;

import com.example.administrator.magiccamera.Filter.base.BaseFilter;
import com.example.administrator.magiccamera.R;
import com.example.administrator.magiccamera.utils.OpenGlUtils;

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class GrayFilter extends BaseFilter{
    public GrayFilter(Context context) {
        super(context);
    }

    @Override
    protected void createProgram() {
        super.mProgram = OpenGlUtils.createProgram(
                OpenGlUtils.readShaderFromRawResource(super.context, R.raw.gray_vertex),
                OpenGlUtils.readShaderFromRawResource(super.context, R.raw.gray_fragment));
    }
}
