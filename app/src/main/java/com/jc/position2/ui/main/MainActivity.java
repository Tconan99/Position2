package com.jc.position2.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.position2.R;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.ui.BaseActivity;
import com.jc.position2.logic.alarm.AlarmService;
import com.jc.position2.logic.alarm.AlarmTimeUtils;
import com.jc.position2.logic.log.LogService;
import com.jc.position2.logic.log.LogStoreService;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.login.UserStoreService;
import com.jc.position2.logic.position.PositionStoreService;
import com.jc.position2.ui.login.LoginActivity;
import com.jc.position2.ui.position.PositionListActivity;
import com.jc.position2.ui.setting.SettingActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements CallBack {

    @Bind(R.id.main_recycler)
    RecyclerView recyclerView;

    @Bind(R.id.main_week_tv)
    TextView weekTextView;

    @Bind(R.id.main_day_tv)
    TextView dayTextView;

    @Bind(R.id.main_swipe)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.main_username_tv)
    TextView usernameTextView;

    RecyclerView.Adapter adapter;
    List<JCCalendar> list;

    List<String> calendarWeekName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        UserStoreService userStoreService = new UserStoreService(activity);
        UserBean userBean = userStoreService.load();

        if (userBean==null || userBean.getId()==null) {
            Intent intent = LoginActivity.getIntent(activity);
            startActivity(intent);
            finish();
            return;
        }

        // 设置当前时间
        calendarWeekName = new ArrayList<>();
        calendarWeekName.add("");
        calendarWeekName.add("星期日");
        calendarWeekName.add("星期一");
        calendarWeekName.add("星期二");
        calendarWeekName.add("星期三");
        calendarWeekName.add("星期四");
        calendarWeekName.add("星期五");
        calendarWeekName.add("星期六");
        //
        GregorianCalendar calendar = new GregorianCalendar();
        //calendar.
        String weekStr = calendarWeekName.get(calendar.get(GregorianCalendar.DAY_OF_WEEK));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        String dayStr = simpleDateFormat.format(calendar.getTime());

        weekTextView.setText(weekStr);
        dayTextView.setText(dayStr);

        usernameTextView.setText(userBean.getDisplayName());


        // 列表 初始化
        AlarmTimeUtils alarmTimeUtils = new AlarmTimeUtils();
        list = JCCalendar.convert(alarmTimeUtils.getListGC());

        adapter = new MainAdapter(activity, list);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        RecycleViewDivider recycleViewDivider = new RecycleViewDivider(activity, LinearLayoutManager.HORIZONTAL);
        recycleViewDivider.setMarginLeft(120);
        recyclerView.addItemDecoration(recycleViewDivider);
        adapter.notifyDataSetChanged();

        // 刷新
        onRefresh();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.this.onRefresh();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        UserStoreService userStoreService = new UserStoreService(activity);
        UserBean userBean = userStoreService.load();

        if (userBean==null || userBean.getId()==null) {
            Intent intent = LoginActivity.getIntent(activity);
            startActivity(intent);
            return;
        }

    }

    /**
     * 定位回调函数
     * @param result 考勤结果
     */
    @Override
    public void callBack(ServiceResult result) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.item_right) {
            Intent intent = PositionListActivity.getIntent(activity);
            startActivity(intent);
        } else if (item.getItemId()==R.id.item_left) {
            Intent intent = SettingActivity.getIntent(activity);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_text, menu);
        return true;
    }

    private boolean isRefresh;
    public synchronized void onRefresh() {
        if (isRefresh) {
            Toast.makeText(activity, "正在刷新列表，请稍后再试", Toast.LENGTH_SHORT).show();
        }

        isRefresh = true;

        swipeRefreshLayout.setRefreshing(true);


        AlarmTimeUtils.updateListStyle(activity, list, new CallBack() {
            @Override
            public void callBack(ServiceResult result) {
                if (result.isSuccess()) {
                    Toast.makeText(activity, "加载数据成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "加载数据失败", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                isRefresh = false;
            }
        });

    }

    @Override
    public void baseOnRefresh() {
        super.baseOnRefresh();
        onRefresh();
    }

    @OnClick(R.id.main_username_tv)
    public void exitOnClick(View view) {

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserStoreService userStoreService = new UserStoreService(activity);
                userStoreService.remove();

                // 切换用户bug
                LogStoreService.isChanged = true;
                PositionStoreService.isChanged = true;

                // 关闭循环
                AlarmService alarmService = new AlarmService(activity);
                alarmService.close();

                Intent intent = LoginActivity.getIntent(activity);
                startActivity(intent);
            }
        };

        AlertDialog.Builder builder = new AlertDialog
                .Builder(activity)
                .setTitle("确认")
                .setMessage("是否退出登录？")
                .setPositiveButton("确定", onClickListener)
                .setNegativeButton("取消", null);
        builder.create().show();

    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
