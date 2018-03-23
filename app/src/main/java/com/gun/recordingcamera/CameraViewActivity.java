package com.gun.recordingcamera;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.Calendar;

public class CameraViewActivity extends Activity {

    CameraScreen cs;

    SurfaceView mCameraSurface ;
    SurfaceHolder mCamraHolder;
    Button RecordeBtn, takePictureBtn, stopRecodeBtn;

    MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        RecordeBtn = (Button)findViewById(R.id.start_recoding);
        takePictureBtn = (Button)findViewById(R.id.take_picture);
        stopRecodeBtn = (Button)findViewById(R.id.stop_recoding);

        mCameraSurface = (SurfaceView)findViewById(R.id.camera_view);
        RecordeBtn.setOnClickListener(startRecoding);

        takePictureBtn.setOnClickListener(takePicture);

        stopRecodeBtn.setOnClickListener(stopRecoding);

        cs = new CameraScreen(this);
        mCamraHolder  = mCameraSurface.getHolder();
        mCamraHolder.addCallback(cs);
    }

    void setPermission(String[] permissions){

        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, permissions, 0);
                return;
            }
        }

    }

    View.OnClickListener startRecoding = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(RecordeBtn.getText().equals("녹음 시작")){
                RecordeBtn.setText("녹음 종료");
                startRecording();
            }
            else{
                RecordeBtn.setText("녹음 시작");
                stopRecording();
            }
        }
    };

    View.OnClickListener takePicture= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File newFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"RecordingCamera");

            Log.d("파일 경로",newFile.getAbsolutePath());

            if(!newFile.exists()){
                Log.d("Noi", String.valueOf(newFile.mkdirs()));
            }

            else{
                Log.d("Ya","있음");

            }
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
    };

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

}
