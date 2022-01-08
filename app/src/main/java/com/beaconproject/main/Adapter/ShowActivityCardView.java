package com.beaconproject.main.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.beaconproject.main.Common.ActivityData;
import com.beaconproject.main.Common.RecordData;
import com.beaconproject.main.Global.GlobalVariable;
import com.beaconproject.main.R;
import com.beaconproject.main.SQL.SQLConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ShowActivityCardView extends RecyclerView.Adapter<ShowActivityCardView.showActivityViewHolder>{

    private Context mContext;
    private List<RecordData> mRecordData;

    public ShowActivityCardView(Context context, List<RecordData> mRecordData){

        mContext=context;
        this.mRecordData = mRecordData;
    }

    @NonNull
    @Override
    public showActivityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        view = mLayoutInflater.inflate(R.layout.show_attended_activity,viewGroup,false);
        return new showActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull showActivityViewHolder showActivityViewHolder, int i) {
        GlobalVariable gv = (GlobalVariable)mContext.getApplicationContext();
        String activityId = mRecordData.get(i).getActivityId();
        String attendDateTime = mRecordData.get(i).getAttendDateTime();
        String isSignIn = mRecordData.get(i).getIsSignIn();
        String Notice = mRecordData.get(i).getNotice();
        String UserId = mRecordData.get(i).getUserId();
        String UserName = mRecordData.get(i).getUsername();

        ///取得活動名稱///
        SQLConnection sqlc = new SQLConnection(gv.sqlIP,gv.sqlport,gv.sqldbname,gv.sqluser,gv.sqlpass);
        sqlc.Connection();

        if(sqlc.connectionSuccess){
            if(UserId!=null){
                try{
                    String stmt_Activity = String.format("SELECT * FROM `activaty` WHERE `activatyId`='%s'",mRecordData.get(i).getActivityId());
                    ResultSet rs = sqlc.executeSQL(stmt_Activity);
                    while(rs.next()){
                        showActivityViewHolder.tv_activityLocation.setText(String.format("活動地點：%s",rs.getString("activatyLocation")));
                        showActivityViewHolder.tv_activityName.setText(String.format("%s",rs.getString("activatyName")));
                    }

                    showActivityViewHolder.tv_activityAttendTime.setText(String.format("參加時間：%s",attendDateTime));
                }
                catch (SQLException ex) {
                    showActivityViewHolder.tv_activityAttendTime.setText(String.format("參加時間：NULL"));
                    Toast.makeText(mContext,"SQL連線失敗",Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }
            else{
                Toast.makeText(mContext,"無法取得userId",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            showActivityViewHolder.tv_activityAttendTime.setText(String.format("參加時間：NULL"));
            Toast.makeText(mContext,"SQL連線失敗",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        if(mRecordData!=null) return mRecordData.size();
        else return 0;
    }

    public static class showActivityViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_activityName;
        public TextView tv_activityLocation;
        public TextView tv_activityAttendTime;
        public CardView tv_cardview;

        public showActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_activityAttendTime = (TextView)itemView.findViewById(R.id.show_activityAttenedTime);
            tv_activityLocation = (TextView)itemView.findViewById(R.id.show_activityLocation);
            tv_activityName = (TextView)itemView.findViewById(R.id.show_activityName);
            tv_cardview = (CardView)itemView.findViewById(R.id.show_activity_cardview);
        }
    }
}
