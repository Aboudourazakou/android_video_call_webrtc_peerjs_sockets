package com.example.androidwebrtcsocketpeerjs;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public  class

CallActivity extends AppCompatActivity {

    private String username = "";
    private String friendsUsername = "";
    // FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    boolean isAudio=true;
    boolean isVideo=true;
    boolean isPeerConnected=false;
    Socket mSocket;
    Button callButton;
    ImageView toggleAudioButton,toggleVideoBtn;
    WebView webView;
    View callLayout;
    TextView incomingCaller;
    ImageView acceptCallButton;
    String uniqueId;
    EditText friendNameEdit;
    ImageView rejectButton;
    LinearLayout callControlLayout;





    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_call);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        mSocket=SocketIO.connectToSocketIo();
        callButton=findViewById(R.id.callBtn);
        toggleAudioButton=findViewById(R.id.toggleAudioBtn);
        toggleVideoBtn=findViewById(R.id.toggleVideoBtn);
        webView=findViewById(R.id.webView);
        callLayout=findViewById(R.id.callLayout);
        incomingCaller=findViewById(R.id.incomingCallTxt);
        acceptCallButton=findViewById(R.id.acceptBtn);
        rejectButton=findViewById(R.id.rejectBtn);
        callControlLayout=findViewById(R.id.callControlLayout);


        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendNameEdit=findViewById(R.id.friendNameEdit);
                friendsUsername=friendNameEdit.getText().toString();
                sendCallRequest();
            }
        });

        toggleAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              isAudio=!isAudio;
              callJavaScriptFunctions("javascript:toggleAudio(\""+isAudio+"\")");
                if(isAudio) toggleAudioButton.setImageResource(R.drawable.ic_baseline_call_24);
                else toggleAudioButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
            }
        });

        toggleVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               isVideo=!isVideo;
                callJavaScriptFunctions("javascript:toggleAudio(\""+isVideo+"\")");
                if(isVideo) toggleVideoBtn.setImageResource(R.drawable.ic_baseline_videocam_24);
                else toggleVideoBtn.setImageResource(R.drawable.ic_baseline_videocam_off_24);
            }
        });

        setUpWebView();

    }


    public  void  setUpWebView(){

        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if(request!=null){
                    request.grant(request.getResources());
                }
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
        loadVideoCall();
    }


    public  void onPeerConnected(){
            isPeerConnected=true;
    }

    public  void loadVideoCall(){
          String path="file:android_asset/call.html";
          webView.loadUrl(path);
          webView.setWebViewClient(new WebViewClient(){
              @Override
              public void onPageFinished(WebView view, String url) {
                  super.onPageFinished(view, url);
                   initializePeer();
              }
          });
    }


    public  void  initializePeer(){


            setUniqueId();
           callJavaScriptFunctions("javascript:init(\"" + this.uniqueId + "\")");

               // callJavaScriptFunctions("javascript:startCall( \""+ "1" + "\")" );

           mSocket.on("incoming_call", new Emitter.Listener() {
               @Override
               public void call(Object... args) {
                    String username= (String) args[0];
                   System.out.println("Someone is calling 1");
                   onCallRequest(username);
               }
           });

        mSocket.on("accept_call", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                 switchToControls();
                System.out.println(args[0].toString()+" id de l'appelant");
                String val=args[0].toString();
                callJavaScriptFunctions("javascript:startCall( \""+ val + "\")" );


            }
        });
        mSocket.on("reject_call", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String username= (String) args[0];
                System.out.println(username +"rejectted the call");
                Toast.makeText(CallActivity.this, friendsUsername+" a rejete votre appel", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(CallActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

               // onCallRequest(username);
            }
        });
    }



   private  void onCallRequest(String val){

         if(val!=null){
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     callLayout.setVisibility(View.VISIBLE);
                     incomingCaller.setText(val);
                 }
             });
         }
         acceptCallButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                  mSocket.emit("accept_call",uniqueId);
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          callLayout.setVisibility(View.GONE);
                          switchToControls();
                      }
                  });
             }
         });

         rejectButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
               mSocket.emit("reject_call",uniqueId);
               switchToControls();
             }
         });


   }
    private  void callJavaScriptFunctions(String jsFunction){
        webView.post(new Runnable() {
            @Override
            public void run() {
               webView.evaluateJavascript(jsFunction,null);
            }
        });
    }

    private  void switchToControls(){
         runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 RelativeLayout inputLayout=findViewById(R.id.inputLayout);
                 inputLayout.setVisibility(View.GONE);
                 callControlLayout.setVisibility(View.VISIBLE);
             }
         });
    }


    private  void sendCallRequest(){
        if(!isPeerConnected){
            Toast.makeText(this, "You're not connected", Toast.LENGTH_SHORT).show();
        }
        mSocket.emit("call",username);

    }

    @Override
    public   void onBackPressed(){
           finish();
    }
    @Override
    public  void onDestroy(){
        webView.loadUrl("about:blank");
        super.onDestroy();

    }

    private  String getUniqueId(){
       return  this.uniqueId;
    }
    private void  setUniqueId(){
          this.uniqueId= UUID.randomUUID().toString();
    }



}
