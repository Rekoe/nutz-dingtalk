package com.rekoe.service;

import org.nutz.ioc.loader.annotation.IocBean;

import com.rekoe.core.bean.meal.MealSeller;

@IocBean(fields = { "dao" })
public class MealSellerService extends BaseService<MealSeller> {

}
