package com.beaconproject.main.UI.Create;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.Global.GlobalVariable;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AttendFirstStep extends AppCompatActivity implements View.OnTouchListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_first_step);
        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        stDate = ((EditText) findViewById(R.id.startDate));
        stTime = ((EditText) findViewById(R.id.startTime));
        endDate = ((EditText) findViewById(R.id.endDate));
        endTime = ((EditText) findViewById(R.id.endTime));
        ActivatyName = ((EditText) findViewById(R.id.activatyName));
        ActivatyLocation = ((EditText) findViewById(R.id.location));
        Note = ((EditText) findViewById(R.id.note));

        stDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AttendFirstStep.this, new DatePickerDialog.OnDateSetListener() {
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
                timePickerDialog = new TimePickerDialog(AttendFirstStep.this, new TimePickerDialog.OnTimeSetListener() {
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
                datePickerDialog = new DatePickerDialog(AttendFirstStep.this, new DatePickerDialog.OnDateSetListener() {
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
                timePickerDialog = new TimePickerDialog(AttendFirstStep.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }

    public void previousStep(View view) {
        finish();
    }

    public void nextStep(View view) {
        if (checkInputCorrect()) {

            GlobalVariable sqlInfo = (GlobalVariable) getApplicationContext();
            SQLConnection sqlc = new SQLConnection(sqlInfo.sqlIP, sqlInfo.sqlport, sqlInfo.sqldbname, sqlInfo.sqluser, sqlInfo.sqlpass);
            sqlc.Connection();
            if (sqlc.connectionSuccess) {
                String sqlStmt = String.format("SELECT * FROM `activaty` WHERE `activatyName`=\"%s\"", ActivatyName.getText().toString().trim());
                ResultSet rs = sqlc.executeSQL(sqlStmt);
                int count = 0;
                try {
                    if (rs != null) {
                        rs.last();
                        count = rs.getRow();
                        if (count > 0) {
                            Toast.makeText(AttendFirstStep.this, "真可惜，活動名稱已經被搶走了", Toast.LENGTH_LONG).show();
                        } else {
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent();
                            bundle.putString("ActivatyName", ActivatyName.getText().toString().trim());
                            bundle.putString("ActivatyLocation", ActivatyLocation.getText().toString().trim());
                            bundle.putString("startDate", String.format("%s %s", stDate.getText().toString().trim(), stTime.getText().toString().trim()));
                            bundle.putString("endDate", String.format("%s %s", endDate.getText().toString().trim(), endTime.getText().toString().trim()));
                            bundle.putString("Note", Note.getText().toString().trim());
                            intent.setClass(AttendFirstStep.this, SelectPage.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            sqlc.close();
                        }
                    }
                } catch (SQLException e) {
                    Toast.makeText(AttendFirstStep.this, "無法連線至SQL", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkInputCorrect() {
        if (ActivatyName.getText().toString().trim() != "" &&
                ActivatyLocation.getText().toString().trim() != "" &&
                stDate.getText().toString().trim() != "" &&
                stTime.getText().toString().trim() != "" &&
                endDate.getText().toString().trim() != "" &&
                endTime.getText().toString().trim() != "") {

            SimpleDateFormat DateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date startDateTime = null, endDateTime = new Date(), curr = Calendar.getInstance().getTime();
            try {
                startDateTime = DateTimeFormat.parse(String.format("%s %s", stDate.getText().toString().trim(), stTime.getText().toString().trim()));
                endDateTime = DateTimeFormat.parse(String.format("%s %s", endDate.getText().toString().trim(), endTime.getText().toString().trim()));

                if (startDateTime.before(curr) || endDateTime.before(curr) || startDateTime.after(endDateTime)) {
                    stDate.setError("必須小於目前時間與結束時間");
                    endDate.setError("必須小於目前時間與結束時間");
                    stTime.setError("必須小於目前時間與結束時間");
                    endTime.setError("必須小於目前時間與結束時間");
                    Toast.makeText(this, "必須小於目前時間與結束時間!", Toast.LENGTH_SHORT).show();
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
}
