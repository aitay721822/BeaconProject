package com.beaconproject.main.UI.CheckIn;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.THLight.USBeacon.App.Lib.BatteryPowerData;
import com.THLight.USBeacon.App.Lib.USBeaconConnection;
import com.THLight.USBeacon.App.Lib.iBeaconData;
import com.THLight.USBeacon.App.Lib.iBeaconScanManager;

import com.beaconproject.main.Adapter.SelectActivityAdapter;
import com.beaconproject.main.Common.ActivityData;
import com.beaconproject.main.Common.EmptyRecyclerView;
import com.beaconproject.main.Common.ScanediBeacon;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.Global.GlobalVariable;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckInFirstStep extends AppCompatActivity implements View.OnTouchListener,iBeaconScanManager.OniBeaconScan, USBeaconConnection.OnResponse {

    private View decorView;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    final int REQ_ENABLE_BT		= 2000;
    final int MSG_SCAN_IBEACON			= 1000;
    final int MSG_UPDATE_BEACON_LIST	= 1001;
    final int TIME_BEACON_TIMEOUT		= 30000;
    BluetoothAdapter mBLEAdapter= BluetoothAdapter.getDefaultAdapter();
    iBeaconScanManager miScaner	= null;
    List<ScanediBeacon> miBeacons	= new ArrayList<ScanediBeacon>();
    List<ActivityData> mActivityData;
    private EmptyRecyclerView myrv;
    private SelectActivityAdapter myAdapter;
    private View mEmptyView;
    private SQLConnection sqlc;


    public void previousStep(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_first_step);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// 設定RecyclerView ///
        mActivityData = new ArrayList<ActivityData>();
        myrv = (EmptyRecyclerView) findViewById(R.id.activitylistview);
        mEmptyView = findViewById(R.id.activitylistview);
        myAdapter = new SelectActivityAdapter(this,mActivityData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myrv.setLayoutManager(layoutManager);
        myrv.setItemAnimator(new DefaultItemAnimator());
        myrv.setAdapter(myAdapter);
        myrv.setEmptyView(mEmptyView);
        /// 設定RecyclerView ///


        miScaner = new iBeaconScanManager(this,this);

        //Check the BT is on or off on the phone.
        if(!mBLEAdapter.isEnabled())
        {
            Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQ_ENABLE_BT);   // A request for open Bluetooth
        }
        else
        {
            Message msg= Message.obtain(mHandler, MSG_SCAN_IBEACON, 2000, 3000);
            msg.sendToTarget();
        }


        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_BEACON_LIST, 1000);

        /// SQLConnection ///
        GlobalVariable sqlData = (GlobalVariable)getApplicationContext();
        sqlc = new SQLConnection(sqlData.sqlIP,sqlData.sqlport,sqlData.sqldbname,sqlData.sqluser,sqlData.sqlpass);
        sqlc.Connection();
        if(!sqlc.connectionSuccess){finish();}
        /// SQLConnection ///
        checkPermission();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case MSG_SCAN_IBEACON:
                {
                    int timeForScaning = msg.arg1;
                    int nextTimeStartScan = msg.arg2;
                    miScaner.startScaniBeacon(timeForScaning); //開始掃描Beacon
                    this.sendMessageDelayed(Message.obtain(msg),nextTimeStartScan);
                }
                break;
                case MSG_UPDATE_BEACON_LIST:
                    synchronized(myAdapter)
                    {
                        getActivity();
                        myAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_BEACON_LIST, 1000);
                    }
                    break;
            }
        }
    };

    public void onResponse(int msg) {
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onScaned(iBeaconData iBeacon)
    {
        synchronized(myAdapter)
        {
            addOrUpdateiBeacon(iBeacon);
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onBatteryPowerScaned(BatteryPowerData batteryPowerData) {
        Log.d("debug", batteryPowerData.batteryPower+"");
        for(int i = 0 ; i < miBeacons.size() ; i++)
        {
            if(miBeacons.get(i).macAddress.equals(batteryPowerData.macAddress))
            {
                ScanediBeacon ib = miBeacons.get(i);
                ib.batteryPower = batteryPowerData.batteryPower;
                miBeacons.set(i, ib);
            }
        }
    }

    public void addOrUpdateiBeacon(iBeaconData iBeacon)
    {
        long currTime= System.currentTimeMillis();
        ScanediBeacon beacon= null;
        for(ScanediBeacon b : miBeacons)
        {
            if(b.equals(iBeacon, false))
            {
                beacon= b;
                break;
            }
        }

        if(beacon==null)
        {
            beacon= ScanediBeacon.copyOf(iBeacon);
            miBeacons.add(beacon);
        }
        else
        {
            beacon.rssi= iBeacon.rssi;
        }
        beacon.lastUpdate= currTime;
    }

    public void getActivity()
    {
        {
            long currTime	= System.currentTimeMillis();
            int len= miBeacons.size();
            ScanediBeacon beacon= null;
            for(int i= len- 1; 0 <= i; i--)
            {
                beacon= miBeacons.get(i);
                if(null != beacon && TIME_BEACON_TIMEOUT < (currTime- beacon.lastUpdate))
                {
                    miBeacons.remove(i);
                }
            }
        }

        {
            myAdapter.clear();
            //Add beacon to the list that it could show on the screen.
            for(ScanediBeacon beacon : miBeacons)
            {
                String SearchBeaconData = String.format("SELECT * FROM `activaty` WHERE `beaconUUID` = '%s'",beacon.beaconUuid);
                ResultSet resultActivity = sqlc.executeSQL(SearchBeaconData);
                try{
                    //如果result有資料的話
                    while(resultActivity.next()){
                        /// initialize Data Set ///
                        String activityID = resultActivity.getString("activatyId");
                        String activityName = resultActivity.getString("activatyName");
                        String activityLocation = resultActivity.getString("activatyLocation");
                        String activityStartDate = resultActivity.getString("activatyStartDate");
                        String activityEndDate = resultActivity.getString("activatyEndDate");
                        String activityNote = resultActivity .getString("activatyNote");
                        String activatyCreationDate = resultActivity.getString("activatyCreationDate");
                        /// initialize Data Set ///
                        ActivityData data = new ActivityData(activityID,activityName,activityLocation,activityNote,activatyCreationDate,activityStartDate,activityEndDate,beacon.beaconUuid);
                        if(data.validCheckInTime())
                            mActivityData.add(data);
                    }
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    public void cleariBeacons()
    {
        myAdapter.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("debug", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                break;
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(" 授權位置信息");
                builder.setMessage("請授權位置信息以便掃描Beacon。");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
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
