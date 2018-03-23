package com.gun.recordingcamera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CameraScreen  implements SurfaceHolder.Callback, Camera.PreviewCallback  {

    private Camera mCamera = null;
//	private ImageView processImage = null;

    private int imageFormat;
    private int previewWidth = 1280;
    private int previewHeight = 720;
    private boolean bAutoFocusing = false;
    private boolean supported = false;
    private CameraViewActivity act;


    public CameraScreen(CameraViewActivity pAct /*,ImageView pProcessImage*/) {

        act = pAct;
//		processImage = pProcessImage;

        cameraStart();

    }

    public void cameraStart() {
        String filePath = act.getFilesDir().getAbsolutePath() + "/tessdata";

        File file = new File(filePath);
        if (file.exists()) {

//			Log.println(Log.INFO, "Camera", "Camera start");

        }

        try {
            if (mCamera == null)
                mCamera = Camera.open();

            else {
                mCamera.release();
                mCamera = Camera.open();
            }

            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
            for (int i = 0; i < previewSizeList.size(); i++) {

                Camera.Size size = previewSizeList.get(i);

                if (size.width == previewWidth && size.height == previewHeight) {
                    supported = true;
                    break;
//					Log.println(Log.INFO, "Camera", "Camera supports 1280x720 resolution");
                }
            }

            if (supported) {
                parameters.setPreviewSize(previewWidth, previewHeight);
                parameters.setRotation(90);
                imageFormat = parameters.getPreviewFormat();

                mCamera.setDisplayOrientation(90);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } else {
                mCamera.release();
            }
        } catch (RuntimeException e) {
            cameraStop();
            act.finish();
        }
    }

    public void cameraStop() {
//		Log.println(Log.INFO, "Camera", "Camera stop");

        try {
            if (mCamera != null) {
//				Parameters parameters = mCamera.getParameters();
//				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
//				mCamera.setParameters(parameters);
                mCamera.startPreview();
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (RuntimeException e) {
            mCamera = null;
        }

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (!bAutoFocusing) {
                Thread autoFocusThread = new Thread(doAutoFocusingProcessing);
                autoFocusThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

//		Log.println(Log.INFO, "Camera", "Surface changed");

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

//		Log.println(Log.INFO, "Camera", "Surface created");

        try {

            if (holder != null) {

                mCamera.setPreviewDisplay(holder);

            }

            mCamera.setPreviewCallback(this);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//		Log.println(Log.INFO, "Camera", "Surface destroyed");
    }

    public void setFocus() {

        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();

            List<String> focusMode = parameters.getSupportedFocusModes();

            if (focusMode.contains("macro"))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
            else if (focusMode.contains("auto"))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            try {
                List<Camera.Area> areas = new LinkedList<Camera.Area>();
                Camera.Area area = new Camera.Area(new Rect(-1000, -1000, -400, 1000), 1000);
                areas.add(area);

                parameters.setFocusAreas(areas);
                mCamera.setParameters(parameters);
                mCamera.autoFocus(null);
            } catch (RuntimeException e) {
                cameraStop();
                act.finish();
            }
//			Log.println(Log.INFO, "Camera", "Tab to focus, focusMode: " + parameters.getFocusMode());
        }

    }

    public boolean isSupported() {
        return supported;
    }

    private Runnable doAutoFocusingProcessing = new Runnable() {
        public void run() {
            bAutoFocusing = true;
            setFocus();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bAutoFocusing = false;
        }
    };

}