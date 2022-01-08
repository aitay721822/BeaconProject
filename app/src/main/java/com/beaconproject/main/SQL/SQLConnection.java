package com.beaconproject.main.SQL;

import android.os.StrictMode;
import android.widget.Toast;

import java.sql.*;

public class SQLConnection {
    public boolean connectionSuccess = false;
    private Connection con = null;
    private Statement stmt = null;
    private String IPAddress = null;
    private String Port = null;
    private String DBName = null;
    private String username = null;
    private String password = null;
    public SQLConnection(String ip,String port,String db,String user,String pass){
        IPAddress=ip;
        Port=port;
        DBName=db;
        username=user;
        password=pass;
    }

    public ResultSet executeSQL(String sql){
        if(connectionSuccess){
            ResultSet rs = null;
            try {
                stmt.execute(sql);
                rs = stmt.getResultSet();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return rs;
        }
        else return null;
    }


    public void Connection(){
        if(IPAddress!=null && Port!=null && DBName!=null && username!=null && password!=null){
            try {
                Class.forName("com.mysql.jdbc.Driver");
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy);
                DriverManager.setLoginTimeout(5);
                con = DriverManager.getConnection("jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=true&characterEncoding=UTF-8", username, password);
                stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                if(con!=null) connectionSuccess=true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void close(){
        try {
            if(con!=null)con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
