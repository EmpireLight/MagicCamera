package com.example.administrator.magiccamera.camera;

import android.graphics.Point;
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

    /**相机实体*/
    private Camera mCamera;
    private int cameraID;

    /**相机的宽高及比例配置*/
    private Config mConfig;
    /**预览的尺寸*/
    private Camera.Size PreSize;
    /**实际的尺寸*/
    private Camera.Size PicSize;

    private Point mPreSize ;
    private Point mPicSize ;

    class Config {
        float rate; //宽高比
        int minPreviewWidth;
        int minPictureWidth;
    }

    /**初始化一个默认的格式大小*/
    public CameraEngine() {
        this.mConfig = new Config();
        mConfig.minPreviewWidth = 720;
        mConfig.minPictureWidth = 720;
        mConfig.rate = 1.778f;//16:9
    }

    public void open() {
        this.close();
        if (mCamera == null) {
            //默认打开后置摄像头
            try {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("打开摄像头失败", e);
            }

            Log.e(TAG, "open: camera end");
            if (mCamera != null) {
                this.setParameters();
                this.cameraID = 1;
            }
        }
    }

    public void open(int cameraId) {
        if (mCamera == null) {
            mCamera = Camera.open(cameraId);
            if (mCamera != null) {
                this.cameraID = cameraId;
                this.setParameters();
            }
        }
    }

    /**选择当前设备允许的预览尺寸*/
    private void setParameters() {
        Camera.Parameters param = mCamera.getParameters();
        PreSize = getPropPreviewSize(param.getSupportedPreviewSizes(), mConfig.rate, mConfig.minPreviewWidth);
        param.setPreviewSize(PreSize.width, PreSize.height);
        PicSize = getPropPictureSize(param.getSupportedPictureSizes(), mConfig.rate, mConfig.minPictureWidth);
        param.setPictureSize(PicSize.width, PicSize.height);

        param.setRotation(90);

        mCamera.setParameters(param);
        Camera.Size pre = param.getPreviewSize();
        Camera.Size pic = param.getPictureSize();
        mPicSize = new Point(pic.height, pic.width);
        mPreSize = new Point(pre.height, pre.width);
        Log.d(TAG, "openCamera: previewSize = " + pre.width + pic.height);
    }

    public void close() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void switchCamera() {
        close();
        cameraID = (cameraID == 0 ? 1: 0);
        open(cameraID);
        startPreview();
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void startPreview() {
        if(mCamera!=null){
            mCamera.startPreview();
        }
    }

    public void stopPreview(){
        if(mCamera!=null) {
            mCamera.stopPreview();
        }
    }

    public void setPreviewTexture(SurfaceTexture texture){
        if(mCamera != null){
            try {
                mCamera.setPreviewTexture(texture);
                Log.d(TAG, "setPreviewTexture: ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setConfig(Config config) {
        this.mConfig=config;
    }

//    public CameraInfo getCameraInfo() {
//        if(mCamera != null) {
//            Camera.Size size = mCamera.getParameters().getPreviewSize();
//            CameraInfo cameraInfo = new CameraInfo();
//            cameraInfo.previewWidth = size.width;
//            cameraInfo.previewHeight = size.height;
//            Camera.CameraInfo cameraInfo1 = new Camera.CameraInfo();
//            Camera.getCameraInfo(cameraID, cameraInfo1);
//            cameraInfo.orientation = cameraInfo1.orientation;
//
//            Camera.Size pictureSize = mCamera.getParameters().getPictureSize();
//            cameraInfo.pictureWidth = pictureSize.width;
//            cameraInfo.pictureHeight = pictureSize.height;
//            return cameraInfo;
//        }
//        return null;
//    }

    public Point getPreviewSize() {
        return mPreSize;
    }

    public Point getPictureSize() {
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

    //TODO 理解算法（Comparator类是用来排序的）
    private static Comparator<Camera.Size> sizeComparator=new Comparator<Camera.Size>(){
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
