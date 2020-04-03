package com.beaconproject.main.UI.Manage;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beaconproject.main.Adapter.AddAdminAdapter;
import com.beaconproject.main.Adapter.ManageEditStatusAdapter;
import com.beaconproject.main.Adapter.ManageSelectPeopleAdapter;
import com.beaconproject.main.Common.EmptyRecyclerView;
import com.beaconproject.main.Common.PeopleData;
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

public class ManageAddAdmin extends AppCompatActivity implements View.OnTouchListener{

    private SharedPreferences sharedPreferences;
    private GlobalVariable gv;
    private View decorView;

    private EditText searchAdmin;
    private ImageView addBtn;

    private List<PeopleData> mAdminData;
    private AddAdminAdapter myAdapter;
    private EmptyRecyclerView myrv;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_add_admin);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        gv = (GlobalVariable)getApplicationContext();
        sharedPreferences = getSharedPreferences("BeaconProjectData",MODE_PRIVATE);

        /// RecyclerView使用的Adapter以及串列 ///
        mAdminData = new ArrayList<PeopleData>();
        myrv = (EmptyRecyclerView) findViewById(R.id.adminPeopleView);
        mEmptyView = findViewById(R.id.adminPeopleView);
        myAdapter = new AddAdminAdapter(this,mAdminData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myrv.setLayoutManager(layoutManager);
        myrv.setItemAnimator(new DefaultItemAnimator());
        myrv.setAdapter(myAdapter);
        myrv.setEmptyView(mEmptyView);
        /// RecyclerView使用的Adapter以及串列 ///

        searchAdmin = (EditText)findViewById(R.id.Admin_search);
        addBtn = (ImageView)findViewById(R.id.AdminAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
                sqlc.Connection();
                if(sqlc.connectionSuccess){
                    String currUsercode = sharedPreferences.getString("usercode",null);
                    String activityId = sharedPreferences.getString("currActivityId",null);
                    if(activityId!=null){
                        if(!searchAdmin.getText().toString().trim().equals(currUsercode)){
                            String stmt = String.format("SELECT * FROM `user` WHERE `usercode` = '%s'", searchAdmin.getText().toString().trim());
                            ResultSet rs = sqlc.executeSQL(stmt);
                            int count = 0;
                            try{
                                rs.last();
                                count = rs.getRow();
                                if(count>0){
                                    String usercode = rs.getString("usercode");
                                    String userId = rs.getString("userId");
                                    String repeatCheck = String.format("SELECT * FROM `activitypermission` WHERE `activityId`='%s' AND `userId`='%s';",activityId, userId);
                                    rs = sqlc.executeSQL(repeatCheck);
                                    int repeatCount = 0;
                                    rs.last();
                                    repeatCount = rs.getRow();
                                    if(repeatCount==0){
                                        String addtoAdmin = String.format("INSERT INTO `activitypermission`(`activityId`, `userId`, `EditPermission`, `DeletePermission`) VALUES ('%s','%s','1','0');",activityId, userId);
                                        sqlc.executeSQL(addtoAdmin);
                                        getList();
                                    }
                                    else{
                                        Toast.makeText(ManageAddAdmin.this, "請不要重複新增!", Toast.LENGTH_SHORT).show();
                                        getList();
                                    }
                                }
                                else
                                    Toast.makeText(ManageAddAdmin.this, "無此ID的使用者", Toast.LENGTH_SHORT).show();
                            }
                            catch(SQLException e){
                                Toast.makeText(ManageAddAdmin.this, "SQL錯誤", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(ManageAddAdmin.this,"不可以搜尋自己!",Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(ManageAddAdmin.this,"請重啟應用!",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(ManageAddAdmin.this,"無法取得清單。",Toast.LENGTH_LONG).show();
                    finish();

                }

            }
        });
        getList();
    }

    void getList(){
        mAdminData.clear();
        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
        sqlc.Connection();
        if(sqlc.connectionSuccess){
            String currUserId = sharedPreferences.getString("userId",null);
            String activityId = sharedPreferences.getString("currActivityId",null);
            if(activityId!=null && currUserId !=null){
                String stmt = String.format("SELECT * FROM `activitypermission` WHERE `activityId` = '%s'", activityId);
                ResultSet rs = sqlc.executeSQL(stmt);
                try{
                    List<PermissionData> permissionData =new ArrayList<>();
                    while(rs.next()){
                        String userId = rs.getString("userId");
                        String EditPermission = rs.getString("EditPermission");
                        String DeletePermission = rs.getString("DeletePermission");
                        permissionData.add(new PermissionData(
                            userId,activityId,EditPermission,DeletePermission
                        ));
                    }
                    for(int i=0;i<permissionData.size();i++){
                        if(!permissionData.get(i).getUserId().equals(currUserId)){
                            String getUser = String.format("SELECT * FROM `user` WHERE `userId`='%s';", permissionData.get(i).getUserId());
                            rs=sqlc.executeSQL(getUser);
                            while(rs.next()){
                                String usercode = rs.getString("usercode");
                                String userId = rs.getString("userId");
                                PeopleData peopleData = new PeopleData();
                                peopleData.setNotice(activityId);
                                peopleData.setUserID(userId);
                                peopleData.setName(usercode);
                                mAdminData.add(peopleData);
                            }
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }
                catch(SQLException e){
                    Toast.makeText(this, "SQL錯誤", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(this,"請重啟應用!",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this,"無法取得清單。",Toast.LENGTH_LONG).show();
            finish();

        }
    }
    public void previousStep(View view) {
        finish();
    }

    @Override
    protected void onStart() {
        init();
        super.onStart();
    }

    private void init() {
        int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (Build.VERSION.SDK_INT < 19 || !checkDeviceHasNavigationBar()) {
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
