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

import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.acl.Permission;
import com.rekoe.service.PermissionService;

@IocBean
@At("/permission")
@Ok("json")
public class PermissionModule {

	@Inject
	private PermissionService permissionService;

	/**
	 * 权限列表
	 * 
	 * @Param page
	 * @return
	 */
	@At
	@NutzRequiresPermissions(value = "permission.list", name = "权限列表", tag = "权限管理", enable = true)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", permissionService.searchByPage(page, Cnd.NEW().desc("id")));
	}

	/**
	 * 权限搜索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@At
	@NutzRequiresPermissions(value = "permission.list", name = "权限分页搜索", tag = "权限管理", enable = false)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", permissionService.searchByKeyAndPage(key, page, "name", "description").addParam("key", key));
	}

	/**
	 * 添加权限
	 * 
	 * @param Permission
	 * @return
	 */
	@POST
	@NutzRequiresPermissions(value = "permission.add", name = "添加权限", tag = "权限管理", enable = true)
	@AdaptBy(type = JsonAdaptor.class)
	public Result save(Permission permission) {
		return permissionService.save(permission) == null ? Result.fail("保存权限失败!") : Result.success().addData("permission", permission);
	}

	/**
	 * 权限详情
	 * 
	 * @param id
	 *            权限id
	 * @return
	 */
	@At("/detail/?")
	@NutzRequiresPermissions(value = "permission.edit", name = "编辑权限", tag = "权限管理", enable = true)
	public Result detail(long id) {
		return Result.success().addData("permission", permissionService.fetch(id));
	}

	/**
	 * 删除全新啊
	 * 
	 * @param id
	 *            权限id
	 * @return
	 */
	@At("/delete/?")
	@NutzRequiresPermissions(value = "permission.delete", name = "删除权限", tag = "权限管理", enable = true)
	public Result delete(@Param("id") long id) {
		return permissionService.delete(id) == 1 ? Result.success() : Result.fail("删除权限失败!");
	}

	/**
	 * 更新权限
	 * 
	 * @param Permission
	 * @return
	 */
	@POST
	@NutzRequiresPermissions(value = "permission.edit", name = "编辑权限", tag = "权限管理", enable = false)
	@AdaptBy(type = JsonAdaptor.class)
	public Result update(Permission permission) {
		return permissionService.updateIgnoreNull(permission) != 1 ? Result.fail("更新权限失败!") : Result.success().addData("permission", permission);
	}

}
