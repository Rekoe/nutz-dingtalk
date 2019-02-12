package com.rekoe.service;

import java.util.List;

import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Times;

import com.rekoe.core.bean.meal.MealOrder;

@IocBean(fields = { "dao" })
public class MealOrderService extends BaseService<MealOrder> {

	public List<Record> orderInfo(String userid) {
		String time = Times.format("yyyyMMdd", Times.now());
		Sql sql = Sqls.createf("select f.name,i.item_id,f.price,i.number from meal_order as o left join meal_order_info as i on i.meal_order_id=o.id left join meal_food as f on f.id=i.item_id where o.ding_talk_id='%s' and o.time='%s' and o.is_deleted=0", userid, time);
		sql.setCallback(Sqls.callback.records());
		dao().execute(sql);
		return sql.getList(Record.class);
	}
}
