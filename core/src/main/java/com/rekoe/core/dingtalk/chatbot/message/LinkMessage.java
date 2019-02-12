package com.rekoe.core.dingtalk.chatbot.message;

import java.util.HashMap;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Strings;

/**
 * Created by dustin on 2017/3/18.
 */
public class LinkMessage implements Message {

	private String title;
	private String text;
	private String picUrl;
	private String messageUrl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getMessageUrl() {
		return messageUrl;
	}

	public void setMessageUrl(String messageUrl) {
		this.messageUrl = messageUrl;
	}

	public String toJsonString() {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put("msgtype", "link");

		Map<String, String> linkContent = new HashMap<String, String>();
		if (Strings.isBlank(title)) {
			throw new IllegalArgumentException("title should not be blank");
		}
		linkContent.put("title", title);

		if (Strings.isBlank(messageUrl)) {
			throw new IllegalArgumentException("messageUrl should not be blank");
		}
		linkContent.put("messageUrl", messageUrl);

		if (Strings.isBlank(text)) {
			throw new IllegalArgumentException("text should not be blank");
		}
		linkContent.put("text", text);

		if (Strings.isNotBlank(picUrl)) {
			linkContent.put("picUrl", picUrl);
		}
		items.put("link", linkContent);
		return Json.toJson(items);
	}
}
