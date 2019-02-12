package com.rekoe.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;

import org.nutz.boot.starter.feign.annotation.FeignInject;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@IocBean(create = "init")
public class DingOauthService {

	@Inject("java:$conf.get('dingtalk.oauth.appid')")
	private String DINGTALK_OAUTH_APPID;

	@Inject("java:$conf.get('dingtalk.oauth.appsecret')")
	private String DINGTALK_OAUTH_APPSECRET;

	public String DINGDING_URL = "https://oapi.dingtalk.com/connect/oauth2/sns_authorize?appid=" + DINGTALK_OAUTH_APPID + "&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=";

	private static String accessToken;

	@FeignInject
	protected DingOauth dingOauth;

	public void init() {
		Timer refreshTimer = new Timer();
		refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				accessToken = dingOauth.refreshAccessToken(DINGTALK_OAUTH_APPID,DINGTALK_OAUTH_APPSECRET);
				System.err.println("DingOauth init token:" + accessToken);
			}
		}, 0, 60 * 60 * 1000 * 2);
	}

	interface DingOauth {

		@RequestLine("GET /sns/gettoken?appid={appid}&appsecret={secret}")
		NutMap getAccessToken(@Param("appid") String corpid, @Param("secret") String secret);

		@RequestLine("POST /sns/get_persistent_code?access_token={access_token}")
		@Headers("Content-Type: application/json")
		@Body("{msg}")
		NutMap getPersistentCode(@Param("access_token") String access_token, @Param("msg") String msg);

		@RequestLine("POST /sns/get_sns_token?access_token={access_token}")
		@Headers("Content-Type: application/json")
		@Body("{msg}")
		NutMap getSnsToken(@Param("access_token") String access_token, @Param("msg") String msg);

		@RequestLine("GET /sns/getuserinfo?sns_token={sns_token}")
		NutMap getUserInfo(@Param("sns_token") String sns_token);

		default String refreshAccessToken(String appid,String secret) {
			NutMap result = getAccessToken(appid, secret);
			String token = result.getString("access_token");
			return token;
		}

		default NutMap getPersistentCode(String code) {
			return getPersistentCode(accessToken, Json.toJson(NutMap.NEW().addv("tmp_auth_code", code)));
		}

		default NutMap getDingUserInfo(String code) {
			NutMap params = getPersistentCode(code);
			params.remove("errcode");
			params.remove("errmsg");
			params.remove("unionid");
			String snsToken = getSnsToken(accessToken, Json.toJson(params)).getString("sns_token");
			NutMap userInfo = getUserInfo(snsToken);
			return userInfo.getAs("user_info", NutMap.class);
		}
	}

	public NutMap getPersistentCode(String code) {
		return this.dingOauth.getPersistentCode(code);
	}
	
	public String getToken() {
		return accessToken;
	}
	
	public NutMap getDingTalkUser(String code) {
		return dingOauth.getDingUserInfo(code);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String url = "https%3a%2f%2foapi.dingtalk.com%2fconnect%2foauth2%2fsns_authorize%3fappid%3ddingoa6lrest1tenlpvnks%26response_type%3dcode%26scope%3dsnsapi_login%26state%3dSTATE%26redirect_uri%3dhttp%3a%2f%2fadmin.kuaiqiangche.com%2ferp%2flogin%2funionDingding";
		System.out.println(URLDecoder.decode(url, "utf-8"));
		//String callback = "http://meal.rekoe.com/oauth/dingding/callback";
		//System.out.println(URLEncoder.encode(dingOauthService.DINGDING_URL + callback, "utf-8").toLowerCase());
	}

}
