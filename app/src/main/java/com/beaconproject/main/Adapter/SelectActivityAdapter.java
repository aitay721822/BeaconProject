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

import com.beaconproject.main.Common.ActivityData;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.UI.CheckIn.SelectListPage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SelectActivityAdapter extends RecyclerView.Adapter<SelectActivityAdapter.ActivityInfoAdapter> {

    private List<ActivityData> mActivityData;
    private Context mContext;

    public SelectActivityAdapter(Context mContext,List<ActivityData> mActivityData){
        this.mContext=mContext;
        this.mActivityData=mActivityData;
    }

    @NonNull
    @Override
    public ActivityInfoAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        view = mLayoutInflater.inflate(R.layout.listview_activity,viewGroup,false);
        return new ActivityInfoAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityInfoAdapter activityInfoAdapter, final int i) {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("BeaconProjectData" , MODE_PRIVATE);

        activityInfoAdapter.tv_activityName.setText(mActivityData.get(i).getActivityName());
        activityInfoAdapter.tv_BeaconUUID.setText("BeaconUID：" + mActivityData.get(i).getBeaconUUID());
        activityInfoAdapter.tv_location.setText("活動地點：" + mActivityData.get(i).getActivityLocation());
        activityInfoAdapter.tv_beaconCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActivityData.get(i).validCheckInTime()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("您確定要選擇此活動?");
                    builder.setMessage("選取後將會跳至下一步");
                    builder.setPositiveButton("好的",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            ///建立連線///
                            GlobalVariable data = (GlobalVariable)mContext.getApplicationContext();
                            SQLConnection sqlc = new SQLConnection(data.sqlIP,data.sqlport,data.sqldbname,data.sqluser,data.sqlpass);
                            sqlc.Connection();
                            ///建立連線///

                            Intent getData = ((Activity) mContext).getIntent();

                            /// 檢查 ///
                            String UserId = sharedPreferences.getString("userId",null);
                            if(UserId!=null){
                                String stmt = String.format("SELECT * FROM `record` WHERE `userId` = '%s' AND `activatyId`='%s' ",UserId,mActivityData.get(i).getActivityId());
                                ResultSet result = sqlc.executeSQL(stmt);
                                int count = 0;
                                if(result!=null){
                                    try {
                                        result.last();
                                        count = result.getRow();
                                        if(count>0) {
                                            Toast.makeText(mContext,"Sorry,你不能再重複參加此次活動了",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Intent intent = new Intent(mContext, SelectListPage.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("activityId",mActivityData.get(i).getActivityId());
                                            bundle.putString("activityName",mActivityData.get(i).getActivityName());
                                            bundle.putString("activatyLocation",mActivityData.get(i).getActivityLocation());
                                            bundle.putString("activityStartDate",mActivityData.get(i).getActivityStartDate());
                                            bundle.putString("activityEndDate",mActivityData.get(i).getActivityEndDate());
                                            bundle.putString("activityNote",mActivityData.get(i).getActivityNote());
                                            bundle.putString("activityCreationDate",mActivityData.get(i).getActivityCreationDate());
                                            bundle.putString("beaconUUID",mActivityData.get(i).getBeaconUUID());

                                            intent.putExtras(bundle);
                                            mContext.startActivity(intent);
                                            sqlc.close();
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            /// 檢查 ///
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
                else
                    Toast.makeText(mContext,"日期不合法故不能參加。",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean editItem(ActivityData oldData, ActivityData newData, int position){
        removeItem(oldData);
        mActivityData.add(position,newData);
        return true;
    }

    public boolean removeItem(ActivityData mData){
        mActivityData.remove(mData);
        return true;
    }

    public boolean addItem(ActivityData mData){
        mActivityData.add(mData);
        return true;
    }

    public void clear(){
        mActivityData.clear();
    }

    @Override
    public int getItemCount() {
        return mActivityData.size();
    }

    public static class ActivityInfoAdapter extends RecyclerView.ViewHolder{

        TextView tv_activityName;
        TextView tv_location;
        TextView tv_BeaconUUID;
        CardView tv_beaconCardView;
        public ActivityInfoAdapter(@NonNull View itemView){
            super(itemView);

            tv_activityName=(TextView)itemView.findViewById(R.id.activityName);
            tv_location = (TextView)itemView.findViewById(R.id.activityLocation);
            tv_BeaconUUID = (TextView) itemView.findViewById(R.id.beaconUUID);
            tv_beaconCardView = (CardView)itemView.findViewById(R.id.activityCardView);
        }
    }
}
