package com.rekoe.service;

import org.nutz.ioc.loader.annotation.IocBean;

import com.rekoe.core.bean.meal.FoodCategory;

@IocBean(fields = { "dao" })
public class FoodCategoryService extends BaseService<FoodCategory>{

}
