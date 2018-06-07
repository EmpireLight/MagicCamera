package com.example.administrator.magiccamera.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.example.administrator.magiccamera.camera.utils.CameraInfo;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/5/17.
 */

public class CameraEngine {
    private static final String TAG = "CameraEngine";

    private static Camera mCamera = null;
    private static int cameraID = 0;

    private SurfaceTexture mTexture;
    private boolean isPreview;

    private Camera.Size mPicSize;
    private Camera.Size mPreSize;

    private Config mConfig;

    class Config {
        float rate; //宽高比
        int minPreviewWidth;
        int minPictureWidth;
    }

    public CameraEngine() {
        this.mConfig = new Config();
        mConfig.minPreviewWidth = 720;
        mConfig.minPictureWidth = 720;
        mConfig.rate = 1.778f;//16:9
    }

    public void openCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(cameraID);
                setParameters();
            }catch (Exception e) {

            }
        }
    }

    public void openCamera(int cameraId) {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(cameraId);
                setParameters();
            }catch (Exception e) {

            }
        }
    }

    private void setParameters() {
        Camera.Parameters param=mCamera.getParameters();

        //获取并设置符合参数的照片分辨率
        mPicSize = getPropPictureSize(param.getSupportedPictureSizes(), mConfig.rate, mConfig.minPictureWidth);
        param.setPictureSize(mPicSize.width, mPicSize.height);

        //获取并设置符合参数的预览分辨率
        mPreSize = getPropPreviewSize(param.getSupportedPreviewSizes(), mConfig.rate, mConfig.minPreviewWidth);
        param.setPreviewSize(mPreSize.width, mPreSize.height);

        mCamera.setParameters(param);

        Log.i(TAG, "openCamera: previewSize = " + mPreSize.width + mPreSize.height);
    }

    public void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void switchCamera() {
        stopCamera();
        cameraID = cameraID == 0 ? 1: 0;
        openCamera(cameraID);
        startPreview(mTexture);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void startPreview(SurfaceTexture mTexture) {
        this.mTexture = mTexture;
        if (mCamera != null && !isPreview) {
            try {
                mCamera.setPreviewTexture(mTexture);
                mCamera.startPreview();
                isPreview = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPreview() {
        if (mCamera != null && isPreview) {
            mCamera.stopPreview();
            isPreview = false;
        }
    }

    public void setPreviewTexture(SurfaceTexture texture){
        if(mCamera!=null){
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setConfig(Config config) {
        this.mConfig=config;
    }

    public void startPreview() {
        if(mCamera!=null){
            mCamera.startPreview();
        }
    }

    public CameraInfo getCameraInfo() {
        if(mCamera != null) {
            Camera.Size size = mCamera.getParameters().getPreviewSize();
            CameraInfo cameraInfo = new CameraInfo();
            cameraInfo.previewWidth = size.width;
            cameraInfo.previewHeight = size.height;
            Camera.CameraInfo cameraInfo1 = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraID, cameraInfo1);
            cameraInfo.orientation = cameraInfo1.orientation;

            Camera.Size pictureSize = mCamera.getParameters().getPictureSize();
            cameraInfo.pictureWidth = pictureSize.width;
            cameraInfo.pictureHeight = pictureSize.height;
            return cameraInfo;
        }
        return null;
    }

    public Camera.Size getPreviewSize() {
        return mPreSize;
    }

    public Camera.Size getPictureSize() {
        return mPicSize;
    }

    /**
     * 找到合适的预览尺寸
     *
     * 根据传进来的长宽比(主要是16:9 或 4:3两种尺寸)自动寻找适配的PreviewSize和PictureSize，消除变形。
     * 默认的是全屏，因为一些手机全屏时，屏幕的长宽比不是16:9或4:3所以在找尺寸时也是存在一些偏差的。其中有个值，就是判断两个float是否相等，
     * 这个参数比较关键，里面设的0.03.经我多个手机测试，这个参数是最合适的，否则的话有些奇葩手机得到的尺寸拍出照片变形
     *
     * 参考：https://blog.csdn.net/yanzi1225627/article/details/33028041
     *
     * @param list
     * @param th
     * @param minWidth
     * @return
     */
    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.height >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }

    /**
     * 找到合适的照片尺寸
     *
     * @param list
     * @param th
     * @param minWidth
     * @return
     */
    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Camera.Size s:list){
            if((s.height >= minWidth) && equalRate(s, th)){
                break;
            }
            i++;
        }
        if(i == list.size()){
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }

    //TODO 理解算法
    private static boolean equalRate(Camera.Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.03)
        {
            return true;
        }
        else{
            return false;
        }
    }

    //TODO 理解算法
    private Comparator<Camera.Size> sizeComparator=new Comparator<Camera.Size>(){
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if(lhs.height == rhs.height){
                return 0;
            }
            else if(lhs.height > rhs.height){
                return 1;
            }
            else{
                return -1;
            }
        }
    };
}
