package com.rekoe.core.dingtalk.chatbot.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Strings;

/**
 * Created by dustin on 2017/3/17.
 */
public class TextMessage implements Message {

	private String text;
	private List<String> atMobiles;
	private boolean isAtAll;

	private String agentid;
	private List<String> uids;

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public void setUids(List<String> uids) {
		this.uids = uids;
	}

	public TextMessage(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getAtMobiles() {
		return atMobiles;
	}

	public void setAtMobiles(List<String> atMobiles) {
		this.atMobiles = atMobiles;
	}

	public boolean isAtAll() {
		return isAtAll;
	}

	public void setIsAtAll(boolean isAtAll) {
		this.isAtAll = isAtAll;
	}

	public String toJsonString() {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put("msgtype", "text");

		Map<String, String> textContent = new HashMap<String, String>();
		if (Strings.isBlank(text)) {
			throw new IllegalArgumentException("text should not be blank");
		}
		textContent.put("content", text);
		items.put("text", textContent);

		Map<String, Object> atItems = new HashMap<String, Object>();
		if (atMobiles != null && !atMobiles.isEmpty()) {
			atItems.put("atMobiles", atMobiles);
		}
		if (isAtAll) {
			atItems.put("isAtAll", isAtAll);
		}
		items.put("at", atItems);
		if (Strings.isNotBlank(agentid)) {
			items.put("agentid", agentid);
			StringBuffer sb = new StringBuffer();
			for (String uid : uids) {
				sb.append(uid);
				sb.append("|");
			}
			sb.deleteCharAt(sb.length() - 1);
			items.put("touser", sb);
		}
		return Json.toJson(items);
	}
}
