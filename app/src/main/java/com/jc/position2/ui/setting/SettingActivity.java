package com.jc.position2.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.position2.R;
import com.jc.position2.base.ui.BaseActivity;
import com.jc.position2.ui.main.MainAdapter;
import com.jc.position2.ui.main.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
    }

    @Bind(R.id.setting_list_rv)
    RecyclerView recyclerView;

    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        list = new ArrayList<>();
        list.add("08:00");
        list.add("09:00");
        list.add("10:00");
        list.add("11:00");
        list.add("12:00");


        RecyclerView.Adapter adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view =  LayoutInflater.from(activity).inflate(R.layout.activity_setting_item, parent, false);
                MyViewHolder holder = new MyViewHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                if (position>=list.size()) {
                    holder.time.setText("新增");
                } else {
                    holder.time.setText(list.get(position));
                }
            }

            @Override
            public int getItemCount() {
                return list.size() + 1;
            }

            @Override
            public int getItemViewType(int position) {
                if (position < list.size()-1) {
                    return 0;
                } else {
                    return 1;
                }
            }
        };

        recyclerView.setAdapter(adapter);
        RecycleViewDivider recycleViewDivider = new RecycleViewDivider(activity, LinearLayoutManager.HORIZONTAL);
        recyclerView.addItemDecoration(recycleViewDivider);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));


        adapter.notifyDataSetChanged();
        //recyclerView.set
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.setting_item_time_tv)
        public TextView time;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                        Toast.makeText(activity, "点击的位置" + getAdapterPosition(), Toast.LENGTH_SHORT).show();

                        if (getAdapterPosition() == list.size()-1) {

                        }
                    }
                }
            });
        }
    }
}
