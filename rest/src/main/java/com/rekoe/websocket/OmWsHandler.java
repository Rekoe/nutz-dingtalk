package com.rekoe.websocket;

import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.mvc.websocket.handler.SimpleWsHandler;

import com.rekoe.common.shiro.OAuthToken;
import com.rekoe.core.bean.acl.User;

import redis.clients.jedis.Jedis;

public class OmWsHandler extends SimpleWsHandler {

	private PubSubService pubSubService;

	private JedisAgent jedisAgent;

	private Dao dao;

	public OmWsHandler(String prefix, JedisAgent jedisAgent, Dao dao) {
		super(prefix);
		this.jedisAgent = jedisAgent;
		this.dao = dao;
	}

	public PubSubService getPubSubService() {
		return pubSubService;
	}

	public void setPubSubService(PubSubService pubSubService) {
		this.pubSubService = pubSubService;
	}

	public void init() {
		super.init();
		if (httpSession != null)
			httpSession.setAttribute("wsid", session.getId());
	}

	@Override
	public void defaultAction(NutMap msg) {
		String channel = prefix + msg.getString("room");
		String text = msg.getString("text");
		endpoint.each(channel, (index, session, length) -> session.getAsyncRemote().sendText(getNickName(msg.getString("room"), false, session.getId()) + "说:" + text));
	}

	@Override
	public void join(NutMap msg) {
		super.join(msg);
		NutMap resp = new NutMap("action", "notify");
		resp.setv("msg", getNickName(msg.getString("room"), true, session.getId()) + "您好,欢迎回来");
		String channel = prefix + msg.getString("room");
		endpoint.each(channel, (index, session, length) -> session.getAsyncRemote().sendText(Json.toJson(resp)));
	}

	@Override
	public void left(NutMap msg) {
		super.left(msg);
	}

	private boolean isGuest() {
		if (Lang.isEmpty(httpSession)) {
			return true;
		}
		Object sessionObj = httpSession.getAttribute(NutShiro.SessionKey);
		if (Lang.isEmpty(sessionObj)) {
			return true;
		}
		return false;
	}

	public void sayhi(NutMap req) {
		String name = req.getString("name");
		NutMap resp = new NutMap("action", "notify");
		resp.setv("msg", "hi, " + name);
		endpoint.sendJson(session.getId(), resp);
	}

	private String getNickName(String room, boolean add, String wsid) {
		final String[] nackName = new String[] { "游客" };
		boolean isGuest = isGuest();
		if (isGuest) {
			return nackName[0] + getLoginNumber("guest");
		}
		Object sessionObj = httpSession.getAttribute(NutShiro.SessionKey);
		if (sessionObj instanceof String) {
			nackName[0] = sessionObj.toString();
		} else if (sessionObj instanceof Long) {
			User user = dao.fetch(User.class, Cnd.where("id", "=", sessionObj));
			nackName[0] = StringUtils.defaultString(user.getRealName(), user.getName());
		} else {
			nackName[0] = ((OAuthToken) sessionObj).getNickName();
		}
		return nackName[0];
	}

	private long getLoginNumber(String room) {
		try (Jedis jedis = jedisAgent.getResource()) {
			return jedis.incr("ig:" + room);
		}
	}
}
