package com.rekoe.module;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.meal.FoodCategory;
import com.rekoe.core.bean.meal.MealSeller;
import com.rekoe.service.FoodCategoryService;
import com.rekoe.service.MealSellerService;
import com.rekoe.service.entity.PageredData;

@IocBean
@Ok("json")
@At("/food/category")
public class FoodCategoryModule {

	@Inject
	private FoodCategoryService foodCategoryService;

	@Inject
	private MealSellerService mealSellerService;

	@At
	@NutzRequiresPermissions(value = "meal.food.category.list", name = "分类分类列表", tag = "订餐管理", enable = true)
	public Result list(@Param(value = "page", df = "1") int page, @Param(value = "seller", df = "0") int sellerId) {
		PageredData<NutMap> result = new PageredData<NutMap>();
		List<NutMap> dataList = new ArrayList<>();
		result.setDataList(dataList);
		PageredData<FoodCategory> data = foodCategoryService.searchByPage(page, sellerId == 0 ? Cnd.NEW().asc("id") : Cnd.where("sellerId", "=", sellerId).asc("id"));
		result.setPager(data.getPager());
		Lang.each(data.getDataList(), new Each<FoodCategory>() {
			@Override
			public void invoke(int index, FoodCategory ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				NutMap bean = Lang.obj2nutmap(ele);
				bean.remove("sellerId");
				MealSeller mealSeller = mealSellerService.fetch(Cnd.where("id", "=", ele.getSellerId()));
				bean.put("seller", mealSeller);
				dataList.add(bean);
			}
		});
		return Result.success().addData("pager", result);
	}

	@At
	@NutzRequiresPermissions(value = "meal.food.category.list", name = "商品分类列表", tag = "订餐管理", enable = false)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		PageredData<NutMap> result = new PageredData<NutMap>();
		List<NutMap> dataList = new ArrayList<>();
		result.setDataList(dataList);
		PageredData<FoodCategory> data = foodCategoryService.searchByKeyAndPage(key, page, "name").addParam("key", key);
		result.setPager(data.getPager());
		Lang.each(data.getDataList(), new Each<FoodCategory>() {
			@Override
			public void invoke(int index, FoodCategory ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				NutMap bean = Lang.obj2nutmap(ele);
				bean.remove("sellerId");
				MealSeller mealSeller = mealSellerService.fetch(Cnd.where("id", "=", ele.getSellerId()));
				bean.put("seller", mealSeller);
				dataList.add(bean);
			}
		});
		return Result.success().addData("pager", result);
	}

	@At
	@Ok("json")
	@NutzRequiresPermissions(value = "meal.food.category.edit", name = "分类编辑", tag = "订餐管理", enable = true)
	public Result status(@Param("id") long id) {
		FoodCategory foodCategory = foodCategoryService.fetch(id);
		if (Lang.isNotEmpty(foodCategory)) {
			boolean onSale = foodCategory.isOnSale();
			foodCategoryService.update(Chain.make("on_sale", !onSale), Cnd.where("id", "=", id));
			return Result.success();
		}
		return Result.fail("未找到数据");
	}

	@At
	@Ok("json")
	@NutzRequiresPermissions(value = "meal.food.category.edit", name = "分类编辑", tag = "订餐管理", enable = true)
	public Result type(@Param("id") long id, @Param("type") int type) {
		FoodCategory foodCategory = foodCategoryService.fetch(id);
		if (Lang.isNotEmpty(foodCategory)) {
			foodCategoryService.update(Chain.make("type", type), Cnd.where("id", "=", id));
			return Result.success();
		}
		return Result.fail("未找到数据");
	}
}
