package com.rekoe.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.nutz.castor.Castors;
import org.nutz.integration.shiro.AbstractSimpleAuthorizingRealm;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import com.rekoe.core.bean.acl.User;
import com.rekoe.service.PermissionService;
import com.rekoe.service.RoleService;
import com.rekoe.service.UserService;

@IocBean(create = "_init")
public class LoginRealm extends AbstractSimpleAuthorizingRealm {

	@Inject
	private UserService userService;


	@Inject
	private org.apache.shiro.cache.CacheManager shiroCacheManager;

	public void _init() {
		setCacheManager(shiroCacheManager);
	}


	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof SimpleShiroToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		SimpleShiroToken upToken = (SimpleShiroToken) token;
		Object obj = upToken.getPrincipal();
		Long uid = Castors.me().castTo(obj, Long.class);
		User user = userService.fetch(uid);
		if (Lang.isEmpty(user))// 用户不存在
			throw new UnknownAccountException("Account [" + user.getName() + "] is Unknown.");
		if (user.isLocked())// 用户被锁定
			throw new LockedAccountException("Account [" + user.getName() + "] is locked.");
		SecurityUtils.getSubject().getSession().setAttribute("lang", "zh");
		ByteSource salt = ByteSource.Util.bytes(user.getSalt());
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(uid, user.getPassword(), getName());
		info.setCredentialsSalt(salt);
		return info;
	}

	@Inject
	private RoleService roleService;

	@Inject
	private PermissionService permissionService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		Object obj = principalCollection.getPrimaryPrincipal();
		Long uid = Castors.me().castTo(obj, Long.class);
		User user = userService.fetch(uid);
		List<String> roleNameList = roleService.roleInfos(user.getName());
		List<String> permissionNames = new ArrayList<>();
		permissionNames.addAll(permissionService.permissionInfos(user.getName()));
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		auth.addRoles(roleNameList);// 添加角色
		auth.addStringPermissions(permissionNames);// 添加权限
		return auth;
	}
}
