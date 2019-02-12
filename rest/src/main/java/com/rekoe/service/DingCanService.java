package com.rekoe.service;

import java.util.List;

import org.nutz.el.opt.RunMethod;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import xyz.downgoon.snowflake.Snowflake;

@IocBean
public class DingCanService implements RunMethod {

	@Inject
	private Snowflake snowflake;

	@Override
	public Object run(List<Object> fetchParam) {
		return next();
	}

	private long next() {
		return snowflake.nextId();
	}

	@Override
	public String fetchSelf() {
		return "ig";
	}

}
