package com.jc.position2.logic.signin;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.jc.position2.base.service.BaseService;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.base.ui.BaseActivity;
import com.jc.position2.logic.log.JCLog;
import com.jc.position2.logic.log.LogStoreService;
import com.jc.position2.logic.login.UserBean;
import com.jc.position2.logic.network.OpenNetWorkService;
import com.jc.position2.logic.position.JCLocation;
import com.jc.position2.logic.position.PositionService;
import com.jc.position2.logic.position.PositionStoreService;
import com.jc.position2.logic.upload.UploadService;
import com.jc.position2.logic.login.UserStoreService;

import java.util.Date;
import java.util.List;

/**
 * Created by tconan on 16/3/23.
 */
public class SignInService extends BaseService {

    private PositionService positionService;
    private LogStoreService logStoreService;
    private PositionStoreService positionStoreService;
    private UploadService uploadService;
    private OpenNetWorkService openNetWorkService;
    private CallBack callBack;
    private Context context;
    private SignInService my;
    private static boolean isSigning = false; // 定位锁，也是数据锁 TODO 数据的异步操作问题
    private JCLog log;

    public SignInService(Context context, CallBack callBack) {
        this.callBack = callBack;
        this.context = context;
        this.my = this;

        // 本地缓存服务
        positionStoreService = new PositionStoreService(this.context);
        logStoreService = new LogStoreService(this.context);

        // 开启网络服务
        openNetWorkService = new OpenNetWorkService(this.context, new CallBack() {
            @Override
            public void callBack(ServiceResult serviceResult) {
                if (serviceResult.isSuccess()) {
                    log.add("开启网络成功");
                    Log.i("data11", "开启网络成功");
                    positionService.start();
                } else {
                    log.add("开启网络失败");
                    Log.i("data11", "开启网络失败");
                    openNetWorkService.close4GIfServiceOpen();
                    my.callBack(false, "开启网络失败");
                }
            }
        });

        // 定位服务
        positionService = new PositionService(this.context, new CallBack() {
            @Override
            public void callBack(ServiceResult result) {

                if (result.isSuccess()) {
                    BDLocation location = (BDLocation) result.getTag();

                    log.add("定位成功");
                    Log.i("data11", "定位成功");
                    // 保存定位数据
                    List<JCLocation> list = positionStoreService.load();
                    JCLocation jcLocation = new JCLocation();
                    jcLocation.setLocation(location);
                    list.add(jcLocation);
                    positionStoreService.save(list);

                    // 上传数据
                    log.add("上传数据");
                    Log.i("data11", "上传数据");
                    uploadService.uploadAuto();
                }
            }

        });

        // 上传服务
        uploadService = new UploadService(this.context, new CallBack() {
            @Override
            public void callBack(ServiceResult result) {
                openNetWorkService.close4GIfServiceOpen();

                if (my.callBack!=null) {
                    if (result.isSuccess()) {
                        log.add("上传成功");
                        Log.i("data11", "上传成功");
                        my.callBack(true, "上传成功");
                    } else {
                        log.add("上传失败->" + result.getMessage());
                        Log.i("data11", "上传失败");
                        my.callBack(false, "上传失败");
                    }
                }
            }

        });

    }

    public synchronized void start() {

        if (isSigning) {
            Toast.makeText(context, "正在定位请稍后", Toast.LENGTH_SHORT).show();
            callBack(false, "正在定位请稍后");
            return;
        }
        isSigning = true;

        log = new JCLog();
        log.setSaveTimeStap(new Date().getTime());

        UserStoreService user = new UserStoreService(context);
        UserBean userBean = user.load();

        if (userBean==null) {
            Toast.makeText(context, "没有登录", Toast.LENGTH_SHORT).show();
            callBack(false, "没有登录");
            log.add("没有登录");
            return;
        }

        openNetWorkService.open4G();

    }

    public void start(CallBack callBack) {
        if (isSigning) {
            Toast.makeText(context, "正在定位请稍后", Toast.LENGTH_SHORT).show();
            callBack(false, "正在定位请稍后");
            return;
        }

        if (callBack!=null) {
            this.callBack = callBack;
        }

        start();
    }

    public void callBack(boolean isSuccess, String message) {
        Log.i("data11", log.getLog().toString());

        ServiceResult result = new ServiceResult();
        result.setSuccess(isSuccess);
        result.setMessage(message);
        result.setBaseService(this);

        // 保存日志
        logStoreService.add(log);

        my.callBack.callBack(result);
        isSigning = false;

        BaseActivity.staticOnRefresh();
    }

}
