package com.beaconproject.main.UI.Manage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.UI.Create.AttendFirstStep;
import com.beaconproject.main.UI.Create.AttendList;
import com.beaconproject.main.UI.HomePage;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ManageEditActivityInfo extends AppCompatActivity implements View.OnTouchListener {

    private SharedPreferences sharedPreferences;
    private GlobalVariable gv;
    private EditText ActivatyName;
    private EditText ActivatyLocation;
    private EditText Note;
    private EditText stDate;
    private EditText stTime;
    private EditText endDate;
    private EditText endTime;

    private View decorView;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    public void previousStep(View view) {finish();}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_edit_info);

        gv = (GlobalVariable)getApplicationContext();

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// init EditText ///
        stDate = ((EditText) findViewById(R.id.editStartDate));
        stTime = ((EditText) findViewById(R.id.editStartTime));
        endDate = ((EditText) findViewById(R.id.editEndDate));
        endTime = ((EditText) findViewById(R.id.editEndTime));
        ActivatyName = ((EditText) findViewById(R.id.editActivityName));
        ActivatyLocation = ((EditText) findViewById(R.id.editActivityLocation));
        Note = ((EditText) findViewById(R.id.editNote));
        /// init EditText ///

        /// set Variable  ///
        sharedPreferences = getSharedPreferences("BeaconProjectData",MODE_PRIVATE);
        String currActivityId = sharedPreferences.getString("currActivityId",null);
        String currActivityName = sharedPreferences.getString("currActivityName",null);
        String currActivityLocation = sharedPreferences.getString("currActivityLocation",null);
        String currActivityStartDate = sharedPreferences.getString("currActivityStartDate",null);
        String currActivityEndDate = sharedPreferences.getString("currActivityEndDate",null);
        String currActivityNote = sharedPreferences.getString("currActivityNote",null);

        if(currActivityId != null && currActivityName !=null && currActivityLocation != null && currActivityNote !=null && currActivityStartDate !=null && currActivityEndDate != null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ActivatyName.setText(currActivityName);
            ActivatyLocation.setText(currActivityLocation);
            Note.setText(currActivityNote);
            try {
                /// Parsing DateTime ///
                currActivityStartDate = format.format(format.parse(currActivityStartDate));
                currActivityEndDate = format.format(format.parse(currActivityEndDate));
                /// Parsing DateTime ///

                /// Spliting DateTime ///
                String currStartDate = currActivityStartDate.substring(0,currActivityStartDate.indexOf(" "));
                currStartDate = currStartDate.replace("-","/");
                String currStartTime = currActivityStartDate.substring(currActivityStartDate.indexOf(" ") + 1);
                /// Spliting DateTime ///

                /// Spliting DateTime ///
                String currEndDate = currActivityEndDate.substring(0,currActivityEndDate.indexOf(" "));
                currEndDate = currEndDate.replace("-","/");
                String currEndTime = currActivityEndDate.substring(currActivityEndDate.indexOf(" ") + 1);
                /// Spliting DateTime ///

                stDate.setText(currStartDate);
                stTime.setText(currStartTime);
                endDate.setText(currEndDate);
                endTime.setText(currEndTime);
            } catch (ParseException e) {
                Toast.makeText(this,"日期取得錯誤",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this,"無法取得活動資訊",Toast.LENGTH_SHORT).show();
            finish();
        }
        /// set Variable ///

        stDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ManageEditActivityInfo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        stDate.setText(String.format("%02d/%02d/%02d", year, month + 1, dayOfMonth));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        stTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR);
                final int minute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(ManageEditActivityInfo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        stTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ManageEditActivityInfo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate.setText(String.format("%02d/%02d/%02d", year, month + 1, dayOfMonth));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR);
                final int minute = calendar.get(Calendar.MINUTE);
                timePickerDialog = new TimePickerDialog(ManageEditActivityInfo.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

    }

    private boolean checkInputCorrect() {
        if (ActivatyName.getText().toString().trim() != "" &&
                ActivatyLocation.getText().toString().trim() != "" &&
                stDate.getText().toString().trim() != "" &&
                stTime.getText().toString().trim() != "" &&
                endDate.getText().toString().trim() != "" &&
                endTime.getText().toString().trim() != "") {

            SimpleDateFormat DateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date startDateTime = null, endDateTime = null;
            try {
                startDateTime = DateTimeFormat.parse(String.format("%s %s", stDate.getText().toString().trim(), stTime.getText().toString().trim()));
                endDateTime = DateTimeFormat.parse(String.format("%s %s", endDate.getText().toString().trim(), endTime.getText().toString().trim()));

                if (startDateTime.after(endDateTime)) {
                    stDate.setError("必須小於結束時間");
                    endDate.setError("必須小於結束時間");
                    stTime.setError("必須小於結束時間");
                    endTime.setError("必須小於結束時間");
                    Toast.makeText(this, "必須小於結束時間!", Toast.LENGTH_SHORT).show();
                } else return true;
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            return false;
        } else {
            Toast.makeText(this, "有必填欄位尚未填滿", Toast.LENGTH_SHORT).show();
            return false;
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

    private void UPLOAD(SQLConnection sqlc,String ActivityName,String ActivityLocation,String ActivityStartDate,String ActivityEndDate,String ActivityNote){
        if(sqlc.connectionSuccess){
            /// 上傳活動資訊 ///
            String activityId = sharedPreferences.getString("currActivityId",null);
            if(activityId!=null){
                String ActivityInfo =
                        String.format("UPDATE `activaty` SET `activatyName`='%s',`activatyLocation`='%s',`activatyStartDate`='%s',`activatyEndDate`='%s',`activatyNote`='%s' WHERE `activatyId` = '%s'",
                                ActivityName,ActivityLocation,ActivityStartDate,ActivityEndDate,ActivityNote,activityId);
                sqlc.executeSQL(ActivityInfo);
                Toast.makeText(this, "活動資訊已修改!", Toast.LENGTH_LONG).show();

                ActivityStartDate = ActivityStartDate.replace('/','-');
                ActivityEndDate = ActivityEndDate.replace('/','-');

                sharedPreferences.edit().putString("currActivityName",ActivityName).apply();
                sharedPreferences.edit().putString("currActivityLocation",ActivityLocation).apply();
                sharedPreferences.edit().putString("currActivityStartDate",ActivityStartDate).apply();
                sharedPreferences.edit().putString("currActivityEndDate",ActivityEndDate).apply();
                sharedPreferences.edit().putString("currActivityNote",ActivityNote).apply();

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(ManageEditActivityInfo.this, ManageHomePage.class);
                startActivity(intent);
                finish();
                sqlc.close();
            }
            else {
                Toast.makeText(this, "無法取得活動Id。請重選活動!", Toast.LENGTH_LONG).show();
            }
            /// 上傳活動資訊 ///
        }
        else{
            Toast.makeText(this,"SQL無法連線",Toast.LENGTH_SHORT).show();
        }
    }

    public void complete(View view) {
        if(checkInputCorrect()){
            String ActivityName = ActivatyName.getText().toString().trim();
            String ActivityLocation = ActivatyLocation.getText().toString().trim();
            String ActivityStartDate = String.format("%s %s",stDate.getText().toString().trim(),stTime.getText().toString().trim());
            String ActivityEndDate = String.format("%s %s",endDate.getText().toString().trim(),endTime.getText().toString().trim());
            String ActivityNote = Note.getText().toString().trim();
            SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
            sqlc.Connection();
            if(sqlc.connectionSuccess){
                String currActivityName = sharedPreferences.getString("currActivityName",null);
                if(currActivityName!=null && currActivityName.equals(ActivityName)) {
                    UPLOAD(sqlc, ActivityName, ActivityLocation, ActivityStartDate, ActivityEndDate, ActivityNote);
                }
                else{
                    String sqlStmt = String.format("SELECT * FROM `activaty` WHERE `activatyName`=\"%s\"",ActivityName);
                    ResultSet rs = sqlc.executeSQL(sqlStmt);
                    int count = 0;
                    try {
                        if (rs != null) {
                            rs.last();
                            count = rs.getRow();
                            if (count > 0) {
                                Toast.makeText(this, "真可惜，活動名稱已經被搶走了", Toast.LENGTH_LONG).show();
                            } else {
                                UPLOAD(sqlc,ActivityName,ActivityLocation,ActivityStartDate,ActivityEndDate,ActivityNote);
                            }
                        }
                    }catch(SQLException ex){
                        Toast.makeText(this,"SQL錯誤",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                Toast.makeText(this,"SQL無法連線",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
