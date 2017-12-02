package com.jc.position2.ui.position;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.position2.R;
import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.service.BaseService;
import com.jc.position2.base.ui.BaseActivity;
import com.jc.position2.logic.log.LogService;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.position.JCLocation;
import com.jc.position2.logic.alarm.AlarmService;
import com.jc.position2.logic.position.LocTypeUtils;
import com.jc.position2.logic.position.PositionStoreService;
import com.jc.position2.logic.upload.UploadService;
import com.jc.position2.logic.login.UserStoreService;
import com.jc.position2.ui.login.LoginActivity;
import com.jc.position2.ui.main.RecycleViewDivider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PositionListActivity extends BaseActivity implements CallBack, SwipeRefreshLayout.OnRefreshListener {

    //private SignInService signInService;
    private UploadService uploadService;
    private LogService logService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PositionStoreService positionStoreService;
    private List<JCLocation> list;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_list);

        initService();

        //AlarmTimeUtils alarmTimeUtils = new AlarmTimeUtils();

        list = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PositionListActivity.this.onRefresh();
            }
        });

        adapter = new RecyclerView.Adapter<MyViewHolder>() {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MyViewHolder holder = new MyViewHolder(
                        LayoutInflater.from(PositionListActivity.this).inflate(
                                R.layout.recycler_item, parent, false));
                return holder;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                JCLocation location = list.get(position);
                holder.time.setText(String.valueOf(location.getLocation().getTime()));
                holder.location.setText(LocTypeUtils.getLocationDescribe(location.getLocation()));
                String posStr = String.format("%s,%s", String.valueOf(location.getLocation().getLatitude()), String.valueOf(location.getLocation().getLongitude()));
                holder.loctype.setText(posStr);

                if (location.isUpload()) {
                    holder.uptype.setText("已上传");
                    holder.uptype.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    holder.uptype.setText("未上传");
                    holder.uptype.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                }
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        };

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecycleViewDivider recycleViewDivider = new RecycleViewDivider(activity, LinearLayoutManager.HORIZONTAL);
        recyclerView.addItemDecoration(recycleViewDivider);
        recyclerView.setAdapter(adapter);

        //  刷新列表
        onRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserStoreService userStoreService = new UserStoreService(activity);
        UserBean userBean = userStoreService.load();

        if (userBean==null || userBean.getId()==null) {
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
            return;
        }

    }

    private void initService() {

        //signInService = new SignInService(this, this);
        logService = new LogService(this, this);
        positionStoreService = new PositionStoreService(this);
//        alarmService = new AlarmService(this);
//        alarmService.start();

        uploadService = new UploadService(this, new CallBack() {
            @Override
            public void callBack(ServiceResult result) {
                if (result.isSuccess()) {
                    Toast.makeText(activity, "上传成功", Toast.LENGTH_SHORT).show();
                    onRefresh();
                } else {
                    Toast.makeText(activity, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_two, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.item_right) {
            BaseApplication.start(this);
        } else if (item.getItemId()==R.id.item_left) {

            logService.uploadAuto();
            //uploadService.uploadAuto();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 回调方法
     * @param result 回调值
     */
    @Override
    public void callBack(ServiceResult result) {

        long id = BaseService.getId(result.getBaseService());

        if (BaseApplication.signInService.getId() == id) {
            if (result.isSuccess()) {
                Toast.makeText(this, "签到成功", Toast.LENGTH_SHORT).show();
            } else {
                String message = "签到失败";
                if (result.getMessage()!=null) {
                    message = result.getMessage();
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
            onRefresh();
        }
    }

    private boolean isRefresh;
    public synchronized void onRefresh() {

        if (isRefresh) {
            Toast.makeText(activity, "正在刷新列表，请稍后再试", Toast.LENGTH_SHORT).show();
        }

        isRefresh = true;

        if (lastRefreshTime>=dataUpdateTime && lastRefreshTime>0) {
            // TODO 如何处理与提示
            Toast.makeText(activity, "已经是最新的数据", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            isRefresh = false;
            return;
        }

        swipeRefreshLayout.setRefreshing(true);

        // 这是本地加载，所以不用异步，如果改成网络加载，需要改成异步回调的方式
        List<JCLocation> list = positionStoreService.load();
        Collections.reverse(list);
        PositionListActivity.this.list.clear();
        for (JCLocation jcLocation:list) {
            PositionListActivity.this.list.add(jcLocation);
        }


        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();

        lastRefreshTime=new Date().getTime();
        isRefresh = false;
    }

    @Override
    public void baseOnRefresh() {
        super.baseOnRefresh();
        onRefresh();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        public View view;

        @Bind(R.id.textview_time)
        public TextView time;

        @Bind(R.id.textview_location)
        public TextView location;

        @Bind(R.id.textview_loctype)
        public TextView loctype;

        @Bind(R.id.textview_uptype)
        public TextView uptype;

        public MyViewHolder(View view)
        {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PositionListActivity.class);
        return intent;
    }
}
