package com.beaconproject.main.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;

import java.util.List;

public class AddAdminAdapter extends RecyclerView.Adapter<AddAdminAdapter.AddAdninViewHolder> {

    private Context mContext;
    private List<PeopleData> mPeopleData;
    public AddAdminAdapter(Context mContext,List<PeopleData> mPeopleData){
        this.mContext = mContext;
        this.mPeopleData = mPeopleData;
    }
    @NonNull
    @Override
    public AddAdninViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        view = mLayoutInflater.inflate(R.layout.listview_admin,viewGroup,false);
        return new AddAdninViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAdninViewHolder addAdninViewHolder, final int i) {
        addAdninViewHolder.tv_admin.setText(mPeopleData.get(i).getName());

        addAdninViewHolder.tv_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("您確定要移除此管理者?");
                builder.setMessage("移除後管理者就不能再更改活動任何內容。");
                builder.setPositiveButton("是的",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        GlobalVariable gv = (GlobalVariable)mContext.getApplicationContext();
                        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
                        sqlc.Connection();
                        if(sqlc.connectionSuccess){
                            String stmt = String.format("DELETE FROM `activitypermission` WHERE `activityId`='%s' AND `userId`='%s';",mPeopleData.get(i).getNotice(),mPeopleData.get(i).getUserID());
                            mPeopleData.remove(i);
                            sqlc.executeSQL(stmt);
                            sqlc.close();
                            notifyDataSetChanged();
                        }else{
                            Toast.makeText(mContext,"無法連線SQL",Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return mPeopleData.size();
    }

    public static class AddAdninViewHolder extends RecyclerView.ViewHolder{
        TextView tv_admin;
        CardView tv_cardView;

        public AddAdninViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_admin = (TextView)itemView.findViewById(R.id.adminName);
            tv_cardView = (CardView)itemView.findViewById(R.id.adminCard);
        }
    }
}
