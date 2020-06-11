package com.licheedev.serialtool.model.server_api;

import java.util.List;

/**
 * @Author: We
 * @Date: 2020/06/11
 * @Desc:
 */
public class AppCommandListResponse {

    /**
     * code : 200
     * msg : ok
     * data : {"page":0,"pageSize":10,"total":1,"offset":0,"pages":1,"data":[{"id":"454585691058212864","enable":1,"remark":"","created_at":"2020-06-08 18:01:15","updated_at":"2020-06-08 18:01:15","app_id":"454585930641051648","command_hex":"3BB3A1B2C3D4E5F613"}]}
     */

    public int code;
    public String msg;
    public DataBeanX data;

    public static class DataBeanX {
        /**
         * page : 0
         * pageSize : 10
         * total : 1
         * offset : 0
         * pages : 1
         * data : [{"id":"454585691058212864","enable":1,"remark":"","created_at":"2020-06-08 18:01:15","updated_at":"2020-06-08 18:01:15","app_id":"454585930641051648","command_hex":"3BB3A1B2C3D4E5F613"}]
         */

        public int page;
        public int pageSize;
        public int total;
        public int offset;
        public int pages;
        public List<DataBean> data;

        public static class DataBean {
            /**
             * id : 454585691058212864
             * enable : 1
             * remark :
             * created_at : 2020-06-08 18:01:15
             * updated_at : 2020-06-08 18:01:15
             * app_id : 454585930641051648
             * command_hex : 3BB3A1B2C3D4E5F613
             */

            public String id;
            public int enable;
            public String remark;
            public String created_at;
            public String updated_at;
            public String app_id;
            public String command_hex;
        }
    }
}
