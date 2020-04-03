package com.beaconproject.main.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.UI.CheckIn.CheckInFirstStep;
import com.beaconproject.main.UI.Create.AttendFirstStep;
import com.beaconproject.main.UI.Manage.ManageHomePage;
import com.beaconproject.main.UI.Manage.ManageSelectActivity;
import com.beaconproject.main.UI.RecentAttend.RecentAttendActivity;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class HomePage extends AppCompatActivity implements View.OnTouchListener,OnClickListener{

    SharedPreferences sharedPreferences;
    private String Name = "使用者";
    private CardView attendCardView,createCardView,recordCardView,settingsCardView,exitCardView;
    private ImageView userPhoto;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        /// 宣告Shared Preference ///
        sharedPreferences = getSharedPreferences("BeaconProjectData" , MODE_PRIVATE);
        /// 宣告Shared Preference ///

        /// 宣告全域變數 ///
        GlobalVariable data = (GlobalVariable) getApplicationContext();
        /// 宣告全域變數 ///

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// definded CardView ///
        attendCardView = (CardView)findViewById(R.id.attend);
        createCardView = (CardView)findViewById(R.id.create);
        recordCardView = (CardView)findViewById(R.id.record);
        settingsCardView = (CardView)findViewById(R.id.settings);
        exitCardView = (CardView)findViewById(R.id.exit);
        /// definded CardView ///

        /// set OnClick Listener CardView ///
        attendCardView.setOnClickListener(this);
        createCardView.setOnClickListener(this);
        recordCardView.setOnClickListener(this);
        settingsCardView.setOnClickListener(this);
        exitCardView.setOnClickListener(this);
        /// set OnClick Listener CardView ///

        userPhoto = (ImageView)findViewById(R.id.userPhoto);
        userPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = sharedPreferences.getString("userId",null);
                if(userId!=null){
                    GlobalVariable gv = (GlobalVariable)getApplicationContext();
                    SQLConnection sqlc =new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
                    sqlc.Connection();
                    if(sqlc.connectionSuccess){
                        String stmt = String.format("SELECT `name`, `usercode`, `phone`, `email` FROM `user` WHERE `userId` = '%s'",userId);
                        ResultSet rs = sqlc.executeSQL(stmt);
                        try{
                            while(rs.next()){
                                String name = rs.getString("name");
                                String usercode = rs.getString("usercode");
                                String phone = rs.getString("phone");
                                String email = rs.getString("email");
                                android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(HomePage.this);
                                View dialogView = LayoutInflater.from(HomePage.this).inflate(R.layout.dialog_user_information,null);
                                dialogBuilder.setView(dialogView);
                                final AlertDialog showInformation = dialogBuilder.create();

                                EditText showname = dialogView.findViewById(R.id.showUserName);
                                EditText showCode = dialogView.findViewById(R.id.showUserCodes);
                                EditText showPhone = dialogView.findViewById(R.id.showUserPhone);
                                EditText showEmail = dialogView.findViewById(R.id.showUserEmail);
                                Button gobackbtn = dialogView.findViewById(R.id.goBackBtn);
                                showname.setText(name); showCode.setText(usercode); showPhone.setText(phone); showEmail.setText(email);
                                gobackbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showInformation.dismiss();
                                    }
                                });
                                showInformation.show();
                            }
                        }
                        catch (SQLException ex){
                            ex.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(HomePage.this,"無法連接SQL。",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(HomePage.this,"無資料顯示。",Toast.LENGTH_SHORT).show();
                }
            }
        });


        /// 取得資料
        Name = sharedPreferences.getString("username","null Value");
        TextView name_tv = (TextView)findViewById(R.id.name);
        if(Name != null) name_tv.setText(Name);
        else {
            name_tv.setText("Null User");
        }
        /// 取得資料
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()){
            case R.id.attend:
                i = new Intent();
                i.setClass(HomePage.this, CheckInFirstStep.class);
                startActivity(i);
                break;
            case R.id.create:
                i = new Intent();
                i.setClass(HomePage.this, AttendFirstStep.class);
                startActivity(i);
                break;
            case R.id.record:
                i = new Intent();
                i.setClass(HomePage.this, RecentAttendActivity.class);
                startActivity(i);
                break;
            case R.id.settings:
                i = new Intent();
                i.setClass(HomePage.this, ManageSelectActivity.class);
                startActivity(i);
                break;
            case R.id.exit:
                System.exit(0);
                break;
            default:
                break;
        }
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

}
