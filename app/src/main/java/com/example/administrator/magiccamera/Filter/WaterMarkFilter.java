package com.example.administrator.magiccamera.Filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.PluralRules;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.administrator.magiccamera.Filter.base.BaseFilter;
import com.example.administrator.magiccamera.Filter.base.ProcessFilter;
import com.example.administrator.magiccamera.utils.OpenGlUtils;

/**
 * 水印
 * Created by Administrator on 2018/6/11 0011.
 */

public class WaterMarkFilter extends ProcessFilter{
    private final static String TAG = "WaterMarkFilter";
    /**水印的放置位置和宽高*/
    public int x,y,w,h;

    /**图片宽高*/
    public int imgWitdh, imgHeight;

    public WaterMarkFilter(Context context) {
        super(context);
        super.setTextureID(OpenGlUtils.loadNormalTextureID());
    }

    public void setWaterMark(Bitmap bitmap) {
        imgWitdh = bitmap.getWidth();
        imgHeight = bitmap.getHeight();
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);//给纹理加载图片
        bitmap.recycle();
    }

    public void setPosition(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
        Log.e(TAG, "setPosition: x=" + x +", y=" + y +", w=" + width +", h="+ height);
    }

    @Override
    public void draw() {
        GLES20.glViewport(x,y,
                w == 0 ? imgWitdh :w,
                h == 0 ? imgHeight:h);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_ALPHA);
        super.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{super.getTextureID()} , 0);
    }
}
