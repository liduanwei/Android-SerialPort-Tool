package com.licheedev.serialtool.model.server_api;

/**
 * @Author: We
 * @Date: 2020/06/11
 * @Desc:
 */
public class UserLoginResponse {

    /**
     * code : 200
     * msg : ok
     * data : {"id":"454584126918365184","enable":1,"remark":"","created_at":"2020-06-08 17:55:02","updated_at":"2020-06-11 19:18:05","account":"dlc_app","phone":"18888886666","password":"b4ff48988ac65c2b496f5d15a293e80e","password_salt":"LF1a","really_name":"","user_name":"","gender":0,"portrait_uri":"","jwt":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTE5NjA2ODUsInVzZXJfaWQiOjQ1NDU4NDEyNjkxODM2NTE4NH0.g41B1mtl4zwZ_iUqnxQMPNJSwONfb2tZ8zNQzqq3ZO0","last_ip":"219.132.205.42","is_super_admin":true}
     */

    public int code;
    public String msg;
    public DataBean data;

    public static class DataBean {
        /**
         * id : 454584126918365184
         * enable : 1
         * remark :
         * created_at : 2020-06-08 17:55:02
         * updated_at : 2020-06-11 19:18:05
         * account : dlc_app
         * phone : 18888886666
         * password : b4ff48988ac65c2b496f5d15a293e80e
         * password_salt : LF1a
         * really_name :
         * user_name :
         * gender : 0
         * portrait_uri :
         * jwt : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTE5NjA2ODUsInVzZXJfaWQiOjQ1NDU4NDEyNjkxODM2NTE4NH0.g41B1mtl4zwZ_iUqnxQMPNJSwONfb2tZ8zNQzqq3ZO0
         * last_ip : 219.132.205.42
         * is_super_admin : true
         */

        public String id;
        public int enable;
        public String remark;
        public String created_at;
        public String updated_at;
        public String account;
        public String phone;
        public String password;
        public String password_salt;
        public String really_name;
        public String user_name;
        public int gender;
        public String portrait_uri;
        public String jwt;
        public String last_ip;
        public boolean is_super_admin;
    }
}
