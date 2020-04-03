package com.beaconproject.main.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.UI.SignIn.SignInPage;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class WelcomePage extends AppCompatActivity implements View.OnTouchListener {

    SharedPreferences sharedPreferences;

    private View decorView;
    private String macAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        /// 宣告Shared Preference ///
        sharedPreferences = getSharedPreferences("BeaconProjectData" , MODE_PRIVATE);
        /// 宣告Shared Preference ///

        /// 宣告全域變數 ///
        GlobalVariable data = (GlobalVariable) getApplicationContext();
        /// 宣告全域變數 ///

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        ///取得mac地址
        macAddress = getMacAddress();
        if(macAddress==null) finish();
        else sharedPreferences.edit().putString("macAddress",macAddress).apply();
        ///取得mac地址

        /// 讀取SQL看有沒有註冊過
        SQLConnection sqlc = new SQLConnection(data.sqlIP,data.sqlport,data.sqldbname,data.sqluser,data.sqlpass);
        sqlc.Connection();
        String sqlStmt = String.format("SELECT * FROM `user` WHERE `macAddress`=\"%s\"",macAddress);
        ResultSet result = sqlc.executeSQL(sqlStmt);
        int count = 0;
        try{
            if(result!=null) {
                result.last();
                count = result.getRow();
                if(count>0){
                    Intent intent = new Intent();
                    sharedPreferences.edit().putString("userId",result.getString("userId")).apply();
                    sharedPreferences.edit().putString("usercode",result.getString("usercode")).apply();
                    sharedPreferences.edit().putString("userEmail",result.getString("email")).apply();
                    sharedPreferences.edit().putString("username",result.getString("name")).apply();
                    sharedPreferences.edit().putString("userPhone",result.getString("phone")).apply();
                    intent.setClass(WelcomePage.this,HomePage.class);
                    startActivity(intent);
                    finish();
                    sqlc.close();
                }
            }
            else{
                Toast.makeText(this, "無法連線至SQL", Toast.LENGTH_LONG).show();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        /// 讀取SQL看有沒有註冊過
    }

    public void intoSignIn(View view){
        /// Into Next Page ///
        Intent intent = new Intent();
        intent.setClass(WelcomePage.this, SignInPage.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        init();
        super.onStart();
    }

    private void init(){
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (!checkDeviceHasNavigationBar()) {
            return;
        } else {
            decorView.setSystemUiVisibility(flag);
        }
    }

    public boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private String getMacAddress(){
        try{
            byte[] macBytes = null;
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface nif : all) {
                if(!nif.getName().equalsIgnoreCase("wlan0")) continue;
                macBytes = nif.getHardwareAddress();
                if(macBytes==null){return null;}
            }
            StringBuilder resource = new StringBuilder();
            for(Byte b : macBytes)
                resource.append(String.format("%02X:",b));
            if(resource.length()>0){
                resource.deleteCharAt(resource.length()-1);
            }
            return resource.toString();
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (Exception e){ e.printStackTrace(); }
        return null;
    }
}
