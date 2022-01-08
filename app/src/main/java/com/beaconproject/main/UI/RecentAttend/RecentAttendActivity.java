package com.beaconproject.main.UI.RecentAttend;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconproject.main.Adapter.RecyclerViewPeopleAdapter;
import com.beaconproject.main.Adapter.ShowActivityCardView;
import com.beaconproject.main.Common.EmptyRecyclerView;
import com.beaconproject.main.Common.PeopleData;
import com.beaconproject.main.Common.RecordData;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class RecentAttendActivity extends AppCompatActivity implements View.OnTouchListener {

    private View decorView;
    private List<RecordData> mRecordData;
    private EmptyRecyclerView myrv;
    private ShowActivityCardView myAdapter;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_attend);
        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// RecyclerView使用的Adapter以及串列 ///
        mRecordData = new ArrayList<RecordData>();
        myrv = (EmptyRecyclerView) findViewById(R.id.showRecent);
        mEmptyView = findViewById(R.id.showRecent);
        myAdapter = new ShowActivityCardView(this,mRecordData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myrv.setLayoutManager(layoutManager);
        myrv.setItemAnimator(new DefaultItemAnimator());
        myrv.setAdapter(myAdapter);
        myrv.setEmptyView(mEmptyView);
        /// RecyclerView使用的Adapter以及串列 ///

        /// 取得紀錄並顯示 ///
        getRecord();
        /// 取得紀錄並顯示 ///

    }

    void getRecord(){
        final SharedPreferences sharedPreferences = getSharedPreferences("BeaconProjectData", MODE_PRIVATE);
        GlobalVariable gv = (GlobalVariable)getApplicationContext();

        ///取得參加過的活動及紀錄///
        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
        sqlc.Connection();
        if(sqlc.connectionSuccess){
            String UserId = sharedPreferences.getString("userId",null);
            if(UserId!=null){
                String stmt = String.format("SELECT * FROM `record` WHERE `userId` = '%s' AND `isSignIn` = '1'",UserId);
                ResultSet rs = sqlc.executeSQL(stmt);
                try {
                    while(rs.next()){
                        String activityId = rs.getString("activatyId");
                        String isSignIn = rs.getString("isSignIn");
                        String userId = rs.getString("userId");
                        String username = rs.getString("username");
                        String attendDateTime = rs.getString("attendDateTime");
                        String notice = rs.getString("notice");

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            format.setTimeZone(TimeZone.getTimeZone("GMT"));
                            Date ParseDate = format.parse(attendDateTime);
                            format.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
                            attendDateTime = format.format(ParseDate);
                        } catch (ParseException e) {
                            Toast.makeText(this,"日期取得錯誤",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        mRecordData.add(new RecordData(
                            activityId,userId,isSignIn,username,attendDateTime,notice
                        ));
                    }
                    myAdapter.notifyDataSetChanged();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(this,"無法取得userId",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this,"SQL連線失敗",Toast.LENGTH_SHORT).show();
        }
    }

    public void previousStep(View view){
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

}
