package com.example.androidwebrtcsocketpeerjs;


import android.webkit.JavascriptInterface;

public class JavaScriptInterface {

    private CallActivity callActivity;


    @JavascriptInterface
    public final void onPeerConnected() {
        this.callActivity.onPeerConnected();
    }

    public final CallActivity getCallActivity() {
        return this.callActivity;
    }

    public JavaScriptInterface( CallActivity callActivity) {
        //Intrinsics.checkParameterIsNotNull(callActivity, "callActivity");

        this.callActivity = callActivity;
    }
}
