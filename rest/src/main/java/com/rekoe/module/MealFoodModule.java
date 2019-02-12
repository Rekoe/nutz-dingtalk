package com.rekoe.module;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.meal.MealFood;
import com.rekoe.service.MealFoodService;

@IocBean
@Ok("json")
@At("/meal/food")
public class MealFoodModule {

	@Inject
	private MealFoodService mealFoodService;

	@At
	@NutzRequiresPermissions(value = "meal.food.list", name = "菜单列表", tag = "订餐管理", enable = true)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", mealFoodService.searchByPage(page, Cnd.NEW().asc("id")));
	}

	@At
	@NutzRequiresPermissions(value = "meal.food.list", name = "菜单列表", tag = "订餐管理", enable = false)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", mealFoodService.searchByKeyAndPage(key, page, "name", "description").addParam("key", key));
	}

	@At
	@Ok("json")
	@NutzRequiresPermissions(value = "meal.food.edit", name = "菜单编辑", tag = "订餐管理", enable = true)
	public Result status(@Param("id") long id) {
		MealFood mealFood = mealFoodService.fetch(id);
		if (Lang.isNotEmpty(mealFood)) {
			boolean onSale = mealFood.isOnSale();
			mealFoodService.update(Chain.make("on_sale", !onSale), Cnd.where("id", "=", id));
			return Result.success();
		}
		return Result.fail("未找到数据");
	}
}
