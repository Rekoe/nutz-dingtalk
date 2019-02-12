package com.rekoe.core.dingtalk.chatbot.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Strings;

/**
 * Created by dustin on 2017/3/19.
 */
public class FeedCardMessage implements Message {

	private List<FeedCardMessageItem> feedItems;

	public List<FeedCardMessageItem> getFeedItems() {
		return feedItems;
	}

	public void setFeedItems(List<FeedCardMessageItem> feedItems) {
		this.feedItems = feedItems;
	}

	public String toJsonString() {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put("msgtype", "feedCard");

		Map<String, Object> feedCard = new HashMap<String, Object>();

		if (feedItems == null || feedItems.isEmpty()) {
			throw new IllegalArgumentException("feedItems should not be null or empty");
		}
		for (FeedCardMessageItem item : feedItems) {
			if (Strings.isBlank(item.getTitle())) {
				throw new IllegalArgumentException("title should not be blank");
			}
			if (Strings.isBlank(item.getMessageURL())) {
				throw new IllegalArgumentException("messageURL should not be blank");
			}
			if (Strings.isBlank(item.getPicURL())) {
				throw new IllegalArgumentException("picURL should not be blank");
			}
		}
		feedCard.put("links", feedItems);
		items.put("feedCard", feedCard);
		return Json.toJson(items);
	}
}
