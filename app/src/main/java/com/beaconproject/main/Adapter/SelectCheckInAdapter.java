package com.beaconproject.main.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconproject.main.Common.PeopleData;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.UI.HomePage;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SelectCheckInAdapter extends RecyclerView.Adapter<SelectCheckInAdapter.SelectViewHolder> {

    private List<PeopleData> mPeopleData;
    private Context mContext;

    public SelectCheckInAdapter(Context mContext, List<PeopleData> mPeopleData) {
        this.mContext = mContext;
        this.mPeopleData = mPeopleData;
    }

    @NonNull
    @Override
    public SelectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        view = mLayoutInflater.inflate(R.layout.listview_select_people,viewGroup,false);
        return new SelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectViewHolder selectViewHolder,final int i) {

        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("BeaconProjectData" , MODE_PRIVATE);
        final GlobalVariable var = (GlobalVariable)mContext.getApplicationContext();

        selectViewHolder.tv_People.setText(mPeopleData.get(i).getName());
        if(!mPeopleData.get(i).getNotice().isEmpty()) selectViewHolder.tv_Notice.setText("備註：" + mPeopleData.get(i).getNotice());
        else selectViewHolder.tv_Notice.setText("備註：無");

        selectViewHolder.tv_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("確定簽到?");
                builder.setMessage("選取後將會上傳簽到資料");
                builder.setPositiveButton("好的",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent =((Activity)mContext).getIntent();
                        Bundle bundledata = intent.getExtras();
                        String userId = sharedPreferences.getString("userId",null);
                        String activatyId = bundledata.getString("activityId");
                        String notice = mPeopleData.get(i).getNotice();
                        String username = mPeopleData.get(i).getName();
                        SQLConnection sqlc = new SQLConnection(var.sqlIP,var.sqlport,var.sqldbname,var.sqluser,var.sqlpass);
                        sqlc.Connection();
                        if(sqlc.connectionSuccess && userId!=null && activatyId!=null){
                            String stmt = String.format("UPDATE `record` SET `userId`='%s',`isSignIn`='1',`attendDateTime`=NOW() WHERE `username`='%s' AND `notice`='%s' AND `activatyId` = '%s'",
                                    userId,username,notice,activatyId);
                            sqlc.executeSQL(stmt);
                            Toast.makeText(mContext, "簽到成功!", Toast.LENGTH_SHORT).show();
                            Intent gotoHomePage = new Intent();
                            gotoHomePage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            gotoHomePage.setClass(mContext, HomePage.class);
                            mContext.startActivity(gotoHomePage);
                        }
                    }
                });
                builder.setNeutralButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }
                });
                builder.show();
            }
        });
    }

    public boolean editItem(PeopleData oldData,PeopleData newData,int position){
        removeItem(oldData);
        mPeopleData.add(position,newData);
        return true;
    }

    public boolean removeItem(PeopleData mData){
        mPeopleData.remove(mData);
        return true;
    }

    public boolean addItem(PeopleData mData){
        mPeopleData.add(mData);
        return true;
    }

    public void clear(){
        mPeopleData.clear();
    }

    public void filterList(List<PeopleData> filtered){
        mPeopleData = filtered;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mPeopleData.size();
    }

    public static class SelectViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_People;
        private TextView tv_Notice;
        private CardView tv_cardview;
        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_People = (TextView)itemView.findViewById(R.id.username);
            tv_Notice = (TextView)itemView.findViewById(R.id.note);
            tv_cardview = (CardView)itemView.findViewById(R.id.cardPeople);
        }
    }
}
