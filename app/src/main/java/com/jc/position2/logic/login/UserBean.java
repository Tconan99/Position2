package com.jc.position2.logic.login;


import android.util.Log;

import lombok.Data;

/**
 * Created by tconan on 16/4/13.
 */
@Data
public class UserBean implements Cloneable {

    private Long id;          // 用户编号
    private String username;    // 用户名
    private String password;    // 密码
    private String deviceTag;   // 设备标记 暂时无用
    private String imeiNo;      // imei号
    private String deviceModel; // 设备类型

    // 服务端存储的值
    private String displayName;
    private String loginName;
    private String mobile;
    private String email;
    private String dutyId;
    private String photo;
    private Long deptId;
    private String deptName;
    private Long orgId;
    private String orgName;
    private String officeTel;/*办公室电话*/
    private String dutyIdValue;/*职务*/

    @Override
    public UserBean clone() {
        UserBean userBean = null;
        try {
            userBean = (UserBean)super.clone();
        } catch (Exception e) {
            Log.e("error", "error", e);
        }
//        UserBean userBean = new UserBean();
//        userBean.username = this.username;
//        userBean.password = this.password;
//        userBean.deviceTag = this.deviceTag;
//        userBean.imeiNo = this.imeiNo;
//        userBean.deviceModel = this.deviceModel;
        return userBean;
    }
}
