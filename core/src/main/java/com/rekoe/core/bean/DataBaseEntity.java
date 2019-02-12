package com.rekoe.core.bean;

import org.nutz.castor.Castors;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

public class DataBaseEntity {

	public <T extends DataBaseEntity> T exchange(Class<T> clazz) {
		return Castors.me().castTo(this, clazz);

	}

	public String toString() {
		return Json.toJson(this, JsonFormat.compact().setQuoteName(true).setIgnoreNull(false));
	}

}
