package com.gun.recordingcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

public class FileListActivity extends Activity {

    Button RecordeBtn, takePictureBtn, stopRecodeBtn;

    MediaRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        RecordeBtn = (Button)findViewById(R.id.start_recoding);
        takePictureBtn = (Button)findViewById(R.id.take_picture);
        stopRecodeBtn = (Button)findViewById(R.id.stop_recoding);

        RecordeBtn.setOnClickListener(moving());

        takePictureBtn.setOnClickListener(moving());

        stopRecodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setPermission(new String[] {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA});

    }

    void setPermission(String[] permissions){

        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, permissions, 0);
                return;
            }
        }

    }

    View.OnClickListener moving(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileListActivity.this,CameraViewActivity.class);
                startActivity(intent);
            }
        };
    }
}
