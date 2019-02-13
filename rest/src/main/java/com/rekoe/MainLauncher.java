package com.rekoe;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.nutz.boot.NbApp;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.el.opt.custom.CustomMake;
import org.nutz.img.Images;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Tasks;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.event.EventBus;
import org.nutz.resource.Scans;

import com.rekoe.common.shiro.OAuth2SubjectFactory;
import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.acl.Permission;
import com.rekoe.core.bean.acl.Role;
import com.rekoe.core.bean.acl.User;
import com.rekoe.job.NotificationMealJob;
import com.rekoe.job.ReportMealJob;
import com.rekoe.service.DingCanService;
import com.rekoe.service.DingOauthService;

import club.zhcs.captcha.DefaultCaptchaGener;
import club.zhcs.captcha.ImageVerification;
import xyz.downgoon.snowflake.Snowflake;

/**
 * http://ip.nutz.cn/city?ip=117.100.119.85
 */
@IocBean(create = "init")
@IocBy(args = { "*slog", "*org.nutz.plugins.event.EventIocLoader" })
public class MainLauncher {

	private final static Log log = Logs.get();

	@Inject
	protected PropertiesProxy conf;

	@Inject
	private Dao dao;

	@At({ "/" })
	@Ok("->:/index.html")
	public void index() {
	}

	@GET
	@Ok("void")
	@At("/image/avatar")
	public void avatar(@Param(value = "id", df = "0") long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Images.write(Images.createAvatar(new DefaultCaptchaGener("NUTZONEKEY").gen(3)), "png", response.getOutputStream());
	}

	@At({ "/captcha", "/api/captcha" })
	public void captcha(@Param(value = "length", df = "4") int length, HttpServletResponse resp, HttpSession session) throws IOException {
		resp.setContentType("image/jpeg");
		resp.setHeader("Pragma", "No-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		OutputStream out = resp.getOutputStream();
		// 输出图象到页面
		ImageVerification iv = new ImageVerification();
		if (length != 0) {
			iv.setIMAGE_VERIFICATION_LENGTH(length);
		}
		if (ImageIO.write(iv.creatImage(), "JPEG", out)) {
			log.debug("写入输出流成功:" + iv.getVerifyCode() + ".");
		} else {
			log.debug("写入输出流失败:" + iv.getVerifyCode() + ".");
		}
		session.setAttribute("SINO_CAPTCHA", iv.getVerifyCode());
		out.flush();
		out.close();
	}

	@Inject
	private DefaultWebSecurityManager shiroWebSecurityManager;

	@Inject
	private DefaultWebSessionManager shiroWebSessionManager;

	@Inject
	private EventBus eventBus;

	@Inject
	private CookieRememberMeManager shiroRememberMeManager;

	@IocBean(name = "snowflake")
	public Snowflake createSnowflake() {
		return new Snowflake(1, 1);
	}

	@Inject
	private DingCanService dingCanService;

	@Inject
	private DingOauthService dingOauthService;

	@Inject
	private ReportMealJob reportMealJob;

	public void init() {
		Sql sql = Sqls.create("set @@GLOBAL.sql_mode=''");
		Sql sql2 = Sqls.create("set sql_mode ='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION'");
		sql.forceExecQuery();
		sql2.forceExecQuery();
		dao.execute(sql, sql2);
		shiroWebSessionManager.setDeleteInvalidSessions(true);
		NutShiro.DefaultUnauthorizedAjax = Json.fromJson(NutMap.class, Result.fail("无此授权").toString());
		NutShiro.DefaultUnauthenticatedAjax = NutShiro.DefaultUnauthorizedAjax;
		shiroWebSecurityManager.setSubjectFactory(new OAuth2SubjectFactory());
		Daos.createTablesInPackage(dao, "com.rekoe.core.bean", false);
		Daos.migration(dao, "com.rekoe.core.bean", true, true);
		List<Class<?>> clazzs = Scans.me().scanPackage("com.rekoe.module");
		CustomMake.me().register("ig", dingCanService);
		List<Role> roles = dao.query(Role.class, null);
		List<Permission> premissions = dao.query(Permission.class, null);
		if (Lang.isEmpty(roles)) {
			dao.insert(new Permission("*", "超级权限", true));
		}
		boolean premInit = Lang.isEmpty(premissions);
		List<Permission> newPremissions = new ArrayList<>();

		Lang.each(clazzs, new Each<Class<?>>() {

			@Override
			public void invoke(int index, Class<?> clazz, int length) throws ExitLoop, ContinueLoop, LoopException {
				Method[] methods = clazz.getMethods();
				Lang.each(methods, new Each<Method>() {

					@Override
					public void invoke(int index, Method ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						NutzRequiresPermissions nutzRequiresPermissions = ele.getAnnotation(NutzRequiresPermissions.class);
						if (Lang.isEmpty(nutzRequiresPermissions)) {
							return;
						}
						boolean enable = nutzRequiresPermissions.enable();
						if (enable) {
							if (nutzRequiresPermissions.value().length == 1) {
								Permission perm = new Permission(nutzRequiresPermissions.value()[0], nutzRequiresPermissions.name(), false);
								if (premissions.contains(perm)) {
									return;
								}
								if (newPremissions.contains(perm)) {
									return;
								}
								newPremissions.add(perm);
							}
						}
					}
				});
			}
		});
		if (!Lang.isEmpty(newPremissions)) {
			dao.fastInsert(newPremissions);
		}

		if (Lang.isEmpty(roles)) {
			Role role = new Role();
			role.setName("admin");
			role.setDescription("超级管理组");
			role.setLock(true);
			role.setPermissions(dao.query(Permission.class, null));
			roles.add(dao.insert(role));
			dao.insertRelation(role, "permissions");
		}
		if (dao.count(User.class) == 0) {
			User user = new User();
			user.setCreateDate(Times.now());
			user.setDescription("超级管理员");
			user.setLocked(false);
			user.setName("admin");
			user.setSystem(true);
			user.setRegisterIp("127.0.0.1");
			user.setProviderid("local");
			String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
			user.setSalt(salt);
			Sha256Hash hash = new Sha256Hash("12345678", user.getSalt());
			user.setPassword(hash.toBase64());
			user.setRoles(roles);
			user = dao.insert(user);
			dao.insertRelation(user, "roles");
		}
		if (premInit) {
			Permission permission = dao.fetch(Permission.class, Cnd.where("name", "=", "meal.order.list"));
			Role role = dao.insert(new Role("订餐查询", "查询订单详情", true));
			dao.insert("system_role_permission", Chain.make("permissionid", permission.getId()).add("roleid", role.getId()));
		}
		// dao.update(MealFood.class, Chain.make("onSale", false), Cnd.where("price",
		// ">", 20).or("price", "=", 0F));
		Tasks.scheduleAtFixedTime(new Runnable() {
			@Override
			public void run() {
				
			}
		}, Times.nextMinute(new Date(), 1));
	}

	@Inject
	private NotificationMealJob notificationMealJob;

	public static void main(String[] args) throws Exception {
		new NbApp(MainLauncher.class).run();
	}
}
