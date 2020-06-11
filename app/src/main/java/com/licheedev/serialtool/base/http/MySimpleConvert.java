package com.licheedev.serialtool.base.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Type;

import cn.dlc.commonlibrary.okgo.converter.MyConverter;
import cn.dlc.commonlibrary.okgo.exception.ApiException;

public class MySimpleConvert<T> extends MyConverter<T> {
    private Type mType;

    /**
     * 处理了我们接口那种code=0，code=1情况的转换器
     *
     * @param clazz
     */
    public MySimpleConvert(Class<T> clazz) {
        super(clazz);
    }

    /**
     * 处理了我们接口那种code=0，code=1情况的转换器
     */
    public MySimpleConvert() {
        super(null);
    }

    public MySimpleConvert(Type type) {
        super(null);
        mType = type;
    }


    @Override
    public T convert(String json) throws Throwable {
        return toBean01(json);
    }


    /**
     * Json转JsonElement
     *
     * @param json
     * @return
     * @throws Throwable
     */
    public static JsonElement toJsonElement(String json) throws Throwable {
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        JsonElement jsonElement = new JsonParser().parse(jsonReader);
        return jsonElement;
    }

    /**
     * json转处理完code=0，code=1情况的bean
     *
     * @param json
     * @param <T>
     * @return
     * @throws Throwable
     */
    public <T> T toBean01(String json) throws Throwable {

        JsonElement jsonElement = toJsonElement(json);

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int code = jsonObject.get("code").getAsInt();
        String msg = jsonObject.get("msg").getAsString();
        if (code == 1) {
            String data = "{}";
            try {
                if (jsonObject.has("data")) {
                    data = jsonObject.get("data").toString();
                }
            } catch (Exception e) {
                data = "{}";
            }
            T t = parseJsonStrToObj(data, mType);
            if (t == null) {
                throw new ApiException("数据异常", code);
            }
            return t;
        } else {
            throw new ApiException(msg, code);
        }
    }

    public <T> T parseJsonStrToObj(String json, Type typeOfT) {
        T result = null;
        Gson mGSon = new GsonBuilder().disableHtmlEscaping().create();
        try {
            result = mGSon.fromJson(json, typeOfT);
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return result;
    }

}
