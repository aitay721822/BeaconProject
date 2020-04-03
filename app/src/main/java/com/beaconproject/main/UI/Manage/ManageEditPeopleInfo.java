package com.beaconproject.main.UI.Manage;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beaconproject.main.Adapter.ManageSelectPeopleAdapter;
import com.beaconproject.main.Adapter.RecyclerViewPeopleAdapter;
import com.beaconproject.main.Common.CSVFile;
import com.beaconproject.main.Common.EmptyRecyclerView;
import com.beaconproject.main.Common.PeopleData;
import com.beaconproject.main.Common.RecordData;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.UI.Create.AttendList;
import com.beaconproject.main.UI.HomePage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageEditPeopleInfo extends AppCompatActivity implements View.OnTouchListener{

    private SharedPreferences sharedPreferences;
    private GlobalVariable gv;
    private View decorView;

    private final int PERMISSION_REQUEST_EXT_STORAGE = 2;
    private final int REQUST_CODE = 100;

    private List<PeopleData> mPeopleData;
    private ManageSelectPeopleAdapter myAdapter;
    private EmptyRecyclerView myrv;
    private View mEmptyView;

    private ImageView addBtn;
    private ImageView importBtn;
    private Button upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_edit_people_info);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除

        /// RecyclerView使用的Adapter以及串列 ///
        mPeopleData = new ArrayList<PeopleData>();
        myrv = (EmptyRecyclerView) findViewById(R.id.managePeopleView);
        mEmptyView = findViewById(R.id.managePeopleView);
        myAdapter = new ManageSelectPeopleAdapter(this,mPeopleData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myrv.setLayoutManager(layoutManager);
        myrv.setItemAnimator(new DefaultItemAnimator());
        myrv.setAdapter(myAdapter);
        myrv.setEmptyView(mEmptyView);
        /// RecyclerView使用的Adapter以及串列 ///

        gv = (GlobalVariable)getApplicationContext();
        sharedPreferences = getSharedPreferences("BeaconProjectData",MODE_PRIVATE);

        /// 搜尋框的實現 ///
        final EditText searchText = (EditText)findViewById(R.id.manage_search);
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

        /// 匯入按鈕的實現 ///
        importBtn = ((ImageView)findViewById(R.id.manage_import));
        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        /// 匯入按鈕的實現 ///

        /// 新增按鈕的實現 ///
        addBtn = ((ImageView)findViewById(R.id.manage_add));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ManageEditPeopleInfo.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_add,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                final EditText addNamefield = (EditText)mView.findViewById(R.id.addName);
                final EditText addNoticefield = (EditText)mView.findViewById(R.id.addNotice);
                Button addCompleteBtn = (Button)mView.findViewById(R.id.addCompleteBtn);
                addCompleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(addNamefield.getText().toString().trim().isEmpty()){
                            addNamefield.setError("必須輸入姓名");
                            Toast.makeText(ManageEditPeopleInfo.this, "必須輸入姓名", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //新增資料
                            mPeopleData.add(new PeopleData(addNamefield.getText().toString().trim(),addNoticefield.getText().toString().trim()));
                            myAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
        /// 新增按鈕的實現 ///

        upload = (Button)findViewById(R.id.manage_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPeopleData.size()>0){
                    ///先把搜尋框清空，才有完整結果///
                    searchText.setText(""); myAdapter.notifyDataSetChanged();
                    ///先把搜尋框清空，才有完整結果///
                    SQLConnection sqlc = new SQLConnection(gv.sqlIP, gv.sqlport, gv.sqldbname, gv.sqluser, gv.sqlpass);
                    sqlc.Connection();
                    if (sqlc.connectionSuccess) {
                        String activityId = sharedPreferences.getString("currActivityId",null);
                        if(activityId!= null){
                            String deleteRecord = String.format("DELETE FROM `record` WHERE `activatyId`='%s';",activityId);
                            sqlc.executeSQL(deleteRecord);
                            for(int i=0;i<mPeopleData.size();i++) {
                                /// 取得參加者基本資料 ///
                                String attendName = mPeopleData.get(i).getName();
                                String attendNotice = mPeopleData.get(i).getNotice();
                                String UserId = mPeopleData.get(i).getUserID();
                                String AttendTime = mPeopleData.get(i).getCheckInTime();
                                /// 取得參加者基本資料 ///
                                if(UserId != null && AttendTime!=null){
                                    ///上傳資料///
                                    String insertRecord = String.format("INSERT INTO `record`(`username`, `userId`, `activatyId`, `isSignIn`, `attendDateTime`, `notice`) VALUES ('%s','%s','%s','%s','%s','%s');",attendName,UserId,activityId,"1",AttendTime,attendNotice);
                                    sqlc.executeSQL(insertRecord);
                                    ///上傳資料///
                                }
                                else{
                                    ///上傳資料///
                                    String insertRecord = String.format("INSERT INTO `record`(`username`, `activatyId`, `notice`) VALUES ('%s','%s','%s');",attendName,activityId,attendNotice);
                                    sqlc.executeSQL(insertRecord);
                                    ///上傳資料///
                                }
                            }
                            Toast.makeText(ManageEditPeopleInfo.this, "參加人員修改成功!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(ManageEditPeopleInfo.this,"無法取得活動ID，請重選活動",Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setClass(ManageEditPeopleInfo.this, ManageHomePage.class);
                        startActivity(intent);
                        finish();
                        sqlc.close();
                    }
                }
                else{
                    Toast.makeText(ManageEditPeopleInfo.this,"至少要有一個參加者。",Toast.LENGTH_SHORT).show();
                }
            }
        });
        getList();
        /// 獲取權限 ///
        checkPermission();
        /// 獲取權限 ///
    }

    void filter(String res){
        myAdapter.getFilter().filter(res);
    }

    public void performFileSearch(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
        intent.setDataAndType(uri,"text/csv");
        Intent destIntent = Intent.createChooser( intent, "選擇csv檔案" );
        startActivityForResult(destIntent,REQUST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUST_CODE && resultCode== Activity.RESULT_OK){
            if(data!=null){
                try {
                    mPeopleData.clear();
                    InputStream input = getContentResolver().openInputStream(data.getData());
                    CSVFile csvFile = new CSVFile(input);
                    List<String[]> csvContent = csvFile.read();
                    for(int i=1;i<csvContent.size();i++){
                        if(csvContent.get(i).length==1)
                            mPeopleData.add(new PeopleData(
                                    csvContent.get(i)[0],""
                            ));
                        else if (csvContent.get(i).length>=2)
                            mPeopleData.add(new PeopleData(
                                    csvContent.get(i)[0],csvContent.get(i)[1]
                            ));
                    }
                    myAdapter.notifyDataSetChanged();
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "檔案不存在!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(this, "不支持的編碼(No Supported Encode)!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {//Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private void getList() {
        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
        sqlc.Connection();
        if(sqlc.connectionSuccess){
            String currActivityId = sharedPreferences.getString("currActivityId",null);
            if(currActivityId!=null) {
                String stmt = String.format("SELECT * FROM `record` WHERE `activatyId` = '%s'", currActivityId);
                ResultSet rs = sqlc.executeSQL(stmt);
                try{
                    while(rs.next()){
                        String userId = rs.getString("userId");
                        String activatyId = rs.getString("activatyId");
                        String isSignIn = rs.getString("isSignIn");
                        String attendDateTime = rs.getString("attendDateTime");
                        String username = rs.getString("username");
                        String notice = rs.getString("notice");
                        mPeopleData.add(new PeopleData(
                                username,notice,attendDateTime,userId
                        ));
                    }
                    myAdapter.notifyDataSetChanged();
                }
                catch(SQLException ex){
                    Toast.makeText(this,"SQL錯誤。",Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
            else{
                Toast.makeText(this,"無法取得活動Id。請重選活動",Toast.LENGTH_LONG).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_EXT_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("debug", "write permission granted");
                } else {
                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setTitle("");
                    builder.setMessage("如果拒絕寫入權限，將無法匯入csv檔!");
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
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle(" 允許讀入儲存空間");
                builder.setMessage("請授權讀寫信息以便匯入*.csv檔。");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_EXT_STORAGE);
                    }
                });
                builder.show();
            }
        }
    }
}
