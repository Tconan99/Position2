package com.jc.position2.ui.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.position2.R;
import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.logic.alarm.AlarmTimeUtils;
import com.jc.position2.logic.position.LocTypeUtils;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tconan on 16/4/27.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{

    private Activity activity;
    private List<JCCalendar> list;
    private ProgressDialog progressDialog;

    public MainAdapter(Activity activity, List<JCCalendar> list) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(
                LayoutInflater.from(activity).inflate(R.layout.activity_main_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // GetInfo
        JCCalendar calendar = list.get(position);
        int style = calendar.getStyle();
        String location = calendar.getAddressStr();

        // StateStyle
        holder.messageTV.setVisibility(View.VISIBLE);
        holder.button.setVisibility(View.GONE);
        holder.messageTV.setTextColor(Color.GRAY);

        if (style==AlarmTimeUtils.STATE_NONE) {
            holder.typeTV.setText("计算中...");
            holder.messageTV.setText("计算中...");

        } else if (style==AlarmTimeUtils.STATE_LOST) {
            holder.typeTV.setText("已超期");
            holder.messageTV.setText("已超期");
            holder.messageTV.setTextColor(Color.RED);

        } else if (style==AlarmTimeUtils.STATE_NOT_ARRIVED) {
            holder.typeTV.setText("未开始");
            holder.messageTV.setText("未开始");

        } else if (style==AlarmTimeUtils.STATE_SIGNED) {
            //holder.typeTV.setText("已签到");
            holder.typeTV.setText(location);
            holder.messageTV.setText("已签到");
            holder.messageTV.setTextColor(Color.GREEN);

        } else if (style==AlarmTimeUtils.STATE_UNUPLOAD) {
            //holder.typeTV.setText("未上传");
            holder.typeTV.setText(location);
            holder.messageTV.setText("未上传");
            holder.messageTV.setTextColor(activity.getResources().getColor(android.R.color.holo_orange_dark));

        } else if (style==AlarmTimeUtils.STATE_SIGNING) {
            holder.messageTV.setVisibility(View.GONE);
            holder.button.setVisibility(View.VISIBLE);
            holder.typeTV.setText("签到");

        }

        // DateTime
        holder.timeTV.setText(calendar.getDateStr());


    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View view;

        @Bind(R.id.main_item_type_tv)
        public TextView typeTV;

        @Bind(R.id.main_item_time_tv)
        public TextView timeTV;

        @Bind(R.id.main_item_button)
        public Button button;

        @Bind(R.id.main_item_message_tv)
        public TextView messageTV;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.main_item_button)
        public void buttonOnClick() {

            // TODO 签到时间校验

            if (progressDialog!=null) {
                Toast.makeText(activity, "正在签到, 请稍候...", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog = ProgressDialog.show(activity, null, "正在签到, 请稍候...");

            BaseApplication.start(new CallBack() {
                @Override
                public void callBack(ServiceResult result) {

                    if (result.isSuccess()) {
                        Toast.makeText(activity, "签到成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "签到失败->" + result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    AlarmTimeUtils.updateListStyle(activity, list, new CallBack() {
                        @Override
                        public void callBack(ServiceResult result) {
                            notifyDataSetChanged();

                            if (progressDialog!=null) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        }
                    });
                }
            });
        }
    }
}
