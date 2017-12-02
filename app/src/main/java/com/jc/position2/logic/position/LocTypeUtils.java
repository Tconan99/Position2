package com.jc.position2.logic.position;

import com.baidu.location.BDLocation;

/**
 * Created by tconan on 16/3/23.
 */
public class LocTypeUtils {
    public static String valueOf(BDLocation location) {

        String locType = "没有位置信息";

        if (location!=null) {
            locType = location.getLocType() + ":";
        }

        if (location==null) {
            locType = "没有位置信息";
        } else if (location.getLocType() == BDLocation.TypeNone) {// 没有结果
            locType += "TypeNone";

        } else if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            locType += "gps定位成功";

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            locType += "网络定位成功";

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            locType += "离线定位成功，离线定位结果也是有效的";

        } else if (location.getLocType() == BDLocation.TypeServerError) {
            locType += "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因";

        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            locType += "网络不同导致定位失败，请检查网络是否通畅";

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            locType += "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机";

        }   else if (location.getLocType() == BDLocation.TypeOffLineLocationFail) {
            locType += "离线定位失败";

        }  else if (location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail) {
            locType += "网络连接失败时，查找本地离线定位时对应的返回结果";

        }  else if (location.getLocType() == BDLocation.TypeCacheLocation) {
            locType += "定位缓存的结果";

        }  else if (location.getLocType() == 502) {
            locType += "key参数错误，请按照说明文档重新申请KEY";

        }  else if (location.getLocType() == 505) {
            locType += "key不存在或者非法，请按照说明文档重新申请KEY";

        }  else if (location.getLocType() == 601) {
            locType += "key服务被开发者自己禁用，请按照说明文档重新申请KEY";

        }  else if (location.getLocType() == 602) {
            locType += "key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY";

        }  else if (location.getLocType() == 501 || location.getLocType() == 700) {
            locType += "key验证失败，请按照说明文档重新申请KEY";

        }

        return locType;
    }

    public static boolean isSuccess(BDLocation location) {
        boolean isSuccess;

        if (location==null) {
            isSuccess = false;

        } else if (location.getLocType() == BDLocation.TypeGpsLocation) {
            // GPS定位结果
            isSuccess = true;

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
            // 网络定位结果
            isSuccess = true;

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
            // 离线定位结果
            isSuccess = true;

        } else if (location.getLocType() == BDLocation.TypeCacheLocation) {
            //定位缓存结果
            isSuccess = true;

        } else {
            isSuccess = false;

        }

        return isSuccess;
    }

    public static final String STRING_NO_ADDRESS = "没有地址";
    public static String getLocationDescribe(BDLocation location) {

        String name = STRING_NO_ADDRESS;

        if (location!=null) {

            name = location.getLocationDescribe();

            if (name == null || "".equals(name.trim()) || "null".equals(name.trim()) || STRING_NO_ADDRESS.equals(name)) {
                if (location.getAddress() != null) {
                    name = location.getAddrStr();
                }
            }

            if (name == null || "".equals(name.trim()) || "null".equals(name.trim()) || STRING_NO_ADDRESS.equals(name)) {
                name = STRING_NO_ADDRESS;
            }
        }

        return name;
    }
}
