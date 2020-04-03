package com.beaconproject.main.UI.Manage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.beaconproject.main.Adapter.ManageSelectAdapter;
import com.beaconproject.main.Adapter.ShowActivityCardView;
import com.beaconproject.main.Common.ActivityData;
import com.beaconproject.main.Common.EmptyRecyclerView;
import com.beaconproject.main.Common.PermissionData;
import com.beaconproject.main.Common.RecordData;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ManageSelectActivity extends AppCompatActivity implements View.OnTouchListener {

    private View decorView;

    private List<ActivityData> mActivityData;
    private EmptyRecyclerView myrv;
    private ManageSelectAdapter myAdapter;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_select);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// RecyclerView使用的Adapter以及串列 ///
        mActivityData = new ArrayList<ActivityData>();
        myrv = (EmptyRecyclerView) findViewById(R.id.select_activity_rv);
        mEmptyView = findViewById(R.id.select_activity_rv);
        myAdapter = new ManageSelectAdapter(this,mActivityData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myrv.setLayoutManager(layoutManager);
        myrv.setItemAnimator(new DefaultItemAnimator());
        myrv.setAdapter(myAdapter);
        myrv.setEmptyView(mEmptyView);
        /// RecyclerView使用的Adapter以及串列 ///

        ///取得活動資訊///
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
        sqlc.Connection();
        if(sqlc.connectionSuccess){
            SharedPreferences sharedPreferences = getSharedPreferences("BeaconProjectData",MODE_PRIVATE);
            String UserId = sharedPreferences.getString("userId",null);
            if(UserId!=null){
                try{
                    List<PermissionData> permissionData = new ArrayList<PermissionData>();
                    String stmt = String.format("SELECT * FROM `activitypermission` WHERE `userId`='%s'",UserId);
                    ResultSet rs = sqlc.executeSQL(stmt);
                    while(rs.next()) {
                        String activityId = rs.getString("activityId");
                        String EditPermission = rs.getString("EditPermission");
                        String DeletePermission = rs.getString("DeletePermission");
                        permissionData.add(new PermissionData(UserId,activityId,EditPermission,DeletePermission));
                    }

                    for(int i=0;i<permissionData.size();i++){
                        if(permissionData.get(i).getEditPermission().equals("1") || permissionData.get(i).getDeletePermission().equals("1")){
                            stmt = String.format("SELECT * FROM `activaty` WHERE `activatyId` = '%s'",permissionData.get(i).getActivityId());
                            rs = sqlc.executeSQL(stmt);
                            while(rs.next()){
                                String activityName = rs.getString("activatyName");
                                String activatyLocation = rs.getString("activatyLocation");
                                String activatyStartDate = rs.getString("activatyStartDate");
                                String activatyEndDate = rs.getString("activatyEndDate");
                                String activatyNote = rs.getString("activatyNote");
                                String activatyCreationDate = rs.getString("activatyCreationDate");
                                String beaconUUID = rs.getString("beaconUUID");

                                mActivityData.add(new ActivityData(
                                        permissionData.get(i).getActivityId(),activityName,activatyLocation,activatyNote,activatyCreationDate,activatyStartDate,activatyEndDate,beaconUUID
                                ));
                            }
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                    sqlc.close();
                }
                catch(SQLException ex){
                    Toast.makeText(this,"無法連接SQL",Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }
            else{
                Toast.makeText(this,"無法存取使用者名稱，請重啟應用。",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this,"無法連接SQL",Toast.LENGTH_SHORT).show();
        }

        ///取得活動資訊///

    }

    public void previousStep(View view){finish();}
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
