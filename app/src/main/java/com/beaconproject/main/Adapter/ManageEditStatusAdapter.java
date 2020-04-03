package com.beaconproject.main.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconproject.main.Common.PeopleData;
import com.beaconproject.main.Common.RecordData;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.UI.Create.AttendList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageEditStatusAdapter extends RecyclerView.Adapter<ManageEditStatusAdapter.ManageEditStatusViewHolder> implements Filterable {

    private final static String TAG = RecyclerViewPeopleAdapter.class.getSimpleName();

    private Context mCotext;
    private List<RecordData> peopleDataList;    //固定資料
    private List<RecordData> showDataList;     //顯示資料
    private Filter mFilter;

    public ManageEditStatusAdapter(Context context,List<RecordData> mPeopleAdapter){
        this.mCotext = context;
        this.peopleDataList = mPeopleAdapter;
        this.showDataList = peopleDataList;
    }

    @NonNull
    @Override
    public ManageEditStatusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mCotext);
        view = mLayoutInflater.inflate(R.layout.listview_checkin_status,viewGroup,false);
        return new ManageEditStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ManageEditStatusViewHolder manageEditStatusViewHolder, final int i) {
        manageEditStatusViewHolder.tv_userName.setText(showDataList.get(i).getUsername());
        if(showDataList.get(i).getIsSignIn().equals("1")) {
            manageEditStatusViewHolder.tv_status.setText("已簽到");
            manageEditStatusViewHolder.tv_status.setTextColor(mCotext.getResources().getColor(R.color.LightGreen));
        }
        else{
            manageEditStatusViewHolder.tv_status.setText("未簽到");
            manageEditStatusViewHolder.tv_status.setTextColor(mCotext.getResources().getColor(R.color.LightRed));
        }

        manageEditStatusViewHolder.tv_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(mCotext);
                View mView = LayoutInflater.from(mCotext).inflate(R.layout.dialog_change_status,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                final EditText showname = (EditText)mView.findViewById(R.id.showNameStatus);
                showname.setText(manageEditStatusViewHolder.tv_userName.getText());

                final Button changeCheckIn = (Button)mView.findViewById(R.id.changeIsCheckIn);
                final Button changeNotIn = (Button)mView.findViewById(R.id.changeNotCheckIn);
                final Button viewDetails = (Button)mView.findViewById(R.id.viewDetails);

                changeCheckIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecordData newData = new RecordData(
                                showDataList.get(i).getActivityId(),
                                showDataList.get(i).getUserId(),
                                "1",
                                showDataList.get(i).getUsername(),
                                showDataList.get(i).getAttendDateTime(),
                                showDataList.get(i).getNotice()
                                );
                        editItem(showDataList.get(i),newData);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                changeNotIn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        RecordData newData = new RecordData(
                                showDataList.get(i).getActivityId(),
                                showDataList.get(i).getUserId(),
                                "0",
                                showDataList.get(i).getUsername(),
                                showDataList.get(i).getAttendDateTime(),
                                showDataList.get(i).getNotice()
                        );
                        editItem(showDataList.get(i),newData);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                viewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(showDataList.get(i).getUserId()!=null){
                            GlobalVariable gv = (GlobalVariable)mCotext.getApplicationContext();
                            SQLConnection sqlc =new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
                            sqlc.Connection();
                            if(sqlc.connectionSuccess){
                                String stmt = String.format("SELECT `name`, `usercode`, `phone`, `email` FROM `user` WHERE `userId` = '%s'",showDataList.get(i).getUserId());
                                ResultSet rs = sqlc.executeSQL(stmt);
                                try{
                                    while(rs.next()){
                                        String name = rs.getString("name");
                                        String usercode = rs.getString("usercode");
                                        String phone = rs.getString("phone");
                                        String email = rs.getString("email");
                                        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(mCotext);
                                        View dialogView = LayoutInflater.from(mCotext).inflate(R.layout.dialog_user_information,null);
                                        dialogBuilder.setView(dialogView);
                                        final AlertDialog showInformation = dialogBuilder.create();

                                        EditText showname = dialogView.findViewById(R.id.showUserName);
                                        EditText showCode = dialogView.findViewById(R.id.showUserCodes);
                                        EditText showPhone = dialogView.findViewById(R.id.showUserPhone);
                                        EditText showEmail = dialogView.findViewById(R.id.showUserEmail);
                                        Button gobackbtn = dialogView.findViewById(R.id.goBackBtn);
                                        showname.setText(name); showCode.setText(usercode); showPhone.setText(phone); showEmail.setText(email);
                                        gobackbtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showInformation.dismiss();
                                            }
                                        });
                                        showInformation.show();
                                    }
                                }
                                catch (SQLException ex){
                                    ex.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(mCotext,"無法連接SQL。",Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(mCotext,"無資料顯示。",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public boolean editItem(RecordData oldData, RecordData newData){
        int index_allList = -1, index_showList = -1;
        for(int i=0;i<peopleDataList.size();i++)
            if(peopleDataList.get(i).equals(oldData))
                index_allList = i;
        if(index_allList!=-1){
            peopleDataList.remove(index_allList);
            peopleDataList.add(index_allList,newData);
        }

        for(int i=0;i<showDataList.size();i++)
            if(showDataList.get(i).equals(oldData))
                index_showList = i;
        if(index_showList!=-1){
            showDataList.remove(index_showList);
            showDataList.add(index_showList,newData);
        }

        return true;
    }

    @Override
    public int getItemCount() {
        return showDataList.size();
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null){
            mFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<RecordData> filterData = new ArrayList<RecordData>();
                    if (constraint != null && constraint.toString().trim().length() > 0) {
                        Log.d(TAG, "確認是否為空值。 filterData.size = " + filterData.size());
                        for (int i = 0; i < peopleDataList.size(); i++) {
                            String content = peopleDataList.get(i).getUsername();
                            Log.d(TAG, "確認是否進入for迴圈 content = " + content);
                            if (content.contains(String.valueOf(constraint))) {
                                Log.d(TAG, "確認輸入文字是否相同。");
                                RecordData data = new RecordData();
                                data.setUsername(peopleDataList.get(i).getUsername());
                                data.setNotice(peopleDataList.get(i).getNotice());
                                data.setUserId(peopleDataList.get(i).getUserId());
                                data.setActivityId(peopleDataList.get(i).getActivityId());
                                data.setAttendDateTime(peopleDataList.get(i).getAttendDateTime());
                                data.setIsSignIn(peopleDataList.get(i).getIsSignIn());
                                filterData.add(data);
                            }
                        }
                    }
                    else{
                        filterData = peopleDataList;
                        Log.d(TAG,"確認什麼都沒打 filterDatas = datalists = " + peopleDataList.size());
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.count = filterData.size();
                    filterResults.values = filterData;
                    Log.d(TAG, "final size = " + filterResults.count);
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    showDataList = (List<RecordData>) results.values;
                    if (results.count >= 0) {
                        notifyDataSetChanged();
                    }
                }
            };
        }
        return mFilter;
    }

    public static class ManageEditStatusViewHolder extends RecyclerView.ViewHolder{
        TextView tv_userName;
        TextView tv_status;
        CardView tv_cardView;
        public ManageEditStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_userName=(TextView)itemView.findViewById(R.id.statusUsername);
            tv_status = (TextView)itemView.findViewById(R.id.status);
            tv_cardView = (CardView) itemView.findViewById(R.id.cardStatus);
        }
    }
}