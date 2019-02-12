package com.rekoe.module;

import org.nutz.dao.Cnd;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import com.rekoe.common.vo.GrantDTO;
import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.acl.Role;
import com.rekoe.service.RoleService;

@IocBean
@At("/role")
@Ok("json")
public class RoleModule {

	@Inject
	private RoleService roleService;

	/**
	 * 列表
	 * 
	 * @param page
	 *            页码
	 * @return
	 */
	@At
	@NutzRequiresPermissions(value = "role.list", name = "角色列表", tag = "角色管理", enable = true)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", roleService.searchByPage(page, Cnd.NEW().desc("id")));
	}

	/**
	 * 搜索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@At
	@NutzRequiresPermissions(value = "role.list", name = "角色分页搜索", tag = "角色管理", enable = false)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", roleService.searchByKeyAndPage(key, page, "name", "description").addParam("key", key));
	}

	/**
	 * 添加角色
	 * 
	 * @param role
	 * @return
	 */
	@POST
	@NutzRequiresPermissions(value = "role.add", name = "添加角色", tag = "角色管理", enable = true)
	@AdaptBy(type = JsonAdaptor.class)
	public Result add(Role role) {
		return roleService.save(role) == null ? Result.fail("保存角色失败!") : Result.success().addData("role", role);
	}

	/**
	 * 角色详情
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@At("/detail/?")
	@NutzRequiresPermissions(value = "role.edit", name = "角色详情", tag = "角色管理", enable = true)
	public Result detail(long id) {
		return Result.success().addData("role", roleService.fetch(id));
	}

	/**
	 * 删除角色
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@At("/delete/?")
	@NutzRequiresPermissions(value = "role.delete", name = "删除角色", tag = "角色管理", enable = true)
	public Result delete(long id) {
		return roleService.delete(id) == 1 ? Result.success() : Result.fail("删除角色失败!");
	}

	/**
	 * 更新角色
	 * 
	 * @param role
	 *            待更新角色
	 * @return
	 */
	@POST
	@NutzRequiresPermissions(value = "role.edit", name = "编辑角色", tag = "角色管理", enable = false)
	@AdaptBy(type = JsonAdaptor.class)
	public Result update(Role role) {
		return roleService.updateIgnoreNull(role) != 1 ? Result.fail("更新角色失败!") : Result.success().addData("role", role);
	}

	/**
	 * 获取角色的权限设置信息
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@At("/permission/?")
	@NutzRequiresPermissions(value = "role.grant", name = "获取角色的授权信息", tag = "角色管理", enable = true)
	public Result permissionInfo(long id) {
		return Result.success().addData("infos", roleService.getPermissions(id));
	}

	/**
	 * 设置角色的权限
	 * 
	 * @param dto
	 * @return
	 */
	@At("/grant")
	@POST
	@NutzRequiresPermissions(value = "role.grant", name = "为角色授权", tag = "角色管理", enable = false)
	@AdaptBy(type = JsonAdaptor.class)
	public Result grantRole(GrantDTO dto) {
		return roleService.setPermission(dto.getGrantIds(), dto.getId());
	}

}
