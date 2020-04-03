package com.beaconproject.main.UI.Manage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.UI.CheckIn.CheckInFirstStep;
import com.beaconproject.main.UI.Create.AttendFirstStep;
import com.beaconproject.main.UI.Create.AttendList;
import com.beaconproject.main.UI.HomePage;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageHomePage extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private SharedPreferences sharedPreferences;
    private GlobalVariable gv;
    private TextView showActivityName;
    private CardView editActivityContent,editCheckInStatus,editPersonList,intoHomePage,deleteActivity,adminActivity;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// definded CardView ///
        editActivityContent = (CardView)findViewById(R.id.editActivityContent);
        editCheckInStatus = (CardView)findViewById(R.id.editCheckStatus);
        editPersonList = (CardView)findViewById(R.id.editAttPersonList);
        intoHomePage = (CardView)findViewById(R.id.intoHomePage);
        deleteActivity = (CardView)findViewById(R.id.deleteActivity);
        adminActivity = (CardView)findViewById(R.id.manage_addAdmin) ;
        /// definded CardView ///

        /// set OnClick Listener CardView ///
        editActivityContent.setOnClickListener(this);
        editCheckInStatus.setOnClickListener(this);
        editPersonList.setOnClickListener(this);
        intoHomePage.setOnClickListener(this);
        deleteActivity.setOnClickListener(this);
        adminActivity.setOnClickListener(this);
        /// set OnClick Listener CardView ///

        /// 取得廣域變數 ///
        gv = (GlobalVariable)getApplicationContext();
        /// 取得廣域變數 ///

        ///取得活動名稱///
        sharedPreferences = getSharedPreferences("BeaconProjectData",MODE_PRIVATE);
        String activityName = sharedPreferences.getString("currActivityName",null);
        showActivityName = (TextView)findViewById(R.id.activityShowName);
        if(activityName!=null){
            showActivityName.setText(activityName);
        }
        else {
            Toast.makeText(this,"請重選活動!",Toast.LENGTH_SHORT).show();
            finish();
        }
        ///取得活動名稱///
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()){
            case R.id.editActivityContent:
                i = new Intent();
                i.setClass(ManageHomePage.this, ManageEditActivityInfo.class);
                startActivity(i);
                break;
            case R.id.editAttPersonList:
                i = new Intent();
                i.setClass(ManageHomePage.this, ManageEditPeopleInfo.class);
                startActivity(i);
                break;
            case R.id.editCheckStatus:
                i = new Intent();
                i.setClass(ManageHomePage.this, ManageEditCheckInStatus.class);
                startActivity(i);
                break;
            case R.id.deleteActivity:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("您確定要刪除此活動?");
                builder.setMessage("刪除活動將會連活動資訊、參加者資訊一併刪除");
                builder.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
                        sqlc.Connection();
                        if(sqlc.connectionSuccess){
                            String userId = sharedPreferences.getString("userId",null);
                            String ActivityId = sharedPreferences.getString("currActivityId",null);
                            if(userId != null){
                                boolean check = false;
                                String permissionCheck = String.format("SELECT * FROM `activitypermission` WHERE `activityId` = '%s' AND `userId`= '%s'",ActivityId,userId);
                                ResultSet rs = sqlc.executeSQL(permissionCheck);
                                try {
                                    while(rs.next()){
                                        if(rs.getString("DeletePermission").equals("1")) check = true;
                                        else check=false;
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                if(check){
                                    String DeletePermission = String.format("DELETE FROM `activitypermission` WHERE `activityId` = '%s'",ActivityId);
                                    String DeleteActivity = String.format("DELETE FROM `activaty` WHERE `activatyId` = '%s'",ActivityId);
                                    String DeleteRecord = String.format("DELETE FROM `record` WHERE `activatyId` = '%s'",ActivityId);
                                    sqlc.executeSQL(DeleteActivity);
                                    sqlc.executeSQL(DeletePermission);
                                    sqlc.executeSQL(DeleteRecord);
                                    sqlc.close();
                                    Toast.makeText(ManageHomePage.this,"活動刪除成功!",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(ManageHomePage.this,"您尚無刪除權限。",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(ManageHomePage.this,"無法取得使用者Id! 請重啟應用!",Toast.LENGTH_SHORT).show();

                            }
                            Intent goBackHomePage = new Intent();
                            goBackHomePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            goBackHomePage.setClass(ManageHomePage.this, HomePage.class);
                            startActivity(goBackHomePage);
                        }
                        else {
                            Toast.makeText(ManageHomePage.this,"無法連接SQL!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNeutralButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }
                });
                builder.show();
                break;
            case R.id.manage_addAdmin:
                SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
                sqlc.Connection();
                if(sqlc.connectionSuccess){
                    String userId = sharedPreferences.getString("userId",null);
                    String ActivityId = sharedPreferences.getString("currActivityId",null);
                    if(userId != null){
                        boolean check = false;
                        String permissionCheck = String.format("SELECT * FROM `activitypermission` WHERE `activityId` = '%s' AND `userId`= '%s'",ActivityId,userId);
                        ResultSet rs = sqlc.executeSQL(permissionCheck);
                        try {
                            while(rs.next()){
                                if(rs.getString("DeletePermission").equals("1")) check = true;
                                else check=false;
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        if(check){
                            i = new Intent();
                            i.setClass(ManageHomePage.this, ManageAddAdmin.class);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(ManageHomePage.this,"您尚無權限。",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(ManageHomePage.this,"無法取得使用者Id! 請重啟應用!",Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(ManageHomePage.this,"無法連接SQL!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.intoHomePage:
                finish();
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
