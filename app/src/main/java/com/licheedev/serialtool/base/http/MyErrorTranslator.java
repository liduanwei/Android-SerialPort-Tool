package com.licheedev.serialtool.base.http;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.exception.HttpException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import cn.dlc.commonlibrary.okgo.exception.ApiException;
import cn.dlc.commonlibrary.okgo.translator.ErrorTranslator;


public class MyErrorTranslator implements ErrorTranslator {
    
    @Override
    public String translate(Throwable e) {

        e.printStackTrace();
        if (e instanceof HttpException) {
            return "服务器异常, 请稍后再试";
        } else if (e instanceof ApiException) {
            return e.getMessage();
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof JsonSyntaxException
                || e instanceof JsonSerializer
                || e instanceof NotSerializableException
                || e instanceof ParseException) {
            return "网络接口异常, 请稍后再试";
        } else if (e instanceof ClassCastException) {
            return "类型转换错误";
        } else if (e instanceof ConnectException) {
            return "网络异常，无法连接到服务器";
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            return "证书验证失败";
        } else if (e instanceof ConnectTimeoutException
                || e instanceof java.net.SocketTimeoutException
                || e instanceof UnknownHostException) {
            return "服务器连接超时, 请稍后再试";
        } else if (e instanceof NullPointerException) {
            return "NullPointerException";
        } else {
            return e.getMessage();
        }
    }
    
}
