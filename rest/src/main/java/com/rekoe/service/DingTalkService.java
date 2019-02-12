package com.rekoe.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.nutz.boot.starter.feign.NutzJsonDecoder;
import org.nutz.boot.starter.feign.annotation.FeignInject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.rekoe.core.bean.acl.User;
import com.rekoe.core.bean.dingtalk.DingTalkUser;
import com.rekoe.core.dingtalk.chatbot.message.Message;
import com.rekoe.core.dingtalk.isv.message.Markdown;
import com.rekoe.core.dingtalk.isv.message.MarkdownMessage;
import com.rekoe.core.vo.DingDepartment;

import feign.Body;
import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@IocBean(create = "initRefreshTokenTimer")
public class DingTalkService {

	private final static Log log = Logs.get();

	@Inject
	private Dao dao;

	private static String accessToken;
	
	@Inject("java:$conf.get('dingtalk.agentid')")
	public String agentId;

	@Inject("java:$conf.get('dingtalk.corpid')")
	public String CORPID;

	@Inject("java:$conf.get('dingtalk.corpsecret')")
	private String CORPSECRET;

	public String markDownMsg(List<NutMap> data) {
		StringBuffer text = new StringBuffer();
		Lang.each(data, new Each<NutMap>() {
			@Override
			public void invoke(int arg0, NutMap map, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				String image = map.getString("image");
				String name = map.getString("name");
				int number = map.getInt("number");
				text.append(com.rekoe.core.dingtalk.chatbot.message.MarkdownMessage.getReferenceText(com.rekoe.core.dingtalk.chatbot.message.MarkdownMessage.getImageText(image)));
				text.append("\n");
				text.append(com.rekoe.core.dingtalk.chatbot.message.MarkdownMessage.getReferenceText(com.rekoe.core.dingtalk.chatbot.message.MarkdownMessage.getHeaderText(6, name + ", 数量: X" + number)));
				text.append("\n");
			}
		});
		text.append("exec:" + Times.sDTms(Times.now()));
		return text.toString();
	}

	public void sendMarkdownMessage(String toUser, Markdown markdown) {
		MarkdownMessage message = new MarkdownMessage();
		message.setAgentid(agentId);
		message.setTouser(toUser);
		message.setMsgtype("markdown");
		message.setMarkdown(markdown);
		this.sendMessage(Json.toJson(message));
	}

	interface DingTalk {
		@Body("{body}")
		@Headers("Content-Type: application/json")
		@RequestLine("POST /robot/send?access_token={access_token}")
		String robot(@Param("access_token") String access_token, @Param("body") String body);

		@RequestLine("GET /user/getuserinfo?access_token={access_token}&code={code}")
		NutMap getuserinfo(@Param("access_token") String access_token, @Param("code") String code);

		@RequestLine("GET /user/get?access_token={access_token}&userid={userid}")
		NutMap get(@Param("access_token") String access_token, @Param("userid") String userid);

		@RequestLine("GET /get_jsapi_ticket?access_token={access_token}")
		NutMap get_jsapi_ticket(@Param("access_token") String access_token);

		@Body("{msg}")
		@Headers("Content-Type: application/json")
		@RequestLine("POST /message/send?access_token={access_token}")
		String send(@Param("access_token") String access_token, @Param("msg") String msg);

		@RequestLine("GET /gettoken?corpid={corpid}&corpsecret={secret}")
		NutMap refreshAccessToken(@Param("corpid") String corpid, @Param("secret") String secret);

		@RequestLine("GET /department/list?access_token={access_token}")
		NutMap departmentList(@Param("access_token") String access_token);

		@RequestLine("GET /user/list?access_token={access_token}&department_id={department_id}")
		NutMap dingUserList(@Param("access_token") String access_token, @Param("department_id") int department_id);

		default void sendDingDingMsg(String agentId, String touser, String title, String content) {
			String body = Json.toJson(NutMap.NEW().addv("touser", touser).addv("agentid", agentId).addv("msgtype", "text").addv("text", NutMap.NEW().addv("title", title).addv("content", content)));
			send(accessToken, body);
		}

		default void sendDingDingMsgByMarkDown(String agentId, String touser, String title, String content) {
			String body = Json.toJson(NutMap.NEW().addv("touser", touser).addv("agentid", agentId).addv("msgtype", "markdown").addv("markdown", NutMap.NEW().addv("title", title).addv("text", content)));
			send(accessToken, body);
		}

		default void sendDingDingMsg(List<String> uids, Message message) {
			String json = message.toJsonString();
			send(accessToken, json);
		}

		default NutMap get_jsapi_ticket() {
			return get_jsapi_ticket(accessToken);
		}

		default void robotDingDingMsgByMarkDown(String token, Message message) {
			robot(token, message.toJsonString());
		}

		default String sendMessage(String message) {
			return send(accessToken, message);
		}

