package com.beaconproject.main.Adapter;

import android.content.Context;
import android.nfc.Tag;
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

import com.beaconproject.main.Common.PeopleData;
import com.beaconproject.main.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewPeopleAdapter extends RecyclerView.Adapter<RecyclerViewPeopleAdapter.PeopleAdapter> implements Filterable {

    private final static String TAG = RecyclerViewPeopleAdapter.class.getSimpleName();


    private Context mCotext;
    private List<PeopleData> peopleDataList;    //固定資料
    private List<PeopleData> showDataList;     //顯示資料
    private Filter mFilter;

    public RecyclerViewPeopleAdapter(Context context,List<PeopleData> mPeopleAdapter){
        this.mCotext = context;
        this.peopleDataList = mPeopleAdapter;
        this.showDataList = peopleDataList;
    }

    @NonNull
    @Override
    public PeopleAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(mCotext);
        view = mLayoutInflater.inflate(R.layout.listview_select_people,viewGroup,false);
        return new PeopleAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleAdapter peopleAdapter, final int i) {

        final String Name = showDataList.get(i).getName();
        final String Notice = showDataList.get(i).getNotice();
        if(!Notice.trim().isEmpty())peopleAdapter.tv_notice.setText(String.format("備註：%s",Notice));
        else peopleAdapter.tv_notice.setText("備註：無");
        peopleAdapter.tv_userName.setText(Name);

        peopleAdapter.tv_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mCotext);
                View mView = LayoutInflater.from(mCotext).inflate(R.layout.edit_people_layout,null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                final EditText editName = (EditText)mView.findViewById(R.id.editName);
                final EditText editNotice = (EditText)mView.findViewById(R.id.editNotice);
                Button editCompleteBtn = (Button)mView.findViewById(R.id.editCompleteBtn);
                Button deletedBtn = (Button)mView.findViewById(R.id.deletedBtn);

                editName.setText(Name);
                editNotice.setText(Notice);

                editCompleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PeopleData newPD = new PeopleData(
                                editName.getText().toString().trim(),
                                editNotice.getText().toString().trim(),
                                showDataList.get(i).getCheckInTime(),
                                showDataList.get(i).getUserID()
                        );
                        editItem(showDataList.get(i),newPD);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                deletedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(showDataList.get(i));
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    public boolean editItem(PeopleData oldData, PeopleData newData){
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

    public boolean removeItem(PeopleData mData){
        int index_allList = -1, index_showList = -1;
        for(int i=0;i<peopleDataList.size();i++)
            if(peopleDataList.get(i).equals(mData))
                index_allList = i;
        if(index_allList!=-1)
            peopleDataList.remove(index_allList);

        for(int i=0;i<showDataList.size();i++)
            if(showDataList.get(i).equals(mData))
                index_showList = i;
        if(index_showList!=-1){
            showDataList.remove(index_showList);
        }
        return true;
    }

    public boolean addItem(PeopleData mData){
        peopleDataList.add(mData);
        showDataList.add(mData);
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
                    List<PeopleData> filterData = new ArrayList<PeopleData>();
                    if (constraint != null && constraint.toString().trim().length() > 0) {
                        Log.d(TAG, "確認是否為空值。 filterData.size = " + filterData.size());
                        for (int i = 0; i < peopleDataList.size(); i++) {
                            String content = peopleDataList.get(i).getName();
                            Log.d(TAG, "確認是否進入for迴圈 content = " + content);
                            if (content.contains(String.valueOf(constraint))) {
                                Log.d(TAG, "確認輸入文字是否相同。");
                                PeopleData data = new PeopleData();
                                data.setName(peopleDataList.get(i).getName());
                                data.setNotice(peopleDataList.get(i).getNotice());
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
                    showDataList = (List<PeopleData>) results.values;
                    if (results.count >= 0) {
                        notifyDataSetChanged();
                    }
                }
            };
        }
        return mFilter;
    }

    public static class PeopleAdapter extends RecyclerView.ViewHolder{
        TextView tv_userName;
        TextView tv_notice;
        CardView tv_cardView;
        public PeopleAdapter(@NonNull View itemView) {
            super(itemView);
            tv_userName=(TextView)itemView.findViewById(R.id.username);
            tv_notice = (TextView)itemView.findViewById(R.id.note);
            tv_cardView = (CardView) itemView.findViewById(R.id.cardPeople);
        }
    }
}
