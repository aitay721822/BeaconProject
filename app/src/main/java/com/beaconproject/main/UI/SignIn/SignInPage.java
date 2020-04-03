package com.beaconproject.main.UI.SignIn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.MacAddress;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.UI.HomePage;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SignInPage extends AppCompatActivity implements View.OnTouchListener{

    SharedPreferences sharedPreferences;
    private String MacAddress = null;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        /// 將虛擬按鍵清除
        decorView = getWindow().getDecorView();
        /// 將虛擬按鍵清除
        /// 宣告全域變數 ///
        GlobalVariable data = (GlobalVariable) getApplicationContext();
        /// 宣告全域變數 ///

        /// 宣告Shared Preference ///
        sharedPreferences = getSharedPreferences("BeaconProjectData" , MODE_PRIVATE);
        /// 宣告Shared Preference ///

        MacAddress = sharedPreferences.getString("macAddress",null);
        EditText uuid_text = (EditText)findViewById(R.id.macAddress);
        if(MacAddress!=null) uuid_text.setText(MacAddress);
        else {
            uuid_text.setText("null");
            finish();
        }
    }

    public void uploadToSql(View view){
        /// data row
        String macAdr = ((EditText)(findViewById(R.id.macAddress))).getText().toString().trim();
        String userid = ((EditText)(findViewById(R.id.userid))).getText().toString().trim();
        String email = ((EditText)(findViewById(R.id.email))).getText().toString().trim();
        String name = ((EditText)(findViewById(R.id.name))).getText().toString().trim();
        String phone = ((EditText)(findViewById(R.id.phone))).getText().toString().trim();
        /// data row

        String[] uploadData = {};
        GlobalVariable data = (GlobalVariable) getApplicationContext();
        SQLConnection sqlc = new SQLConnection(data.sqlIP,data.sqlport,data.sqldbname,data.sqluser,data.sqlpass);
        sqlc.Connection();
        if(sqlc.connectionSuccess){
            if(!(macAdr.matches("null") || userid.isEmpty() && email.isEmpty() && name.isEmpty() && phone.isEmpty())){
                if(!checkIdUsed(((EditText)(findViewById(R.id.userid))),sqlc)){
                    String sqlStmt = String.format("INSERT INTO `user` (`userId`, `macAddress`, `name`, `usercode`, `phone`, `email`) VALUES (UUID(), '%s', '%s', '%s', '%s', '%s');",macAdr,name,userid,phone,email);
                    sqlc.executeSQL(sqlStmt);

                    ///  into HomePage ///
                    sqlStmt = String.format("SELECT * FROM `user` WHERE `macAddress`=\"%s\"",MacAddress);
                    ResultSet result = sqlc.executeSQL(sqlStmt);
                    int count = 0;
                    try{
                        if(result!=null) {
                            result.last();
                            count = result.getRow();
                            if(count>0){
                                Intent intent = new Intent();
                                sharedPreferences.edit().putString("userId",result.getString("userId")).apply();
                                sharedPreferences.edit().putString("usercode",result.getString("usercode")).apply();
                                sharedPreferences.edit().putString("userEmail",result.getString("email")).apply();
                                sharedPreferences.edit().putString("username",result.getString("name")).apply();
                                sharedPreferences.edit().putString("userPhone",result.getString("phone")).apply();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setClass(SignInPage.this, HomePage.class);
                                startActivity(intent);
                                finish();
                                sqlc.close();
                            }
                        }
                        else{
                            Toast.makeText(this, "無法連線至SQL", Toast.LENGTH_LONG).show();
                        }
                    }
                    catch(SQLException ex){
                        ex.printStackTrace();
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                    ///  into HomePage ///
                }
            }
            else{
                EditText Met = ((EditText)(findViewById(R.id.macAddress)));
                EditText Uet = ((EditText)(findViewById(R.id.userid)));
                EditText Eet = ((EditText)(findViewById(R.id.email)));
                EditText Net = ((EditText)(findViewById(R.id.name)));
                EditText Pet = ((EditText)(findViewById(R.id.phone)));
                checkInputEmpty(Met); checkInputEmpty(Uet); checkInputEmpty(Eet); checkInputEmpty(Net); checkInputEmpty(Pet);
            }
        }
        else{
            Toast.makeText(this, "無法連線至SQL", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIdUsed(EditText et, SQLConnection sqlc)  {
        String userId = et.getText().toString().trim();
        String sqlStmt = String.format("SELECT * FROM `user` WHERE `userId`=\"%s\"",userId);
        ResultSet result = sqlc.executeSQL(sqlStmt);
        int count = 0;
        try{
            if(result!=null) {
                result.last();
                count = result.getRow();
                if(count>0){
                    et.setError("ID已有人使用");
                    Toast.makeText(this, "此ID已有人使用", Toast.LENGTH_LONG).show();
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                Toast.makeText(this, "無法連線至SQL", Toast.LENGTH_LONG).show();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }

    private void checkInputEmpty(EditText et){
        if(et.getText().toString().trim().isEmpty()){
            et.setError("請輸入文字。");
        }
        else{
            et.setError(null);
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
