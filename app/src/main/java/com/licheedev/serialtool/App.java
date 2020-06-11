package com.licheedev.serialtool;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.licheedev.myutils.LogPlus;
import com.licheedev.serialtool.base.http.MyErrorTranslator;
//import com.licheedev.serialtool.util.OkGoWrapper;
import com.licheedev.serialtool.model.server_api.AppInfoResponse;
import com.licheedev.serialtool.model.server_api.UserLoginResponse;
import com.licheedev.serialtool.util.DeviceHelper;
import com.licheedev.serialtool.util.PrefHelper;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;


import java.nio.charset.Charset;

import cn.dlc.commonlibrary.okgo.OkGoWrapper;
import cn.dlc.commonlibrary.okgo.exception.ApiException;
import cn.dlc.commonlibrary.okgo.logger.JsonRequestLogger;
import cn.dlc.commonlibrary.okgo.rx.OkObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by Administrator on 2017/3/28 0028.
 */

public class App extends Application {

    private Handler mUiHandler;
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        mUiHandler = new Handler();
        initUtils();
        initOkGo();
    }

    private void initUtils() {
        PrefHelper.initDefault(this);
    }

    public static App instance() {
        return sInstance;
    }

    public static Handler getUiHandler() {
        return instance().mUiHandler;
    }

    private void initOkGo() {
        // 网络
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)))
                .addInterceptor(chain -> {
                    Request.Builder builder1 = chain.request().newBuilder();
                    String token = DeviceHelper.getUserToken();
                    token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTE5NjA2ODUsInVzZXJfaWQiOjQ1NDU4NDEyNjkxODM2NTE4NH0.g41B1mtl4zwZ_iUqnxQMPNJSwONfb2tZ8zNQzqq3ZO0";
                    if (token != null) {
                        builder1.addHeader("Authorization", token);
                    }
                    Request request = builder1.build();
                    RequestBody requestBody = request.body();

                    Charset UTF8 = Charset.forName("UTF-8");
                    String reqBody = null;
                    if (requestBody != null) {
                        Buffer buffer = new Buffer();
                        requestBody.writeTo(buffer);

                        Charset charset = UTF8;
                        MediaType contentType = requestBody.contentType();
                        if (contentType != null) {
                            charset = contentType.charset(UTF8);
                        }
                        reqBody = buffer.readString(charset);
                    }

                    Log.e("retrofitRequest", String.format("Sending request %s on %s%n%s", request.url(), reqBody, request.headers()));
                    Response response = chain.proceed(request);
                    ResponseBody body = response.peekBody(1024 * 1024);
                    String ss = body.string();
                    Log.e("retrofitResponse", ss);
                    return response;
                });
        OkGoWrapper.initOkGo(this, builder.build());
        OkGoWrapper.instance().setErrorTranslator(new MyErrorTranslator())
                // 拦截网络错误，一般是登录过期啥的
                .setErrorInterceptor(tr -> {
                    if (tr instanceof ApiException) {
                        ApiException ex = (ApiException) tr;
                        if (ex.getCode() == 401) {
                            // 登录信息过期，请重新登录
                            /*todo */
                            doUserLogin();
                            return true;
                        }
                    }
                    return false;
                })
                .setRequestLogger(new JsonRequestLogger(BuildConfig.DEBUG, 30));
    }

    private void doUserLogin() {
        MainHttp.get()
                .doUserLogin("dlc_app", "123456")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OkObserver<UserLoginResponse>() {
                    @Override
                    public void onSuccess(UserLoginResponse response) {
                        DeviceHelper.saveUserToken(response.data.jwt);
                    }

                    @Override
                    public void onFailure(String s, Throwable throwable) {
                        LogPlus.d(s);
                    }
                });
    }
}
