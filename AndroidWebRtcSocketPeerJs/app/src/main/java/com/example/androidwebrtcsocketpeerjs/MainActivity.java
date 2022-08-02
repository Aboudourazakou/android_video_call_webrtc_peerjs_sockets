package com.example.androidwebrtcsocketpeerjs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import io.socket.client.Socket;


public  class MainActivity extends AppCompatActivity {


    String[]permissions={Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
    int requestCode=1;
    Button loginBtn;
    TextView textView;
    Socket mSocket;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        if(!isPermissionGranted()){
            askPermissions();
        }
        loginBtn=findViewById(R.id.loginBtn);
        textView=findViewById(R.id.usernameEdit);
        mSocket=SocketIO.connectToSocketIo();



        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=textView.getText().toString();
                Intent intent=new Intent(MainActivity.this,CallActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);

            }
        });
        // FirebaseApp.initializeApp(this);
    }


    public  boolean isPermissionGranted(){
        for(String permission:this.permissions){
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return  false;
            }
        }
        return  true;
    }


    public  void askPermissions(){
        ActivityCompat.requestPermissions(this,permissions,requestCode);
    }


}
