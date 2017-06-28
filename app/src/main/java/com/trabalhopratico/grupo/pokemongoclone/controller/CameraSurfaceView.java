package com.trabalhopratico.grupo.pokemongoclone.controller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.trabalhopratico.grupo.pokemongoclone.R;
import com.trabalhopratico.grupo.pokemongoclone.util.MyApp;

import java.io.IOException;

public class CameraSurfaceView extends  Fragment {
    Camera mCamera;
    CameraPreview mPreview;
    Activity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInsatnce){
        View view = inflater.inflate(R.layout.surface_view, container, false);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        activity = getActivity();
        mCamera = getCameraInstance();
        safeCameraOpenInView(getActivity().findViewById(android.R.id.content));
    }

    @Override
    public void  onStop(){
        releaseCameraAndPreview();
        super.onStop();

    }

    private Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        }catch(Exception e){e.printStackTrace();}
        return c;
    }

    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if(mPreview != null){
            mPreview.destroyDrawingCache();
            //mPreview.mCamera = null;
        }
    }

    private boolean safeCameraOpenInView(View view) {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        qOpened = (mCamera != null);

        if(qOpened){
            mPreview = new CameraPreview(activity.getBaseContext(), mCamera);
            SurfaceView preview = (SurfaceView) view.findViewById(R.id.camera_surface_view);


            mPreview.setHolder(preview);
            mPreview.startCameraPreview();
        }
        return qOpened;
    }

    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public void setHolder(SurfaceView surfaceView){
            mHolder = surfaceView.getHolder();
            mHolder.addCallback(this);

        }
        /**
         * This is called immediately after the surface is first created.
         * Implementations of this should start up whatever rendering code
         * they desire.  Note that only one thread can ever draw into
         * a {@link Surface}, so you should not draw into the Surface here
         * if your normal rendering will be in another thread.
         *
         * @param holder The SurfaceHolder whose surface is being created.
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try{
                mCamera.setPreviewDisplay(mHolder);
                mCamera.setDisplayOrientation(getDegreeCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, mCamera));
                mCamera.startPreview();
            }catch (IOException e) {
                Log.d("captura", "Error setting camera preview: " + e.getMessage() );
            }
        }

        /**
         * This is called immediately after any structural changes (format or
         * size) have been made to the surface.  You should at this point update
         * the imagery in the surface.  This method is always called at least
         * once, after {@link #surfaceCreated}.
         *
         * @param holder The SurfaceHolder whose surface has changed.
         * @param format The new PixelFormat of the surface.
         * @param width  The new width of the surface.
         * @param height The new height of the surface.
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here
            int degree = getDegreeCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            mCamera.setDisplayOrientation(90);
            Log.i("captura", "orientation=");
            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d("captura", "Error starting camera preview: " + e.getMessage());
            }

        }

        /**
         * This is called immediately before a surface is being destroyed. After
         * returning from this call, you should no longer try to access this
         * surface.  If you have a rendering thread that directly accesses
         * the surface, you must ensure that thread is no longer touching the
         * Surface before returning from this function.
         *
         * @param holder The SurfaceHolder whose surface is being destroyed.
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseCameraAndPreview();
        }
        public void startCameraPreview() {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private int getDegreeCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360; // compensate the mirror
            } else { // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            return result;
        }
    }
}