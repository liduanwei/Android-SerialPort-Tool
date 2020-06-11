package com.licheedev.serialtool;

/**
 * @Author: We
 * @Date: 2020/05/21
 * @Desc:
 */
public class Urls {
    public static String API_HOST_URL = "http://120.24.237.146:8000";

    /*App基本信息*/
    public static final String userLoginPath = "/v1/backenduser/login";
    public static final String appInfoPath = "/v1/app/info";
    public static final String appCommandListPath = "/v1/app/command/list";

    public static String getFullUrl(String apiPath) {
        return API_HOST_URL + apiPath;
    }
}
