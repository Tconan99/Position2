package com.jc.position2.ui.main;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.jc.position2.R;
import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.ui.BaseActivity;
import com.jc.position2.logic.login.UserStoreService;
import com.jc.position2.ui.login.LoginActivity;
import com.jc.position2.ui.position.PositionListActivity;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        WelcomeActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);


        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if (msg.what==1) {
                    Intent intent = MainActivity.getIntent(activity);
                    startActivity(intent);
                    finish();
                } else if (msg.what==2) {
                    Intent intent = PositionListActivity.getIntent(activity);
                    startActivity(intent);
                    finish();
                } else if (msg.what==999) {
                    Intent intent = LoginActivity.getIntent(activity);
                    startActivity(intent);
                    finish();
                }
            }
        };

        Message message = new Message();
        message.what = 1;

        // 判断是否登录了
        UserStoreService userStoreService = new UserStoreService(activity);
        if (!userStoreService.isLogined()) {
            message.what = 999;
        }

        handler.sendMessageDelayed(message, 1500L);

        if (BaseApplication.alarmService!=null) {
            BaseApplication.alarmService.start();
        }
    }
}
