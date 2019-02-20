package com.rekoe.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ShiroSessionListener implements SessionListener {

	@Override
	public void onStart(Session session) {
		System.err.println("ShiroSessionListener onStart : " + session.getId());
	}

	@Override
	public void onStop(Session session) {

	}

	@Override
	public void onExpiration(Session session) {

	}

}
