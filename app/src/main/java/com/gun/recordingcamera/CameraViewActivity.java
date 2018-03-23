package com.gun.recordingcamera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class CameraViewActivity extends Activity {

    CameraScreen cs;

    SurfaceView mCameraSurface ;
    SurfaceHolder mCamraHolder;
    Button RecordeBtn, takePictureBtn, stopRecodeBtn;

    MediaRecorder recorder;

    Camera mCamera;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);


        RecordeBtn = (Button)findViewById(R.id.start_recoding);
        takePictureBtn = (Button)findViewById(R.id.take_picture);
        stopRecodeBtn = (Button)findViewById(R.id.stop_recoding);

        mCameraSurface = (SurfaceView)findViewById(R.id.camera_view);

        RecordeBtn.setOnClickListener(btnClickEvent);
        stopRecodeBtn.setOnClickListener(btnClickEvent);

        takePictureBtn.setOnClickListener(btnClickEvent);

        stopRecodeBtn.setOnClickListener(stopRecoding);

        cs = new CameraScreen(this,(SurfaceView) findViewById(R.id.camera_view));

        cs.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        cs.setKeepScreenOn(true);

        mCamraHolder  = mCameraSurface.getHolder();
        mCamraHolder.addCallback(cs);

    }

    @Override
    protected void onResume() {

        super.onResume();

        int numOfCam = Camera.getNumberOfCameras();
        if(numOfCam > 0){
            try{
                mCamera= Camera.open();
                mCamera.enableShutterSound(false);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                cs.setCamera(mCamera);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCamera != null){
            mCamera.stopPreview();
            cs.setCamera(null);
            mCamera.release();
            mCamera= null;
        }
    }

    boolean setPermission(String[] permissions){

        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, permissions, 0);
                return false;
            }
        }
        return true;
    }



    private View.OnClickListener btnClickEvent = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.take_picture:
                    if(setPermission(new String[]{Manifest.permission.CAMERA})){

                        takePicture();

                    }
                    break;
                case R.id.start_recoding:
                    if(setPermission(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE})){

                            startRecoding();
                    }
                    break;
            }
        }
    };


    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            cs.setCamera(mCamera);
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    void startRecoding () {
            if(RecordeBtn.getText().equals("녹음 시작")){
                RecordeBtn.setText("녹음 종료");
                startRecording();
            }
            else{
                RecordeBtn.setText("녹음 시작");
                stopRecording();
            }
    }

    void takePicture() {
            File newFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"RecordingCamera");
            Log.d("파일 경로",newFile.getAbsolutePath());
            if(!newFile.exists()){
                Log.d("Noi", String.valueOf(newFile.mkdirs()));
            }
            else{
                Log.d("Ya","있음");
            }
            mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
//            Set<String> stringSet = new HashSet<String>();
//            stringSet.add(String.valueOf(Environment.getExternalStoragePublicDirectory("RecordingCamera")));
//
//            for(String string : stringSet){
//
//                Log.d("file path",string);
//
//                File newFIle = new File(string);
//                if(!newFIle.exists()){
//                    if(!newFIle.mkdir()){
//                        File file = new File(newFIle,"");
//                        if(!newFIle.mkdirs()){
//                            Log.d("없음","no");
//                        }
//                        else{
//                            Log.d("mkdirs 생성됨","ok");
//                        }
//                    }
//                    else{
//                        Log.d("mkdir 생성됨","ok");
//                    }
//                }
//                else{
//                    Log.d("있음","ok");
//                }
//            }
    }

    View.OnClickListener stopRecoding= new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    void startRecording(){
        Log.d("recording","start");

        try{
            if(recorder != null){
                recorder.stop();
                recorder.release();
                recorder = null;
            }
            Calendar calendar = Calendar.getInstance();
            recorder = new MediaRecorder();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                File newFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"RecordingCamera");
                if(!newFile.exists()){
                    Log.d("Noi!", String.valueOf(newFile.mkdirs()));
                }
                recorder.setOutputFile(newFile + "/recoding_" + calendar.getTimeInMillis() + ".mp3");
            }

            else{
                recorder.setOutputFile(getExternalCacheDir()+"/recoding_"+calendar.getTimeInMillis() + ".mp3");
            }
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            recorder.prepare();
            recorder.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    void stopRecording(){
        Log.d("recording","stop");

        try{
            if(recorder == null)
                return;

            recorder.stop();
            recorder.release();
            recorder = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outFile));
                sendBroadcast(mediaScanIntent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

}
