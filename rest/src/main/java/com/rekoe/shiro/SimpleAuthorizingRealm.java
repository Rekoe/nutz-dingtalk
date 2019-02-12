package com.rekoe.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.integration.shiro.AbstractSimpleAuthorizingRealm;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import com.rekoe.common.shiro.OAuthToken;
import com.rekoe.core.bean.acl.User;
import com.rekoe.service.PermissionService;
import com.rekoe.service.RoleService;
import com.rekoe.service.UserService;

@IocBean(name = "shiroRealm", create = "_init")
public class SimpleAuthorizingRealm extends AbstractSimpleAuthorizingRealm {

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	@Inject
	private PermissionService permissionService;

	@Inject
	private org.apache.shiro.cache.CacheManager shiroCacheManager;

	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof OAuthToken;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		long uid = (long) principals.getPrimaryPrincipal();
		User user = userService.fetch(uid);
		List<String> roleNameList = roleService.roleInfos(user.getName());
		List<String> permissionNames = new ArrayList<>();
		permissionNames.addAll(permissionService.permissionInfos(user.getName()));
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		auth.addRoles(roleNameList);
		auth.addStringPermissions(permissionNames);
		SecurityUtils.getSubject().getSession().setAttribute("lang", "zh");
		return auth;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		OAuthToken oauthToken = (OAuthToken) token;
		if (oauthToken.isLocked()) {
			throw Lang.makeThrow(LockedAccountException.class, "Account is locked.");
		}
		SimpleAuthenticationInfo account = new SimpleAuthenticationInfo(oauthToken.getUserid(), token, getName());
		return account;
	}

	public SimpleAuthorizingRealm() {
		this(null, null);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
		super(cacheManager, matcher);
		setAuthenticationTokenClass(OAuthToken.class);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager) {
		this(cacheManager, null);
	}

	public SimpleAuthorizingRealm(CredentialsMatcher matcher) {
		this(null, matcher);
	}

	public void _init() {
		setCacheManager(shiroCacheManager);
	}
}
