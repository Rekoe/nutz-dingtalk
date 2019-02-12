package com.rekoe.module;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.exception.SocialAuthException;
import org.brickred.socialauth.util.HttpUtil;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import com.rekoe.common.shiro.OAuthToken;
import com.rekoe.core.bean.acl.User;
import com.rekoe.service.UserService;

@IocBean(create = "init")
public class LoginModule {

	@Inject
	private PropertiesProxy conf;

	@Inject
	private UserService userService;

	/* 提供社会化登录 */
	@At("/login/?")
	@Ok("void")
	public void login(String provider, HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String returnTo = req.getRequestURL().toString() + "/callback";
		if (req.getParameterMap().size() > 0) {
			StringBuilder sb = new StringBuilder().append(returnTo).append("?");
			for (Object name : req.getParameterMap().keySet()) {
				sb.append(name).append('=').append(URLEncoder.encode(req.getParameter(name.toString()), Encoding.UTF8)).append("&");
			}
			returnTo = sb.toString();
		}
		SocialAuthManager manager = new SocialAuthManager(); // 每次都要新建哦
		manager.setSocialAuthConfig(config);
		String url = manager.getAuthenticationUrl(provider, returnTo);
		resp.setHeader("Location", url);
		resp.setStatus(302);
		session.setAttribute("openid_manager", manager);
	}

	@At("/login/?/callback")
	@Ok("void")
	public void returnPoint(String providerId, HttpServletRequest request, HttpServletResponse resopnse, HttpSession session) throws Exception {
		SocialAuthManager manager = (SocialAuthManager) session.getAttribute("openid_manager");
		if (manager == null)
			throw new SocialAuthException("Not manager found!");
		session.removeAttribute("openid_manager"); // 防止重复登录的可能性
		Map<String, String> paramsMap = SocialAuthUtil.getRequestParametersMap(request);
		AuthProvider provider = manager.connect(paramsMap);
		Profile p = provider.getUserProfile();
		String openid = p.getValidatedId();
		User user = userService.fetchByProviderid(provider.getProviderId(), openid);
		if (Lang.isEmpty(user)) {
			String name = StringUtils.defaultIfBlank(p.getDisplayName(), p.getFullName());
			user = userService.initUser(name, openid, provider.getProviderId(), Lang.getIP(request), false);
		}
		Subject subject = SecurityUtils.getSubject();
		subject.login(new OAuthToken(openid, user.getId(), user.isLocked(), openid, request.getRemoteHost()));
		resopnse.setHeader("Location", conf.get("om.wx", "http://wx.onemena.com.cn/") + "#/dashboard");
		resopnse.setStatus(302);
		session.setAttribute("me", subject.getPrincipal());
	}

	@Ok(">>:/login/logout")
	public void logout() {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
	}

	private SocialAuthConfig config;

	public void init() throws Exception {
		if (conf.getBoolean("ngrok.client.enable"))
			HttpUtil.setProxyConfig("127.0.0.1", 1087);
		SocialAuthConfig config = new SocialAuthConfig();
		config.load(getClass().getClassLoader().getResourceAsStream("oauth_consumer.properties"));
		this.config = config;
	}

}
