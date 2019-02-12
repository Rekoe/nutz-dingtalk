package com.rekoe.module;

import java.util.ArrayList;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.rekoe.common.vo.Result;
import com.rekoe.service.entity.PageredData;

@IocBean
@Ok("json")
@At("/seller/order")
public class SellerOrderModule {

	@Inject
	private Dao dao;
	
	@At
	@NutzRequiresPermissions(value = "meal.order.list", name = "订单列表", tag = "订餐管理", enable = false)
	public Result list(@Param(value = "page", df = "1") int page) {
		String time = Times.format("yyyyMMdd", Times.now());
		PageredData<NutMap> result = new PageredData<NutMap>();
		List<NutMap> dataList = new ArrayList<>();
		result.setDataList(dataList);
		Cnd cnd = Cnd.where("mo.time", "=", time).and("mo.is_deleted", "=", false);
		Sql sql = Sqls.create("select * from meal_seller where id in(select mf.seller_id from meal_order_info as moi left join meal_food as mf on mf.id=moi.item_id left join meal_order as mo on mo.id=moi.meal_order_id $condition group by mf.seller_id)");
		sql.setCondition(cnd);
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		Pager pager = dao.createPager(page, 20);
		Sql sqlC = Sqls.create("select count(0) from meal_seller where id in(select mf.seller_id from meal_order_info as moi left join meal_food as mf on mf.id=moi.item_id left join meal_order as mo on mo.id=moi.meal_order_id $condition group by mf.seller_id)");
		sqlC.setCallback(Sqls.callback.integer());
		sqlC.setCondition(cnd);
		dao.execute(sqlC);
		pager.setRecordCount(sqlC.getInt(0));
		sql.setPager(pager);
		result.setPager(pager);
		Lang.each(sql.getList(org.nutz.dao.entity.Record.class), new Each<org.nutz.dao.entity.Record>() {
			@Override
			public void invoke(int index, org.nutz.dao.entity.Record ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				NutMap bean = Lang.obj2nutmap(ele);
				long id = bean.getLong("id");
				Sql sql = Sqls.create("select f.id,f.name,f.price,f.image,oi.number,oi.item_id,count(1) as num from meal_order_info as oi left join meal_food as f on f.id=oi.item_id left join meal_seller as s on s.id=f.seller_id left join meal_order as mo on mo.id=oi.meal_order_id $condition group by item_id");
				sql.setCallback(Sqls.callback.records());
				sql.setCondition(Cnd.where("s.id", "=", id).and("mo.time", "=", time).and("mo.is_deleted", "=", false));
				dao.execute(sql);
				bean.put("id", bean.getString("id"));
				bean.put("items", sql.getList(Record.class));
				dataList.add(bean);
			}
		});
		return Result.success().addData("pager", result);
	}
}
