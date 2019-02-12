package com.rekoe.utils;


/**
 * 企业应用接入时的常量定义
 */
public class Env {

    /**
     * 企业应用接入秘钥相关
     */
    public static final String APP_KEY = "ding61279fcb45e99b9735c2f4657eb6378f";
    public static final String APP_SECRET = "0G95FrDWk5DpaZAepJtJSTcjBwhBCFzlxYReh3sohx3He6czMGoDxbz0blx7mp66";
    
    /**
     * 钉钉网关gettoken地址
     */
    public static final String URL_GET_TOKKEN = "https://oapi.dingtalk.com/gettoken";
    /**
     *获取用户在企业内userId的接口URL
     */
    public static final String URL_GET_USER_INFO = "https://oapi.dingtalk.com/user/getuserinfo";
    /**
     *获取用户姓名的接口url
     */
    public static final String URL_USER_GET = "https://oapi.dingtalk.com/user/get";

    /**
     * DING API地址
     */
	public static final String OAPI_HOST = "https://oapi.dingtalk.com";
    /**
     * 企业应用后台地址，用户管理后台免登使用
     */
	public static final String OA_BACKGROUND_URL = "";


    /**
     * 企业通讯回调加密Token，注册事件回调接口时需要传递给钉钉服务器
     */
	public static final String TOKEN = "";
	public static final String ENCODING_AES_KEY = "";
	
}
