package com.licheedev.serialtool.util;

import com.licheedev.serialtool.model.Command;
import com.licheedev.serialtool.model.server_api.AppInfoResponse;
import com.dlc.commonbiz.base.util.GsonUtil;
import com.licheedev.serialtool.App;

import java.util.List;

import cn.dlc.commonlibrary.utils.PrefUtil;

/**
 * 设备相关操作
 */
public class DeviceHelper {
    static {
        PrefUtil.init(App.instance());
    }

    //保存
    public static void saveAppSid(String sid) {
        PrefUtil.getDefault().edit().putString("sid", sid).apply();
    }

    public static String getAppSid() {
        return PrefUtil.getDefault().getString("sid", null);
    }

    //保存
    public static void saveAppInfo(AppInfoResponse appInfo) {
        PrefUtil.getDefault().edit().putString("appInfo", GsonUtil.getInstance().parseObjToJsonStr(appInfo)).apply();
    }

    //获取
    public static AppInfoResponse getAppInfo() {
        String info = PrefUtil.getDefault().getString("appInfo", null);
        return GsonUtil.getInstance().parseJsonStrToObj(info, AppInfoResponse.class);
    }


    //保存用户
    public static void saveUserToken(String token) {
        PrefUtil.getDefault().edit().putString("token", token).apply();
    }

    public static String getUserToken() {
        return PrefUtil.getDefault().getString("token", null);
    }

    public static void saveCommands(List<Command> commands, String appId) {
        PrefUtil.getDefault().edit().putString("commands_" + appId, GsonUtil.getInstance().parseObjToJsonStr(commands)).apply();
    }

    public static List<Command> getCommands(String appId) {
        String info = PrefUtil.getDefault().getString("commands_" + appId, null);
        try {
            return GsonUtil.getInstance().fromJsonArray(info, Command.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
