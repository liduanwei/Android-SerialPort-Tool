package com.licheedev.serialtool.model.server_api;

/**
 * @Author: We
 * @Date: 2020/06/11
 * @Desc:
 */
public class BaseResponse {
    protected int code;
    protected String msg;
    protected Object data;

    public BaseResponse() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
