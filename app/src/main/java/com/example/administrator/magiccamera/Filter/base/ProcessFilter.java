package com.example.administrator.magiccamera.Filter.base;

import android.content.Context;

import com.example.administrator.magiccamera.R;
import com.example.administrator.magiccamera.utils.OpenGlUtils;

/**
 * Created by Administrator on 2018/6/19 0019.
 */

public class ProcessFilter extends BaseFilter{

    public ProcessFilter(Context context) {
        super(context);
    }

    @Override
    protected void createProgram() {
        super.mProgram = OpenGlUtils.createProgram(
                OpenGlUtils.readShaderFromRawResource(super.context, R.raw.base_vertex),
                OpenGlUtils.readShaderFromRawResource(super.context, R.raw.base_fragment));
    }
}
