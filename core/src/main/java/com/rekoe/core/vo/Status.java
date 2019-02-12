package com.rekoe.core.vo;

public enum Status {
	ACTIVED("正常"), DISABLED("禁用"), ALL("全部"), CANCELED("取消"), FULFILLMENT("已发货️"), OK("已确认️");

	/**
	 * 中文描述,主要用于页面展示
	 */
	private String name;

	private Status(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