		default void refreshAccessToken1(String corpid, String corpsecret) {
			try {
				accessToken = refreshAccessToken(corpid, corpsecret).getString("access_token");
			} catch (Exception e) {
				accessToken = "846f9b5ac8f53bd9b2c5a196790271f2";
				log.error(e);
			}
			System.out.println("refreshAccessToken:" + accessToken);
		}

		default List<NutMap> dingUserList(int department_id) {
			return dingUserList(accessToken, department_id).getList("userlist", NutMap.class);
		}

		default NutMap getuserinfo(String code) {
			return getuserinfo(accessToken, code);
		}

		default NutMap get(String userid) {
			return get(accessToken, userid);
		}

		default List<DingDepartment> departmentList() {
			return departmentList(accessToken).getList("department", DingDepartment.class);
		}
	}

	public void initRefreshTokenTimer() {
		Timer refreshTimer = new Timer();
		refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				dingTalk.refreshAccessToken1(CORPID, CORPSECRET);
			}
		}, 0, 60 * 60 * 1000);
	}

	public NutMap getuserinfo(String code) {
		return dingTalk.getuserinfo(code);
	}

	public NutMap get(String userid) {
		return dingTalk.get(userid);
	}

	public void sendDingDingMsg(List<String> uids, Message message) {
		this.dingTalk.sendDingDingMsg(uids, message);
	}

	public void sendMessage(String message) {
		this.dingTalk.sendMessage(message);
	}

	public void sendDingTalkMsg(List<String> uids, String title, Date data, String text) {
		StringBuffer sb = new StringBuffer();
		for (String uid : uids) {
			sb.append(uid);
			sb.append("|");
		}
		sb.deleteCharAt(sb.length() - 1);
		if (log.isDebugEnabled()) {
			log.debug(sb.toString());
		}
		this.dingTalk.sendDingDingMsg(agentId, sb.toString(), title, text + "\n" + Times.sDT(data));
	}

	public void sendDingTalkMsg(String dUser, String title, Date data, String text) {
		StringBuffer sb = new StringBuffer(dUser);
		sb.append("|");
		sb.deleteCharAt(sb.length() - 1);
		if (log.isDebugEnabled()) {
			log.debug(sb.toString());
		}
		this.dingTalk.sendDingDingMsg(agentId, sb.toString(), title, text + "\n" + Times.sDT(data));
	}

	public void sendDingTalkMarkDownMsg(List<String> uids, String title, String text) {
		StringBuffer sb = new StringBuffer();
		for (String uid : uids) {
			sb.append(uid);
			sb.append("|");
		}
		sb.deleteCharAt(sb.length() - 1);
		if (log.isDebugEnabled()) {
			log.debug(sb.toString());
		}
		this.dingTalk.sendDingDingMsgByMarkDown(agentId, sb.toString(), title, text + "\n" + Times.sDT(Times.now()));
	}

	@FeignInject
	protected DingTalk dingTalk;

	public void newDingTalk() {
		this.dingTalk = Feign.builder().decoder(new NutzJsonDecoder()).target(DingTalk.class, "https://oapi.dingtalk.com");
	}

	public void robotDingDingMsgByMarkDown(String token, Message message) {
		dingTalk.robotDingDingMsgByMarkDown(token, message);
	}

	public void robotDingDingMsgByMarkDown(List<String> uids, Message message) {
		dingTalk.sendDingDingMsg(uids, message);
	}

	public void loadDingUser() {
		Map<String, DingTalkUser> dingTalkUsers = new HashMap<>();
		dao.each(DingTalkUser.class, null, new Each<DingTalkUser>() {
			@Override
			public void invoke(int index, DingTalkUser user, int length) throws ExitLoop, ContinueLoop, LoopException {
				dingTalkUsers.put(user.getOpenId(), user);
			}
		});
		List<DingDepartment> departmentList = this.dingTalk.departmentList();
		Map<String, NutMap> users = new HashMap<>();
		for (DingDepartment department : departmentList) {
			List<NutMap> maps = this.dingTalk.dingUserList(department.getId());
			for (NutMap map : maps) {
				users.put(map.getString("userid"), map);
			}
		}
		Lang.each(users.values(), new Each<NutMap>() {
			@Override
			public void invoke(int index, NutMap param, int length) throws ExitLoop, ContinueLoop, LoopException {
				String openId = param.getString("unionid");
				DingTalkUser user = dingTalkUsers.get(openId);
				if (Lang.isNotEmpty(user) && user.isActive()) {
					Chain chain = Chain.make("active", param.getBoolean("active"));
					String avatar = param.getString("avatar");
					if (StringUtils.isNotBlank(avatar)) {
						chain.add("avatar", avatar);
					}
					if (StringUtils.isBlank(user.getUserid())) {
						chain.add("userid", param.getString("userid"));
					}
					dao.update(DingTalkUser.class, chain, Cnd.where("openId", "=", openId));
					if (!param.getBoolean("active")) {
						dao.update(User.class, Chain.make("locked", true), Cnd.where("openid", "=", openId));
					}
				}
			}
		});
	}

	public NutMap getJsapiTicket() {
		return this.dingTalk.get_jsapi_ticket();
	}
}
