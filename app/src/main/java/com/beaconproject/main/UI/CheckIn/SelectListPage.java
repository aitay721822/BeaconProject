package com.beaconproject.main.UI.CheckIn;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import com.beaconproject.main.Adapter.SelectCheckInAdapter;
import com.beaconproject.main.Common.EmptyRecyclerView;
import com.beaconproject.main.Common.PeopleData;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.Global.GlobalVariable;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectListPage extends AppCompatActivity implements View.OnTouchListener {

    private View decorView;
    private SQLConnection sqlc;
    private List<PeopleData> mPeopleData;
    private EmptyRecyclerView myrv;
    private SelectCheckInAdapter myAdapter;
    private View mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_list_page);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// SQLConnection ///
        GlobalVariable sqlData = (GlobalVariable)getApplicationContext();
        sqlc = new SQLConnection(sqlData.sqlIP,sqlData.sqlport,sqlData.sqldbname,sqlData.sqluser,sqlData.sqlpass);
        sqlc.Connection();
        if(!sqlc.connectionSuccess){finish();}
        /// SQLConnection ///

        ///取得活動參加者清單///
        mPeopleData = new ArrayList<PeopleData>();
        myrv = (EmptyRecyclerView) findViewById(R.id.attendview);
        mEmptyView = findViewById(R.id.attendview);
        myAdapter = new SelectCheckInAdapter(this,mPeopleData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myrv.setLayoutManager(layoutManager);
        myrv.setItemAnimator(new DefaultItemAnimator());
        myrv.setAdapter(myAdapter);
        myrv.setEmptyView(mEmptyView);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        String sqlStmt = String.format("SELECT * FROM `record` WHERE `activatyId` = '%s'  AND `isSignIn` = '0';",bundle.getString("activityId"));
        ResultSet rs = sqlc.executeSQL(sqlStmt);
        try{
            while(rs.next()){
                String username = rs.getString("username"),notice = rs.getString("notice");
                mPeopleData.add(new PeopleData(username,notice));
            }
            myAdapter.notifyDataSetChanged();
        }catch(SQLException ex){
            Toast.makeText(this,"取得活動參加者清單錯誤",Toast.LENGTH_SHORT);
            ex.printStackTrace();
        }
        ///取得活動參加者清單///

        /// 搜尋框的實現 ///
        final EditText searchText = (EditText)findViewById(R.id.search_list);
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
    }

    void filter(String res){
        List<PeopleData> filterList = new ArrayList<PeopleData>();
        for(PeopleData i : mPeopleData){
            if(i.getName().toLowerCase().contains(res.toLowerCase())){
                filterList.add(i);
            }
        }
        myAdapter.filterList(filterList);
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
    public void previousStep(View view){
        finish();
    }
}
