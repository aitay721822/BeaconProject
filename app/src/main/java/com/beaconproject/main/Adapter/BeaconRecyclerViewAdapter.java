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

import com.beaconproject.main.Common.BeaconData;
import com.beaconproject.main.R;
import com.beaconproject.main.UI.Create.AttendList;

import java.util.List;

public class BeaconRecyclerViewAdapter extends RecyclerView.Adapter<BeaconRecyclerViewAdapter.BeaconViewHolder>  {

    private Context mContext;
    private List<BeaconData> mBeaconData;

    public BeaconRecyclerViewAdapter(Context mContext, List<BeaconData> mBeaconData){
        this.mContext = mContext;
        this.mBeaconData = mBeaconData;
    }

    @NonNull
    @Override
    public BeaconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        view = mLayoutInflater.inflate(R.layout.listview_select_beacon,viewGroup,false);
        return new BeaconViewHolder(view);
    }

    public boolean addItem(BeaconData mData){
        mBeaconData.add(mData);
        return true;
    }

    public void clear(){
        mBeaconData.clear();
    }

    @Override
    public void onBindViewHolder(@NonNull final BeaconViewHolder beaconViewHolder, final int i) {

        String mMajorData = mBeaconData.get(i).getMajor();
        String mMinorData = mBeaconData.get(i).getMinor();
        String mmacAddress = mBeaconData.get(i).getMacAddress();
        String TextSource= String.format("MAC ADDRESS:%s \nMajor:%s  / Minor:%s ",mmacAddress,mMajorData,mMinorData);
        beaconViewHolder.tv_beacon_uuid.setText(mBeaconData.get(i).getUUID());
        beaconViewHolder.tv_beacon_information.setText(TextSource);

        beaconViewHolder.tv_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("您確定要選擇此Beacon?");
                builder.setMessage("選取後將會跳至下一步");
                builder.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Bundle previousBundle = ((Activity)mContext).getIntent().getExtras();
                        Intent intent = new Intent(mContext, AttendList.class);
                        previousBundle.putString("BeaconUID",mBeaconData.get(i).getUUID());
                        intent.putExtras(previousBundle);
                        mContext.startActivity(intent);
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
        return mBeaconData.size();
    }

    public static class BeaconViewHolder extends RecyclerView.ViewHolder {

        TextView tv_beacon_uuid;
        TextView tv_beacon_information;
        CardView tv_cardView;

        public BeaconViewHolder(View itemView){
            super(itemView);

            tv_beacon_uuid = (TextView)itemView.findViewById(R.id.b_uuid);
            tv_beacon_information = (TextView)itemView.findViewById(R.id.b_information);
            tv_cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}
