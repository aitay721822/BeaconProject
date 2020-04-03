package com.beaconproject.main.UI.Manage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beaconproject.main.Adapter.ManageEditStatusAdapter;
import com.beaconproject.main.Adapter.ManageSelectPeopleAdapter;
import com.beaconproject.main.Common.EmptyRecyclerView;
import com.beaconproject.main.Common.PeopleData;
import com.beaconproject.main.Common.RecordData;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageEditCheckInStatus extends AppCompatActivity implements View.OnTouchListener {

    private SharedPreferences sharedPreferences;
    private GlobalVariable gv;
    private View decorView;

    private Button completeBtn;

    private List<RecordData> mRecordData;
    private ManageEditStatusAdapter myAdapter;
    private EmptyRecyclerView myrv;
    private View mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_edit_check_in_status);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// RecyclerView使用的Adapter以及串列 ///
        mRecordData = new ArrayList<RecordData>();
        myrv = (EmptyRecyclerView) findViewById(R.id.StatusView);
        mEmptyView = findViewById(R.id.StatusView);
        myAdapter = new ManageEditStatusAdapter(this,mRecordData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myrv.setLayoutManager(layoutManager);
        myrv.setItemAnimator(new DefaultItemAnimator());
        myrv.setAdapter(myAdapter);
        myrv.setEmptyView(mEmptyView);
        /// RecyclerView使用的Adapter以及串列 ///

        /// 搜尋框的實現 ///
        final EditText searchText = (EditText)findViewById(R.id.editStatusSearch);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        /// 搜尋框的實現 ///

        gv = (GlobalVariable)getApplicationContext();
        sharedPreferences = getSharedPreferences("BeaconProjectData",MODE_PRIVATE);

        completeBtn = (Button)findViewById(R.id.ActivatyUpload);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
                sqlc.Connection();
                if(sqlc.connectionSuccess){
                    String userId = sharedPreferences.getString("userId",null);
                    if(userId!=null){
                        for(int i=0;i<mRecordData.size();i++){
                            String activityId = mRecordData.get(i).getActivityId();
                            String isSignIn = mRecordData.get(i).getIsSignIn();
                            String notice = mRecordData.get(i).getNotice();
                            String userName = mRecordData.get(i).getUsername();
                            if(isSignIn.equals("1")){
                                if(mRecordData.get(i).getUserId()==null){
                                    String stmt = String.format("UPDATE `record` SET `userId`='%s',`isSignIn`='%s',`attendDateTime`=NOW() WHERE `activatyId`='%s' AND `notice`='%s' AND `username`='%s';",
                                            userId,isSignIn,activityId,notice,userName);
                                    sqlc.executeSQL(stmt);
                                }
                            }
                            else{
                                String stmt = String.format("UPDATE `record` SET `userId`=NULL,`isSignIn`='%s',`attendDateTime`=NULL WHERE `activatyId`='%s' AND `notice`='%s' AND `username`='%s';",
                                        isSignIn,activityId,notice,userName);
                                sqlc.executeSQL(stmt);
                            }
                        }
                    }
                    else{
                        Toast.makeText(ManageEditCheckInStatus.this,"無法取得UserId，請重啟應用。",Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setClass(ManageEditCheckInStatus.this, ManageHomePage.class);
                    startActivity(intent);
                    finish();
                    sqlc.close();
                }
                else{
                    Toast.makeText(ManageEditCheckInStatus.this,"無法連接SQL",Toast.LENGTH_SHORT).show();
                }
            }
        });

        getList();
    }

    public void previousStep(View view) {
        finish();
    }
    void filter(String res){
        myAdapter.getFilter().filter(res);
    }

    void getList(){
        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
        sqlc.Connection();
        if(sqlc.connectionSuccess){
            String activityId = sharedPreferences.getString("currActivityId",null);
            if(activityId!=null){
                String stmt = String.format("SELECT * FROM `record` WHERE `activatyId` = '%s'", activityId);
                ResultSet rs = sqlc.executeSQL(stmt);
                try{
                    while(rs.next()){
                        String userId = rs.getString("userId");
                        String activatyId = rs.getString("activatyId");
                        String isSignIn = rs.getString("isSignIn");
                        String attendDateTime = rs.getString("attendDateTime");
                        String username = rs.getString("username");
                        String notice = rs.getString("notice");
                        mRecordData.add(new RecordData(
                            activityId,userId,isSignIn,username,attendDateTime,notice
                        ));
                    }
                    myAdapter.notifyDataSetChanged();
                }
                catch(SQLException e){
                    Toast.makeText(this, "SQL錯誤", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(this,"無法取得活動ID，請重選活動",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this,"無法取得清單。",Toast.LENGTH_LONG).show();
            finish();

        }
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
