package com.beaconproject.main.Adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beaconproject.main.Common.ActivityData;
import com.beaconproject.main.R;
import com.beaconproject.main.UI.HomePage;
import com.beaconproject.main.UI.Manage.ManageHomePage;
import com.beaconproject.main.UI.Manage.ManageSelectActivity;
import com.beaconproject.main.UI.RecentAttend.RecentAttendActivity;

import java.util.List;

public class ManageSelectAdapter extends RecyclerView.Adapter<ManageSelectAdapter.SelectViewHolder> {

    private List<ActivityData> mActivityData;
    private Context mContext;

    public ManageSelectAdapter(Context mContext,List<ActivityData> mActivityData){
        this.mContext = mContext;
        this.mActivityData = mActivityData;
    }

    @NonNull
    @Override
    public SelectViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        view = mLayoutInflater.inflate(R.layout.listview_activity,viewGroup,false);
        return new SelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectViewHolder selectViewHolder, final int i) {
        selectViewHolder.tv_activityName.setText(mActivityData.get(i).getActivityName());
        selectViewHolder.tv_location.setText("活動地點：" + mActivityData.get(i).getActivityLocation());
        selectViewHolder.tv_BeaconUUID.setText("BeaconUUID：" + mActivityData.get(i).getBeaconUUID());
        selectViewHolder.tv_beaconCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// 儲存目前修改的活動 ///
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("BeaconProjectData",Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("currActivityId",mActivityData.get(i).getActivityId()).apply();
                sharedPreferences.edit().putString("currActivityName",mActivityData.get(i).getActivityName()).apply();
                sharedPreferences.edit().putString("currActivityLocation",mActivityData.get(i).getActivityLocation()).apply();
                sharedPreferences.edit().putString("currActivityStartDate",mActivityData.get(i).getActivityStartDate()).apply();
                sharedPreferences.edit().putString("currActivityEndDate",mActivityData.get(i).getActivityEndDate()).apply();
                sharedPreferences.edit().putString("currActivityNote",mActivityData.get(i).getActivityNote()).apply();
                /// 儲存目前修改的活動 ///

                ///啟動Manage HomePage///
                Intent i = new Intent();
                i.setClass(mContext, ManageHomePage.class);
                mContext.startActivity(i);
                ((Activity)mContext).finish();
                ///啟動Manage HomePage///
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActivityData.size();
    }

    public static class SelectViewHolder extends RecyclerView.ViewHolder{
        TextView tv_activityName;
        TextView tv_location;
        TextView tv_BeaconUUID;
        CardView tv_beaconCardView;

        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_activityName=(TextView)itemView.findViewById(R.id.activityName);
            tv_location = (TextView)itemView.findViewById(R.id.activityLocation);
            tv_BeaconUUID = (TextView) itemView.findViewById(R.id.beaconUUID);
            tv_beaconCardView = (CardView)itemView.findViewById(R.id.activityCardView);
        }
    }
}
