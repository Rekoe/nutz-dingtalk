package com.rekoe.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Cnd;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.slog.annotation.Slog;

import com.rekoe.common.vo.GrantDTO;
import com.rekoe.common.vo.Result;
import com.rekoe.common.vo.UserLoginDto;
import com.rekoe.core.bean.acl.User;
import com.rekoe.service.PermissionService;
import com.rekoe.service.RoleService;
import com.rekoe.service.UserService;

@IocBean
@At({ "/user" })
@Ok("json")
public class UserModule {

	@Inject
	private UserService userService;

	/**
	 * 用户列表
	 * 
	 * @param page 页码
	 * @return
	 */
	@At
	@NutzRequiresPermissions(value = "user.list", name = "用户分页列表", tag = "用户管理", enable = true)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", userService.searchByPage(page, Cnd.NEW().desc("id")));
	}

	/**
	 * 
	 * @param key  检索条件
	 * @param page 页码
	 * @return
	 */
	@At
	@NutzRequiresPermissions(value = "user.list", name = "用户分页检索", tag = "用户管理", enable = false)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", userService.searchByKeyAndPage((key), (page), "name", "nickName", "realName").addParam("key", key));
	}

	/**
	 * 添加用户
	 * 
	 * @param user
	 * @return
	 */
	@POST
	@NutzRequiresPermissions(value = "user.add", name = "添加用户", tag = "用户管理", enable = true)
	@AdaptBy(type = JsonAdaptor.class)
	public Result add(User user, HttpServletRequest req) {
		return userService.add(user, Lang.getIP(req)) == null ? Result.fail("保存用户失败!") : Result.success().addData("user", user);
	}

	/**
	 * 编辑用户
	 * 
	 * @param user 待更新用户
	 * @return
	 */
	@POST
	@NutzRequiresPermissions(value = "user.edit", name = "编辑用户", tag = "用户管理", enable = true)
	@AdaptBy(type = JsonAdaptor.class)
	public Result edit(User user) {
		switch (user.getStatus()) {
		case ACTIVED:
			user.setLocked(false);
			break;
		default:
			user.setLocked(true);
			break;
		}
		return userService.update(user, "realName", "locked", "status") ? Result.success() : Result.fail("更新用户失败!");
	}

	/**
	 * 重置密码
	 * 
	 * @param user 用户信息
	 * @return
	 */
	@At
	@POST
	@RequiresUser
	public Result resetPassword(@Param("password") String password) {
		if (StringUtils.isBlank(password)) {
			return Result.fail("密码不可以为空!");
		}
		long uid = (long) SecurityUtils.getSubject().getPrincipal();
		boolean isRight = userService.resetPassword(uid, password);
		if (isRight) {
			return Result.success().addData("user", userService.fetch(uid));
		}
		return Result.fail("保存用户失败!");
	}

	@At
	@POST
	@RequiresUser
	public Result rebuilt(@Param("id") long id, @Param("password") String password) {
		if (StringUtils.isBlank(password)) {
			return Result.fail("密码不可以为空!");
		}
		boolean isRight = userService.resetPassword(id, password);
		if (isRight) {
			return Result.success().addData("user", userService.fetch(id));
		}
		return Result.fail("保存用户失败!");
	}

	/**
	 * 删除用户
	 * 
	 * @param id 用户id
	 * @return
	 */
	@At("/delete/?")
	@NutzRequiresPermissions(value = "user.delete", name = "删除用户", tag = "用户管理", enable = true)
	public Result delete(long id) {
		return userService.delete(id) == 1 ? Result.success() : Result.fail("删除用户失败!");
	}

	/**
	 * 用户详情
	 * 
	 * @param id 用户id
	 * @return
	 */
	@At("/detail/?")
	@NutzRequiresPermissions(value = "user.detail", name = "用户详情", tag = "用户管理", enable = true)
	public Result detail(long id) {
		return Result.success().addData("user", userService.fetch(id));
	}

	/**
	 * 获取用户的角色信息
	 * 
	 * @param id 用户id
	 * @return
	 */
	@At("/role/?")
	@NutzRequiresPermissions(value = "user.role", name = "用户角色授权信息", tag = "用户管理", enable = true)
	public Result roleInfo(long id) {
		return Result.success().addData("infos", roleService.getRoles(id));
	}

	/**
	 * 获取用户的权限信息
	 * 
	 * @param id 用户id
	 * @return
	 */
	@At("/permission/?")
	@NutzRequiresPermissions(value = "user.grant", name = "用户权限信息", tag = "用户管理", enable = true)
	public Result permissionInfo(long id) {
		return Result.success().addData("infos", roleService.getRoles(id));
	}

	/**
	 * 为用户设置角色
	 * 
	 * @param dto
	 * @return
	 */
	@POST
	@NutzRequiresPermissions(value = "user.role", name = "设置用户角色", tag = "用户管理", enable = false)
	@AdaptBy(type = JsonAdaptor.class)
	@At("/grant/role")
	public Result grantRole(GrantDTO dto) {
		return userService.setRole(dto.getGrantIds(), dto.getId());
	}

	@Inject
	private RoleService roleService;

	@Inject
	private PermissionService permissionService;

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	@Slog(tag = "账号登陆", after = "用户 ${args[0].userName}")
	public Result login(UserLoginDto userLoginDto, HttpSession session, HttpServletResponse resp, HttpServletRequest req) {
		if (Strings.equalsIgnoreCase(userLoginDto.getCaptcha(), Strings.safeToString(session.getAttribute("SINO_CAPTCHA"), ""))) {
			Result result = userService.login(userLoginDto.getUserName(), userLoginDto.getPassword(), Lang.getIP(req));
			if (result.isSuccess()) {
				// 登录成功处理
				session.setAttribute("SINO_USER_KEY", result.getData().get("loginUser"));
				session.setAttribute("me", SecurityUtils.getSubject().getPrincipal());
				if (userLoginDto.isRememberMe()) {
					NutMap data = NutMap.NEW();
					data.put("user", userLoginDto.getUserName());
					data.put("password", userLoginDto.getPassword());
					data.put("rememberMe", userLoginDto.getPassword());
				}
				return result.addData("roles", roleService.roleInfos(userLoginDto.getUserName())).addData("permissions", permissionService.permissionInfos(userLoginDto.getUserName()));
			}
			return result;
		} else {
			return Result.fail("验证码输入错误");
		}
	}

	@At
	@RequiresUser
	public Result load() {
		long uid = (long) SecurityUtils.getSubject().getPrincipal();
		User user = userService.fetch(uid);
		return Result.success().addData("loginUser", user).addData("roles", roleService.roleInfos(user.getName())).addData("permissions", permissionService.permissionInfos(user.getName()));
	}

	@At
	public Result logout() {
		SecurityUtils.getSubject().logout();
		return Result.success();
	}

	@At("/lang")
	@RequiresUser
	public Result setLang(@Param("lang") String lang, HttpSession session) {
		session.setAttribute("lang", lang);
		return Result.success();
	}
}
