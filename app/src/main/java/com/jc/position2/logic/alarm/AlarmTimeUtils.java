package com.jc.position2.logic.alarm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.jc.position2.base.application.BaseApplication;
import com.jc.position2.base.callback.CallBack;
import com.jc.position2.base.callback.ServiceResult;
import com.jc.position2.logic.position.JCLocation;
import com.jc.position2.logic.position.LocTypeUtils;
import com.jc.position2.logic.position.PositionStoreService;
import com.jc.position2.ui.main.JCCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by tconan on 16/3/24.
 */
public class AlarmTimeUtils {

    //public static final int EVENT_NONE = 0;
    //public static final int EVENT_LOCATION = 1;
    //public static final int EVENT_LIST = 2;
    //public static final int EVENT_LOCATION_LIST = 3;

    public static final int STATE_NULL = -1;
    public static final int STATE_NONE = 0;
    public static final int STATE_LOST = 1;
    public static final int STATE_NOT_ARRIVED= 2;
    public static final int STATE_SIGNED = 3;
    public static final int STATE_SIGNING = 4;
    public static final int STATE_UNUPLOAD = 5;
    public static final int STATE_FAILTURE = 6;


    private List<String> list;
    private List<GregorianCalendar> listGC;
    public static final long TIME_RANGE = 10 * 60 * 1000;
    public static final long TIME_RUN = 5 * 60 * 1000;


    public static List<JCLocation> listLocation; // 更新列表时使用的临时容器, 存储定位的列表信息
    public static boolean updateLock;   // 更新锁, 保证同时只进行一份更新

    public final static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AlarmTimeUtils() {

        list = new ArrayList<>();

        for (int i=6; i<=20; ++i) {
            for (int j=0; j<=59; j=j+30) {
                String hour;
                if (i<10) {
                    hour = "0" + String.valueOf(i);
                } else {
                    hour = String.valueOf(i);
                }

                String minute;
                if (j<10) {
                    minute = "0" + String.valueOf(j);
                } else {
                    minute = String.valueOf(j);
                }

                list.add(hour + ":" + minute);
            }

        }

//        list.add("11:35");
//        list.add("09:30");
//        list.add("10:30");
//        list.add("11:30");
//        list.add("12:30");
//        list.add("13:00");
//        list.add("14:30");
//        list.add("15:43");
//        list.add("16:30");
//        list.add("17:30");
//        list.add("18:30");
//        list.add("19:30");

        listGC = new ArrayList<>();

        get();
    }

    private void get() {

        // TODO 通过网络获取数据，成功后需要转化
        convert(list);
    }

    public List<GregorianCalendar> getListGC() {
        return Collections.unmodifiableList(listGC);
    }

    private void convert(List<String> list) {

        listGC.clear();

        if (list==null || list.size()==0) {
            return;
        }

        for (int i=0; i<list.size(); ++i) {
            try {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                int hourAndMinute = getHourAndMinute(list.get(i));
                int hour = hourAndMinute/100;
                int minute = hourAndMinute%100;
                int second = 0;
                gregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
                gregorianCalendar.set(GregorianCalendar.MINUTE, minute);
                gregorianCalendar.set(GregorianCalendar.SECOND, second);

                listGC.add(gregorianCalendar);
            } catch (Exception e) {
                Log.i("data11", "转化时间失败，时间的格式不正确，应为\"mm:ss\"，实际为\"" + list.get(i) + "\""  , e);
                // TODO 本地缓存异常信息，上传服务器
            }
        }
    }

    private int getHourAndMinute(String string) throws Exception {
        int answer;
        try {
            if (string != null && string.length() == 5 && string.charAt(2) == ':') {
                int hour = Integer.valueOf(string.substring(0, 2));
                int minute = Integer.valueOf(string.substring(3, 5));
                answer = hour * 100 + minute;
            } else {
                throw new Exception("转化时间失败，时间的格式不正确，应为\"mm:ss\"");
            }
        } catch (Exception e) {
            Log.i("data11", "转化时间失败，时间的格式不正确，应为\"mm:ss\"", e);
            throw e;
        }

        return answer;
    }

    public static LocationResult getEvent() {
        GregorianCalendar nowCalendar = new GregorianCalendar();
        return getStyle(nowCalendar);
    }

    public static synchronized LocationResult getStyle(GregorianCalendar selectedCalendar) {
        return getStyle(selectedCalendar, false);
    }

