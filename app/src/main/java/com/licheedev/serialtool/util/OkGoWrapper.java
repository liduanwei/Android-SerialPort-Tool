
package com.licheedev.serialtool.util;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.dlc.commonlibrary.okgo.callback.MyCallback;
import cn.dlc.commonlibrary.okgo.converter.Bean01Convert;
import cn.dlc.commonlibrary.okgo.converter.Convert2;
import cn.dlc.commonlibrary.okgo.converter.MyConverter;
import cn.dlc.commonlibrary.okgo.interceptor.ErrorInterceptor;
import cn.dlc.commonlibrary.okgo.logger.RequestLogger;
import cn.dlc.commonlibrary.okgo.translator.ErrorTranslator;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class OkGoWrapper {
    private ErrorInterceptor mErrorInterceptor;
    private ErrorTranslator mErrorTranslator;
    private RequestLogger mRequestLogger;

    private OkGoWrapper() {
    }

    public static OkGoWrapper instance() {
        return OkGoWrapper.InstanceHolder.sInstance;
    }

    private void checkOkGo() {
        try {
            OkGo.getInstance().getContext();
        } catch (Exception var2) {
            throw new IllegalStateException("OkGo未初始化，必须先调用initOkGo()");
        }
    }

    public static void initOkGo(Application app, OkHttpClient okHttpClient) {
        OkGo okGo = OkGo.getInstance().init(app);
        if (okHttpClient != null) {
            okGo.setOkHttpClient(okHttpClient);
        }

    }

    public OkGoWrapper setErrorInterceptor(ErrorInterceptor errorInterceptor) {
        this.mErrorInterceptor = errorInterceptor;
        return this;
    }

    public OkGoWrapper setErrorTranslator(ErrorTranslator errorTranslator) {
        this.mErrorTranslator = errorTranslator;
        return this;
    }

    public OkGoWrapper setRequestLogger(RequestLogger requestLogger) {
        this.mRequestLogger = requestLogger;
        return this;
    }

    @Nullable
    public ErrorInterceptor getErrorInterceptor() {
        return this.mErrorInterceptor;
    }

    @Nullable
    public ErrorTranslator getErrorTranslator() {
        return this.mErrorTranslator;
    }

    public RequestLogger getRequestLogger() {
        return this.mRequestLogger;
    }

    public <T> void asyncRequest(HttpMethod httpMethod, String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, Class<T> clazz, MyCallback<T> callback, @Nullable Object tag) {
        this.checkOkGo();
        tag = tag == null ? clazz : tag;
        if (clazz != null) {
            callback.setClass(clazz);
        }

        switch (httpMethod) {
            case GET:
                ((GetRequest) ((GetRequest) ((GetRequest) OkGo.get(url).headers(headers)).params(params)).tag(tag)).execute(callback);
                break;
            default:
                ((PostRequest) ((PostRequest) ((PostRequest) OkGo.post(url).headers(headers)).params(params)).tag(tag)).execute(callback);
        }

    }


    public <T> void post(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, Class<T> clazz, MyCallback<T> callback, @Nullable Object tag) {
        this.asyncRequest(HttpMethod.POST, url, headers, params, clazz, callback, tag);
    }


    public <T> void post(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, Class<T> clazz, MyCallback<T> callback) {
        this.post(url, headers, params, clazz, callback, (Object) null);
    }

    public <T> void post(String url, @Nullable HttpParams params, Class<T> clazz, MyCallback<T> callback) {
        this.post(url, (HttpHeaders) null, params, clazz, callback);
    }

    public <T> void get(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, Class<T> clazz, MyCallback<T> callback, @Nullable Object tag) {
        this.asyncRequest(HttpMethod.GET, url, headers, params, clazz, callback, tag);
    }

    public <T> void get(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, Class<T> clazz, MyCallback<T> callback) {
        this.get(url, headers, params, clazz, callback, (Object) null);
    }

    public <T> void get(String url, @Nullable HttpParams params, Class<T> clazz, MyCallback<T> callback) {
        this.get(url, (HttpHeaders) null, params, clazz, callback);
    }

    public void cancelAll() {
        OkGo.getInstance().cancelAll();
    }

    public void cancelTag(Object tag) {
        OkGo.getInstance().cancelTag(tag);
    }

    private Response syncRequest(HttpMethod httpMethod, String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, @Nullable Object tag) throws IOException {
        this.checkOkGo();
        switch (httpMethod) {
            case GET:
                return ((GetRequest) ((GetRequest) ((GetRequest) OkGo.get(url).headers(headers)).params(params)).tag(tag)).execute();
            default:
                return ((PostRequest) ((PostRequest) ((PostRequest) OkGo.post(url).headers(headers)).params(params)).tag(tag)).execute();
        }
    }

    private Response syncRequestJson(String url, @Nullable HttpHeaders headers, @Nullable String jsonData, @Nullable Object tag) throws IOException {
        this.checkOkGo();
        return ((PostRequest) ((PostRequest) ((PostRequest) OkGo.post(url).headers(headers)).upJson(jsonData)).tag(tag)).execute();
    }

    public <T> T syncRequest(HttpMethod httpMethod, String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, @NonNull MyConverter<T> converter, @Nullable Object tag) throws Throwable {
        String json = null;

        try {
            Object finalTag = tag == null ? converter.getToConvertClass() : tag;
            Response response = this.syncRequest(httpMethod, url, headers, params, finalTag);
            int responseCode = response.code();
            if (responseCode != 404 && responseCode < 500) {
                json = Convert2.toString(response);
                T t = converter.convert(json);
                if (this.mRequestLogger != null) {
                    this.mRequestLogger.logRequest(url, headers, params, json, (Throwable) null);
                }

                return t;
            } else {
                throw HttpException.NET_ERROR();
            }
        } catch (Throwable var12) {
            if (this.mRequestLogger != null) {
                this.mRequestLogger.logRequest(url, headers, params, json, var12);
            }

            throw var12;
        }
    }


    public <T> T syncRequestJson(String url, @Nullable HttpHeaders headers, @Nullable String jsonData, @NonNull MyConverter<T> converter, @Nullable Object tag) throws Throwable {
        String json = null;

        try {
            Object finalTag = tag == null ? converter.getToConvertClass() : tag;
            Response response = this.syncRequestJson(url, headers, jsonData, finalTag);
            int responseCode = response.code();
            if (responseCode != 404 && responseCode < 500) {
                json = Convert2.toString(response);
                T t = converter.convert(json);
                return t;
            } else {
                throw HttpException.NET_ERROR();
            }
        } catch (Throwable var12) {
            throw var12;
        }
    }


    public <T> Observable<T> rxRequest(final HttpMethod httpMethod, final String url, @Nullable final HttpHeaders headers, @Nullable final HttpParams params, @NonNull final MyConverter<T> converter, @Nullable final Object tag) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<T> emitter) throws Exception {
                try {
                    T t = OkGoWrapper.this.syncRequest(httpMethod, url, headers, params, converter, tag);
                    emitter.onNext(t);
                } catch (Throwable var3) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(var3);
                        return;
                    }
                }

                emitter.onComplete();
            }
        }).compose(this.interceptError());
    }


    public <T> Observable<T> rxRequestJson(final String url, @Nullable final HttpHeaders headers, @Nullable final String json, @NonNull MyConverter<T> converter, @Nullable final Object tag) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<T> emitter) throws Exception {
                try {
                    T t = OkGoWrapper.this.syncRequestJson(url, headers, json, converter, tag);
                    emitter.onNext(t);
                } catch (Throwable var3) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(var3);
                        return;
                    }
                }

                emitter.onComplete();
            }
        }).compose(this.interceptError());
    }

    public <T> Observable<T> rxRequest(HttpMethod httpMethod, String url, @Nullable HttpParams params, @NonNull MyConverter<T> converter, @Nullable Object tag) {
        return this.rxRequest(httpMethod, url, (HttpHeaders) null, params, converter, tag);
    }

    private <T> ObservableTransformer<T, T> interceptError() {
        return new ObservableTransformer<T, T>() {
            public ObservableSource<T> apply(@io.reactivex.annotations.NonNull Observable<T> upstream) {
                return upstream.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    public ObservableSource<? extends T> apply(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        return OkGoWrapper.this.mErrorInterceptor != null && OkGoWrapper.this.mErrorInterceptor.interceptException(throwable) ? Observable.empty() : Observable.error(throwable);
                    }
                });
            }
        };
    }

    public <T> Observable<T> rxPost(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, @NonNull MyConverter<T> converter, @Nullable Object tag) {
        return this.rxRequest(HttpMethod.POST, url, headers, params, converter, tag);
    }

    public <T> Observable<T> rxPost(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, @NonNull MyConverter<T> converter) {
        return this.rxPost(url, headers, params, converter, (Object) null);
    }

    public <T> Observable<T> rxPostJson(String url, @Nullable HttpHeaders headers, @NonNull String json, @NonNull MyConverter<T> converter, @Nullable Object tag) {
        if (headers == null) {
            headers = new HttpHeaders("Content-Type", "application/json");
        } else {
            headers.put("Content-Type", "application/json");
        }
        return this.rxRequestJson(url, headers, json, converter, tag);
    }

    public <T> Observable<T> rxPost(String url, @Nullable HttpParams params, @NonNull MyConverter<T> converter) {
        return this.rxPost(url, (HttpHeaders) null, params, converter, (Object) null);
    }

    public <T> Observable<T> rxPostBean01(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, Class<T> clazz) {
        return this.rxPost(url, headers, params, new Bean01Convert(clazz));
    }

    public <T> Observable<T> rxPostBean01(String url, @Nullable HttpParams params, Class<T> clazz) {
        return this.rxPostBean01(url, (HttpHeaders) null, params, clazz);
    }

    public <T> Observable<T> rxGet(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, @NonNull MyConverter<T> converter, @Nullable Object tag) {
        return this.rxRequest(HttpMethod.GET, url, headers, params, converter, tag);
    }

    public <T> Observable<T> rxGet(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, @NonNull MyConverter<T> converter) {
        return this.rxGet(url, headers, params, converter, (Object) null);
    }

    public <T> Observable<T> rxGet(String url, @Nullable HttpParams params, @NonNull MyConverter<T> converter) {
        return this.rxGet(url, (HttpHeaders) null, params, converter);
    }

    public <T> Observable<T> rxGetBean01(String url, @Nullable HttpHeaders headers, @Nullable HttpParams params, Class<T> clazz) {
        return this.rxGet(url, headers, params, new Bean01Convert(clazz));
    }

    public <T> Observable<T> rxGetBean01(String url, @Nullable HttpParams params, Class<T> clazz) {
        return this.rxGetBean01(url, (HttpHeaders) null, params, clazz);
    }

    public String testSnapshot() {
        return "test";
    }

    private static class InstanceHolder {
        private static final OkGoWrapper sInstance = new OkGoWrapper();

        private InstanceHolder() {
        }
    }
}
