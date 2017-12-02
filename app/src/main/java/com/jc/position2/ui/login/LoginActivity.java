package com.jc.position2.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jc.position2.R;
import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.ui.BaseActivity;
import com.jc.position2.logic.alarm.AlarmService;
import com.jc.position2.logic.login.LoginService;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.login.UserStoreService;
import com.jc.position2.ui.main.MainActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Bind(R.id.login_imei_tv)
    TextView loginImeiTV;

    @Bind(R.id.login_name_tv)
    EditText loginNameEt;

    @Bind(R.id.login_phone_tv)
    EditText loginPswEt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // 设置标题
        setTitle("登录");

        // 设置IMEI
        String imei = BaseApplication.getImei();
        loginImeiTV.setText(imei);
    }

    @OnClick(R.id.login_ok_btn)
    public void onLoginOkBtnClick(View view) {

        CallBack callBack = new CallBack() {
            @Override
            public void callBack(ServiceResult serviceResult) {
                if (serviceResult.isSuccess()) {
                    Toast.makeText(activity, "success", Toast.LENGTH_SHORT).show();

                    UserStoreService userStoreService = new UserStoreService(activity);
                    userStoreService.save((UserBean)serviceResult.getTag());

                    AlarmService alarmService = new AlarmService(activity);
                    alarmService.start();

                    Intent intent = MainActivity.getIntent(activity);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(activity, serviceResult.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();

        Map<String, String> options = new HashMap<>();
        options.put("username", loginNameEt.getText().toString());
        options.put("password", loginPswEt.getText().toString());
        options.put("imeiNo", imei);
        options.put("deviceModel", "huawei");

        LoginService.login(options, callBack);
    }

}
