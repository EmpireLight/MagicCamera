package com.example.administrator.magiccamera.Filter;

import android.content.Context;

import com.example.administrator.magiccamera.Filter.base.BaseFilter;

/**
 * 磨皮滤镜
 * Created by Administrator on 2018/6/11 0011.
 */

public class BuffingFilter extends BaseFilter {
    Context context;

    BuffingFilter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
