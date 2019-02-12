package com.rekoe.service;

import org.nutz.ioc.loader.annotation.IocBean;

import com.rekoe.core.bean.meal.MealFood;

@IocBean(fields = { "dao" })
public class MealFoodService extends BaseService<MealFood>{

}
