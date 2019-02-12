package com.rekoe.service;

import java.util.List;
import java.util.Map;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.acl.Permission;
import com.rekoe.core.bean.acl.Role;
import com.rekoe.core.bean.acl.User;

/**
 * @author 科技㊣²º¹³M<br/>
 *         2014年2月3日 下午4:48:45 <br/>
 *         http://www.rekoe.com <br/>
 *         QQ:5382211
 */
@IocBean(fields = { "dao" })
public class RoleService extends BaseService<Role> {

	public List<Role> list() {
		return query(null, null);
	}

	public Role insert(Role role) {
		role = dao().insert(role);
		return dao().insertRelation(role, "permissions");
	}

	public void delete(Long id) {
		dao().delete(Role.class, id);
		dao().clear("system_role_permission", Cnd.where("roleid", "=", id));
		dao().clear("system_user_role", Cnd.where("roleid", "=", id));
	}

	public Role view(Long id) {
		return dao().fetchLinks(fetch(id), "permissions");
	}

	public int update(Role role) {
		return dao().update(role);
	}

	public Role fetchByName(String name) {
		return fetch(Cnd.where("name", "=", name));
	}

	public List<String> getPermissionNameList(Role role) {
		List<String> permissionNameList = Lists.newArrayList();
		Lang.each(role.getPermissions(), new Each<Permission>() {
			@Override
			public void invoke(int index, Permission permission, int length) throws ExitLoop, ContinueLoop, LoopException {
				permissionNameList.add(permission.getName());
			}
		});
		return permissionNameList;
	}

	public void updateRoleRelation(Role role, List<Permission> perms) {
		dao().clearLinks(role, "permissions");
		dao().update(role);
		if (Lang.isNotEmpty(role.getPermissions())) {
			role.getPermissions().clear();
		}
		if (Lang.isNotEmpty(perms)) {
			Lang.each(perms, new Each<Permission>() {
				@Override
				public void invoke(int index, Permission ele, int length) throws ExitLoop, ContinueLoop, LoopException {
					addPermission(role.getId(), ele.getId());
				}
			});
			role.setPermissions(perms);
		}
	}

	public Map<Long, String> map() {
		Map<Long, String> map = Maps.newHashMap();
		dao().each(getEntityClass(), Cnd.orderBy().desc("id"), new Each<Role>() {
			@Override
			public void invoke(int index, Role role, int length) throws ExitLoop, ContinueLoop, LoopException {
				map.put(role.getId(), role.getName());
			}
		});
		return map;
	}

	private void addPermission(Long roleId, Long permissionId) {
		dao().insert("system_role_permission", Chain.make("roleid", roleId).add("permissionid", permissionId));
	}

	public List<Role> loadRoles(Integer[] ids) {
		return dao().query(getEntityClass(), Cnd.where("id", "in", ids));
	}

	public Result setPermission(long[] ids, long roleId) {
		dao().clear("system_role_permission", Cnd.where("roleid", "=", roleId));
		dao().each(Permission.class, Cnd.where("id", "in", ids), new Each<Permission>() {
			@Override
			public void invoke(int index, Permission ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				addPermission(roleId, ele.getId());
			}
		});
		return Result.success();
	}

	public List<String> roleInfos(String userName) {
		final List<String> target = Lists.newArrayList();
		Lang.each(roles(userName), new Each<Role>() {
			@Override
			public void invoke(int arg0, Role ele, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				target.add(ele.getName());
			}
		});
		return target;
	}

	public List<Role> roles(String name) {
		User user = dao().fetch(User.class, Cnd.where("name", "=", name));
		if (!Lang.isEmpty(user)) {
			user = dao().fetchLinks(user, "roles");
			return user.getRoles();
		}
		return Lists.newArrayList();
	}

	public List<Record> getRoles(long userid) {
		Sql sql = Sqls.create("SELECT r.id,r.description as description,CASE sur.userid IS NULL WHEN 1 THEN FALSE ELSE TRUE END AS selected FROM system_role r LEFT JOIN (SELECT * FROM system_user_role WHERE userid = @id) sur ON r.id = sur.roleid");
		sql.setCallback(Sqls.callback.records());
		sql.params().set("id", userid);
		dao().execute(sql);
		return sql.getList(Record.class);
	}

	public List<Record> getPermissions(long id) {
		Sql sql = Sqls.create("SELECT p.id,p.description AS description,CASE srp.roleid IS NULL WHEN 1 THEN FALSE ELSE TRUE END AS selected FROM system_permission p LEFT JOIN(SELECT * FROM system_role_permission WHERE roleid =@id) srp ON p.id = srp.permissionid");
		sql.setCallback(Sqls.callback.records());
		sql.params().set("id", id);
		dao().execute(sql);
		return sql.getList(Record.class);
	}
}
