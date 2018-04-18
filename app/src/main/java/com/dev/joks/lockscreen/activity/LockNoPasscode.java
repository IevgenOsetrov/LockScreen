package com.dev.joks.lockscreen.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dev.joks.lockscreen.R;
import com.dev.joks.lockscreen.Values;
import com.dev.joks.lockscreen.event.RestartTimerEvent;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class LockNoPasscode extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = LockNoPasscode.class.getSimpleName();
    private static final String INTERFACE_NAME = "Android";
    private RelativeLayout layout;
    private WindowManager.LayoutParams wmParams;
    private static WindowManager mWindowManager = null;
    private SharedPreferences sharedPreferences;
    public static boolean booleanisCall = false;
    private String hiddenPass;
    private boolean hasPassCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        hasPassCode = sharedPreferences.getBoolean(Values.ENABLE_PASSCODE, false);
        super.onCreate(savedInstanceState);
        if (mWindowManager != null) {
            Log.e("..............", "oncreate return");
            return;
        }
        layout = (RelativeLayout) View.inflate(this, R.layout.activity_lock, null);

        mWindowAddView();

        WebView webView = (WebView) layout.findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDisplayZoomControls(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.addJavascriptInterface(new CustomJavascriptInterface(this), INTERFACE_NAME);
        webView.loadUrl("file:///" + Environment.getExternalStorageDirectory().toString() +
                File.separator + "LockScreen/lock_screen.html");

        telephony = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE); //TelephonyManager object
        customPhoneListener = new CustomPhoneStateListener();
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void unlock() {
        try {
            if (layout != null) {
                mWindowManager.removeView(layout);
            }
        } finally {
            mWindowManager = null;
            finish();
        }
    }

    private void mWindowAddView() {
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        wmParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        layout.setSystemUiVisibility(uiOptions);
        mWindowManager.addView(layout, wmParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public static boolean isNotFirst = true;
    TelephonyManager telephony;
    CustomPhoneStateListener customPhoneListener;

    public class BroadcastCall extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isNotFirst)
                return;
            if (isNotFirst) {
//                telephony = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE); //TelephonyManager object
//                customPhoneListener = new CustomPhoneStateListener();
//                telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE); //Register our listener with TelephonyManager

                Bundle bundle = intent.getExtras();
                String phoneNr = bundle.getString("incoming_number");
                isNotFirst = false;

            }
//            Log.v(TAG, "phoneNr: "+phoneNr);
//            mContext=context;

        }
    }

    int i = 0;
    public static boolean isfirst = true;

    public class CustomPhoneStateListener extends PhoneStateListener {


        private static final String TAG = "CustomPhoneStateListener";


        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (sharedPreferences.getBoolean(Values.ENABLE_PASSCODE, false)) {
                return;
            }
            i++;

            if (incomingNumber != null && incomingNumber.length() > 0)
//                incoming_nr=incomingNumber;

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.e("...........NO:  ", "CALL_STATE_RINGING");
                        booleanisCall = true;
                        unlock();
                        isfirst = false;
 /*                       try {

                        if (layout.isShown())
                            mWindowManager.removeView(layout);
                       }catch (Exception e){
//
                       }
//                        isNotFirst = false;*/
//                    prev_state=state;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.e("...........NO:  ", "CALL_STATE_OFFHOOK");
                        booleanisCall = true;
//                        try {
//                        if (!layout.isShown())
//
//                            mWindowManager.addView(layout, wmParams);
//                        }catch (Exception e){
//
//                        }
//                    prev_state=state;
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.e("...........NO:  ", "CALL_STATE_IDLE ");
                        if (isfirst) {
                            return;
                        }
                        isfirst = false;
                        if (!isfirst) {
                            Log.e("...........NO:  ", "CALL_STATE_IDLE startactivity ");
                            booleanisCall = false;
                            Intent intent1 = new Intent(LockNoPasscode.this, LockNoPasscode.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_FROM_BACKGROUND);
                            startActivity(intent1);
                            isfirst = true;
                        }
                        if (telephony != null && customPhoneListener != null)
                            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_NONE);
//                        if (telephony != null && customPhoneListener != null)
//                            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_NONE);
//                        if (!layout.isShown()) {
//                            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
//                            layout.setSystemUiVisibility(uiOptions);
//                            mWindowManager.addView(layout, wmParams);
//                        }

//                    if((prev_state==TelephonyManager.CALL_STATE_OFFHOOK)){
//                        prev_state=state;
//                        //Answered Call which is ended
//                    }
//                    if((prev_state==TelephonyManager.CALL_STATE_RINGING)){
//                        prev_state=state;
//                        //Rejected or Missed call
//                    }
                        break;
                }
        }
    }

    private class CustomJavascriptInterface {

        private Context context;

        CustomJavascriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void receiveValue(String toast, String hiddenPass) {
            Log.d(TAG, "Values " + toast + " " + hiddenPass);
            if (toast.equals(hiddenPass)) {
                UIUtil.hideKeyboard(LockNoPasscode.this);
                finish();
                unlock();
                EventBus.getDefault().post(new RestartTimerEvent());
                Toast.makeText(context, "Unlock", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LockNoPasscode.this,
                        "Wrong password!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}