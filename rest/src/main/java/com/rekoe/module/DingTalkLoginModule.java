package com.rekoe.module;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import com.rekoe.common.shiro.OAuthToken;
import com.rekoe.core.bean.acl.Role;
import com.rekoe.core.bean.acl.User;
import com.rekoe.core.bean.dingtalk.DingTalkUser;
import com.rekoe.service.DingOauthService;
import com.rekoe.service.DingTalkUserService;
import com.rekoe.service.RoleService;
import com.rekoe.service.UserService;

@IocBean
public class DingTalkLoginModule {

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	@Inject
	private PropertiesProxy conf;

	@Inject
	private DingOauthService dingOauthService;

	@Inject
	private DingTalkUserService dingTalkUserService;

	/**
	 * 统一的OAuth回调入口
	 */
	@At("/oauth/?/callback")
	@Ok(">>:/#/dashboard")
	public void callback(String _providerId, HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		Map<String, String> paramsMap = getRequestParametersMap(req);
		NutMap params = dingOauthService.getDingTalkUser(paramsMap.get("code"));
		String openid = params.getString("unionid");
		User user = userService.fetchByProviderid(_providerId, openid);
		if (Lang.isEmpty(user)) {
			user = userService.initUser(params.getString("nick"), openid, _providerId, Lang.getIP(req), false);
			Role role = roleService.fetch(Cnd.where("name", "=", "订餐查询"));
			userService.dao().insert("system_user_role", Chain.make("userid", user.getId()).add("roleid", role.getId()));
			DingTalkUser dingTalkUser = dingTalkUserService.fetch(openid);
			if (Lang.isEmpty(dingTalkUser)) {
				dingTalkUserService.insert(Chain.make("openId", openid).add("userid", "").add("name", params.getString("nick")).add("dingId", params.getString("dingId")));
			}
		}
		Subject subject = SecurityUtils.getSubject();
		subject.login(new OAuthToken(openid, user.getId(), user.isLocked(), params.getString("nick"), req.getRemoteHost()));
		session.setAttribute("me", subject.getPrincipal());
	}

	public Map<String, String> getRequestParametersMap(final HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		Map<String, String> paramsMap = new HashMap<String, String>();
		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			String key = entry.getKey();
			String values[] = entry.getValue();
			paramsMap.put(key, values[0].toString());
		}
		return paramsMap;
	}
}
