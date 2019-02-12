package com.rekoe.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.google.common.collect.Lists;
import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.acl.Role;
import com.rekoe.core.bean.acl.User;

/**
 * @author 科技㊣²º¹³ <br />
 *         2014年2月3日 下午4:48:45 <br />
 *         http://www.rekoe.com <br />
 *         QQ:5382211s
 */
@IocBean(fields = { "dao" })
public class UserService extends BaseService<User> {

	private final static Log log = Logs.get();

	public List<User> list() {
		return query(null, null);
	}

	public int update(User user) {
		return dao().update(user);
	}

	public void update(long uid, String password, boolean isLocked, Integer[] ids) {
		User user = fetch(uid);
		if (!Lang.isEmptyArray(ids)) {
			user.setRoles(dao().query(Role.class, Cnd.where("id", "in", ids)));
		}
		if (StringUtils.isNotBlank(password)) {
			String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
			user.setSalt(salt);
			user.setPassword(new Sha256Hash(password, salt, 1024).toBase64());
		}
		user.setLocked(isLocked);
		dao().updateWith(user, "roles");
	}

	public void updatePwd(Object uid, String password) {
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		dao().update(User.class, Chain.make("password", new Sha256Hash(password, salt).toBase64()).add("salt", salt), Cnd.where("id", "=", uid));
	}

	public User insert(User user) {
		user = dao().insert(user);
		return dao().insertRelation(user, "roles");
	}

	public User fetchByProviderid(String providerid, String openid) {
		return dao().fetch(User.class, Cnd.where("providerid", "=", providerid).and("openid", "=", openid));
	}

	public User view(Long id) {
		User user = fetch(id);
		dao().fetchLinks(user, null);
		return user;
	}

	public User fetchByName(String name) {
		User user = fetch(Cnd.where("name", "=", name));
		if (!Lang.isEmpty(user)) {
			dao().fetchLinks(user, null);
		}
		return user;
	}

	public List<String> getRoleNameList(User user) {
		List<String> roleNameList = Lists.newArrayList();
		Lang.each(user.getRoles(), new Each<Role>() {
			@Override
			public void invoke(int index, Role role, int length) throws ExitLoop, ContinueLoop, LoopException {
				roleNameList.add(role.getName());
			}
		});
		return roleNameList;
	}

	public void addRole(Long userId, Long roleId) {
		User user = fetch(userId);
		Role role = new Role();
		role.setId(roleId);
		user.setRoles(Lang.list(role));
		dao().insertRelation(user, "roles");
	}

	public void removeRole(Long userId, Long roleId) {
		dao().clear("system_user_role", Cnd.where("userid", "=", userId).and("roleid", "=", roleId));
	}

	public User initUser(String name, String openid, String providerid, String addr) {
		return dao().insert(initUser(name, openid, providerid, addr, true));
	}

	public User initUser(String name, String openid, String providerid, String addr, boolean locked) {
		return initUser(name, openid, providerid, addr, locked, R.UU32());
	}

	public User add(User user, String addr) {
		User temp = dao().fetch(getEntityClass(), Cnd.where("name", "=", user.getName()));
		if (!Lang.isEmpty(temp)) {
			user.setName(user.getName() + R.random(2, 5));
		}
		user.setCreateDate(Times.now());
		user.setOpenid(UUID.randomUUID().toString());
		user.setDescription("注册账号");
		user.setProviderid("local");
		switch (user.getStatus()) {
		case ACTIVED:
			user.setLocked(false);
			break;
		default:
			user.setLocked(true);
			break;
		}
		user.setSystem(false);
		user.setRegisterIp(addr);
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(user.getPassword(), salt).toBase64());
		return dao().insert(user);
	}

	public User initUser(String name, String openid, String providerid, String addr, boolean locked, String pwd) {
		User user = new User();
		user.setCreateDate(Times.now());
		user.setName(name);
		user.setRealName(name);
		user.setOpenid(openid);
		user.setDescription("OAuth Login");
		user.setProviderid(providerid);
		user.setRegisterIp(addr);
		user.setLocked(locked);
		user.setSystem(false);
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(pwd, salt).toBase64());
		return dao().insert(user);
	}

	public User fetchByOpenID(String openid) {
		User user = fetch(Cnd.where("openid", "=", openid));
		if (!Lang.isEmpty(user) && !user.isLocked()) {
			user = dao().fetchLinks(user, "roles");
		}
		return user;
	}

	public User createDingTalkUser(String openid, String name, String addr) {
		User user = new User();
		user.setCreateDate(Times.now());
		user.setName(name);
		user.setRealName(name);
		user.setOpenid(openid);
		user.setDescription("OAuth Login");
		user.setProviderid("DingTalk");
		user.setRegisterIp(addr);
		user.setLocked(true);
		user.setSystem(false);
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(R.UU16(), salt).toBase64());
		return dao().insert(user);
	}

	public User regist(User user, String addr) {
		user.setCreateDate(Times.now());
		user.setRegisterIp(addr);
		user.setSystem(false);
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		user.setSalt(salt);
		user.setPassword(new Sha256Hash(user.getPassword(), salt).toBase64());
		return dao().insert(user);
	}

	public void updateLock(User user) {
		dao().update(user, "^(locked)$");
	}

	public void loadRolePermission(User user) {
		List<Role> roleList = user.getRoles();
		dao().fetchLinks(roleList, "permissions");
	}

	public Result setRole(long[] ids, long uid) {
		dao().clear("system_user_role", Cnd.where("userid", "=", uid));
		List<Role> roles = dao().query(Role.class, Cnd.where("id", "in", ids));
		Lang.each(roles, new Each<Role>() {
			@Override
			public void invoke(int index, Role ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				dao().insert("system_user_role", Chain.make("userid", uid).add("roleid", ele.getId()));
			}
		});
		return Result.success();
	}

	public Result login(String userName, String password, String ip) {
		try {
			User user = fetch(Cnd.where("name", "=", userName));
			Sha256Hash hash = new Sha256Hash(password, user.getSalt());
			if (hash.toBase64().equals(user.getPassword())) {
				dao().fetchLinks(user, null);
				Subject subject = SecurityUtils.getSubject();
				SimpleShiroToken token = new SimpleShiroToken(user.getId());
				subject.login(token);
				return Result.success().addData("loginUser", user);
			}
		} catch (LockedAccountException e) {
			log.debug(e);
			return Result.fail("账户被锁定");
		} catch (Exception e) {
			log.debug(e);
		}
		return Result.fail("登录失败");
	}

	public boolean resetPassword(Object uid, String password) {
		String salt = new SecureRandomNumberGenerator().nextBytes().toBase64();
		String pwd = new Sha256Hash(password, salt).toBase64();
		int result = dao().update(User.class, Chain.make("salt", salt).add("password", pwd), Cnd.where("id", "=", uid));
		return result > 0;
	}
}
