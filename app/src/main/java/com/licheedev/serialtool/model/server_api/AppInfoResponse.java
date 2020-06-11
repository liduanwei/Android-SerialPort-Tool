package com.licheedev.serialtool.model.server_api;

import com.google.gson.annotations.SerializedName;

/**
 * @Author: We
 * @Date: 2020/06/11
 * @Desc:
 */
public class AppInfoResponse {
    public int code;
    public String msg;

    /**
     * data : {"id":"454585930641051648","enable":1,"remark":"","created_at":"2020-06-08 18:02:12","updated_at":"2020-06-08 18:02:12","sid":"fycz","name":"风影称重柜","icon":"http://xiaozhuschool.com/app_icon.png"}
     */

    @SerializedName("data")
    public DataBean data;

    public static class DataBean {
        /**
         * id : 454585930641051648
         * enable : 1
         * remark :
         * created_at : 2020-06-08 18:02:12
         * updated_at : 2020-06-08 18:02:12
         * sid : fycz
         * name : 风影称重柜
         * icon : http://xiaozhuschool.com/app_icon.png
         */

        public String id;
        public int enable;
        public String remark;
        public String created_at;
        public String updated_at;
        public String sid;
        public String name;
        public String icon;
    }
}