    /**
     * 获取参数时间的状态
     * @param selectedCalendar 需要查询状态的时间
     * @param needUpdateList 是否需要更新列表，在更新整个列表状态的时候不需要更列表，在单独使用的时候需要更新列表
     *                        TODO 修改这个逻辑，列表是否更新，由加载列表方法决定，load方法更加的简易，节省资源
     * @return 状态
     */
    public static synchronized LocationResult getStyle(GregorianCalendar selectedCalendar, boolean needUpdateList) {

        if (needUpdateList) {
            updateList();
        }

        LocationResult locationResult = new LocationResult();

        int answerState = STATE_NULL;

        // 获取定位时间列表对应的正确时间, 如果不再定位范围内, 则返回null
        selectedCalendar = getCurrentCalendar(selectedCalendar);

        if (selectedCalendar==null) {
            answerState = STATE_NONE;
        }

        // 当前时间
        GregorianCalendar nowCalendar = new GregorianCalendar();

        // 数据时间, 用setTime重用
        GregorianCalendar dataCalendar = new GregorianCalendar();

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (listLocation!=null && listLocation.size()>0) {

            for (JCLocation jcLocation : listLocation) {

                //Log.i("data11", jcLocation.getLocation().getTime());
                try {
                    // 数据的日期
                    long dataTime = jcLocation.getCreateTime();
                    dataCalendar.setTimeInMillis(dataTime);

                    // 判断是否是同一天的数据, 如果不是同一天的数据则不做处理
                    if (dataCalendar.get(Calendar.YEAR) != nowCalendar.get(Calendar.YEAR)
                            || dataCalendar.get(Calendar.DAY_OF_YEAR) != nowCalendar.get(Calendar.DAY_OF_YEAR)) {
                        continue;
                    }

                    //Log.i("data11", simpleDateFormat.format(dataCalendar.getTime()) + "--" + simpleDateFormat.format(selectedCalendar.getTime()));

                    // 判断列表数据和查询数据是否是相近的时间
                    if (AlarmTimeUtils.isNear(dataCalendar, selectedCalendar)) {

                        if (LocTypeUtils.isSuccess(jcLocation.getLocation())) {
                            if (jcLocation.isUpload()) {
                                answerState = AlarmTimeUtils.STATE_SIGNED;
                            } else {
                                answerState = AlarmTimeUtils.STATE_UNUPLOAD;
                            }
                            locationResult.setLocationDescribe(LocTypeUtils.getLocationDescribe(jcLocation.getLocation()));

                            break;
                        }

                    }

                } catch (Exception e) {
                    Log.e("Error", "Calculate string time error!", e);
                }
            }
        }

        if (answerState == AlarmTimeUtils.STATE_NULL) {
            // 已签列表里面没有对应的签到记录
            if (isNear(nowCalendar, selectedCalendar)) {
                // 相近
                answerState = AlarmTimeUtils.STATE_SIGNING;

            } else if (nowCalendar.after(selectedCalendar)) {
                // 过去
                answerState = AlarmTimeUtils.STATE_LOST;

            } else if (nowCalendar.before(selectedCalendar)) {
                // 到达
                answerState = AlarmTimeUtils.STATE_NOT_ARRIVED;
            } else {
                // 如果状态是STATE_NULL, 则赋值成 STATE_NONE
                answerState = AlarmTimeUtils.STATE_NONE;
            }
        }

        locationResult.setState(answerState);
        return locationResult;
    }


    /**
     * 更新列表样式的主方法
     * @param context 上下文对象
     * @param list 需要更新的列表
     * @param callBack 回调方法
     */
    public static synchronized void updateListStyle(final Context context, final List<JCCalendar> list, final CallBack callBack) {

        if (updateLock) {
            Toast.makeText(context, "正在加载数据, 请稍候...", Toast.LENGTH_SHORT).show();
        }

        updateLock = true;

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what==1 && callBack != null) {
                    ServiceResult serviceResult = new ServiceResult();
                    serviceResult.setSuccess(true);
                    callBack.callBack(serviceResult);
                }
                updateLock = false;
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                updateListStyleThread(list, handler);
            }
        };

        thread.start();

    }

    /**
     * 更新列表数据
     */
    public static synchronized void updateList() {
        // 获取定位数据列表
        PositionStoreService positionStoreService = new PositionStoreService(BaseApplication.baseApplication);
        AlarmTimeUtils.listLocation = positionStoreService.load();
        Collections.reverse(AlarmTimeUtils.listLocation);
    }

    /**
     * 更新列表样式的线程代码
     * @param list 需要更新的列表
     * @param handler 回调助手
     */
    public static synchronized void updateListStyleThread(List<JCCalendar> list, Handler handler) {

        updateList();

        if (list!=null && list.size()>0) {
            for (JCCalendar jcCalendar : list) {
                LocationResult locationResult = getStyle(jcCalendar.getGregorianCalendar());
                jcCalendar.setStyle(locationResult.getState());
                jcCalendar.setAddressStr(locationResult.getLocationDescribe());
            }
        }

        // 获取列表成功, 调用回调方法
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);

    }

    /**
     * 获取定位时间列表对应的正确时间, 如果不再定位范围内, 则返回null
     * @param calendar 当前时间, 或或者想要定位的时间
     * @return 定位时间列表对应的正确时间
     */
    public static synchronized GregorianCalendar getCurrentCalendar(GregorianCalendar calendar) {
        AlarmTimeUtils alarmTimeUtils = new AlarmTimeUtils();
        List<GregorianCalendar> list = alarmTimeUtils.getListGC();

        GregorianCalendar calendarAnswer = null;
        for (GregorianCalendar calendarTmp : list) {
            if (isNear(calendar, calendarTmp)) {
                calendarAnswer = calendarTmp;
                break;
            }
        }

        String oldStr = "null";
        String newStr = "null";

        if (calendar!=null) {
            oldStr = formatter.format(calendar.getTime());
        }
        if (calendarAnswer!=null) {
            newStr = formatter.format(calendarAnswer.getTime());
        }

        if (oldStr!=null && !oldStr.equals(newStr)) {
            Log.i("data11", "oldStr=" + oldStr + ", newStr=" + newStr);
        }
        return calendarAnswer;
    }

    /**
     * 比较两个时间是否在 TIME_RANGE 范围内
     * @param gc1 被比较的时间
     * @param gc2 比较的时间
     * @return true->在范围内, false->不再范围内
     */
    private static boolean isNear(GregorianCalendar gc1, GregorianCalendar gc2) {

        if (gc1==null || gc2==null) {
            return gc1 == gc2;
        }

        if (Math.abs(gc1.getTimeInMillis()-gc2.getTimeInMillis()) <= TIME_RANGE ) {
            return true;
        }
        return false;
    }
}
