package com.licheedev.serialtool;


import com.dlc.commonbiz.base.util.GsonUtil;
import com.licheedev.serialtool.model.server_api.AppCommandListResponse;
import com.licheedev.serialtool.model.server_api.AppInfoResponse;
import com.licheedev.serialtool.model.server_api.UserLoginResponse;
import com.licheedev.serialtool.util.OkGoWrapper;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.util.HashMap;
import java.util.Map;

import cn.dlc.commonlibrary.okgo.converter.RawBeanConvert;
import io.reactivex.Observable;

import static com.licheedev.serialtool.Urls.appCommandListPath;
import static com.licheedev.serialtool.Urls.appInfoPath;
import static com.licheedev.serialtool.Urls.createCommandPath;
import static com.licheedev.serialtool.Urls.getFullUrl;
import static com.licheedev.serialtool.Urls.userLoginPath;


public class MainHttp {
    private final OkGoWrapper mOkGoWrapper;

    private MainHttp() {
        mOkGoWrapper = OkGoWrapper.instance();
    }


    private static class InstanceHolder {
        private static final MainHttp sInstance = new MainHttp();
    }

    public static MainHttp get() {
        return InstanceHolder.sInstance;
    }


    /**
     * 登录 拿 token
     */
    public Observable<UserLoginResponse> doUserLogin(String account, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("account", account);
        params.put("password", password);
        String json = GsonUtil.getInstance().parseObjToJsonStr(params);
        return mOkGoWrapper.rxPostJson(getFullUrl(userLoginPath), null, json, new RawBeanConvert<>(UserLoginResponse.class), null);
    }

    /**
     * 获取APP基本信息
     */
    public Observable<AppInfoResponse> getAppInfo(String sid) {
        Map<String, Object> params = new HashMap<>();
        params.put("sid", sid);
        String json = GsonUtil.getInstance().parseObjToJsonStr(params);
        return mOkGoWrapper.rxPostJson(getFullUrl(appInfoPath), null, json, new RawBeanConvert<>(AppInfoResponse.class), null);
    }

    /**
     * 获取APP串口命令
     */
    public Observable<AppCommandListResponse> getAppCommandList(String appId, int page, int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", appId);
        params.put("page", page);
        params.put("pageSize", pageSize);
        String json = GsonUtil.getInstance().parseObjToJsonStr(params);
        return mOkGoWrapper.rxPostJson(getFullUrl(appCommandListPath), null, json, new RawBeanConvert<>(AppCommandListResponse.class), null);
    }


    /**
     * 新增APP串口指令
     */
    public Observable<AppCommandListResponse> createAppCommand(String appId, String commandHex, String comment) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", appId);
        params.put("command_hex", commandHex);
        params.put("comment", comment);
        String json = GsonUtil.getInstance().parseObjToJsonStr(params);
        return mOkGoWrapper.rxPostJson(getFullUrl(createCommandPath), null, json, new RawBeanConvert<>(AppCommandListResponse.class), null);
    }

}

