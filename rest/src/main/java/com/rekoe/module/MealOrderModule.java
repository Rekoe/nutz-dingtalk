package com.rekoe.module;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.rekoe.common.vo.Result;
import com.rekoe.service.MealOrderService;
import com.rekoe.service.entity.PageredData;

@IocBean
@Ok("json")
@At("/meal/order")
public class MealOrderModule {

	@Inject
	private MealOrderService mealOrderService;

	@Inject
	private Dao dao;

	@At
	@NutzRequiresPermissions(value = "meal.order.list", name = "订单列表", tag = "订餐管理", enable = true)
	public Result list(@Param(value = "page", df = "1") int page, @Attr(scope = Scope.SESSION, value = "me") long uid) {
		Sql sqlu = Sqls.create("select u.is_system,d.userid,d.user_province from system_user as u left join ding_talk_user as d on d.open_id=u.openid $condition");
		sqlu.setCallback(Sqls.callback.record());
		sqlu.setCondition(Cnd.where("u.id", "=", uid));
		dao.execute(sqlu);
		Record record = sqlu.getObject(Record.class);
		String userProvince = record.getString("user_province");
		boolean isAdmin = BooleanUtils.toBoolean(record.getInt("is_system"));
		PageredData<NutMap> result = new PageredData<NutMap>();
		List<NutMap> dataList = new ArrayList<>();
		result.setDataList(dataList);
		String time = Times.format("yyyyMMdd", Times.now());
		result.setDataList(dataList);
		Cnd cnd = Cnd.where("o.time", "=", time).and("o.is_deleted", "=", false);
		if (isAdmin) {

		} else if (StringUtils.isNotBlank(record.getString("userid"))) {
			cnd.and("u.userid", "=", record.getString("userid"));
		} else {
			return Result.success().addData("pager", result);
		}
		if (StringUtils.isNotBlank(userProvince)) {
			cnd.and("u.user_province", "=", userProvince);
		}
		Sql sql = Sqls.create("select o.*,u.name,u.user_province from meal_order as o left join ding_talk_user as u on u.userid=o.ding_talk_id $condition");
		sql.setCondition(cnd);
		sql.setCallback(Sqls.callback.records());
		mealOrderService.dao().execute(sql);
		Pager pager = mealOrderService.dao().createPager(page, 20);
		Sql countSql = Sqls.create("select count(1) from meal_order as o left join ding_talk_user as u on u.userid=o.ding_talk_id $condition");
		countSql.setCallback(Sqls.callback.integer());
		countSql.setCondition(cnd);
		mealOrderService.dao().execute(countSql);
		pager.setRecordCount(countSql.getInt(0));
		sql.setPager(pager);
		result.setPager(pager);
		Lang.each(sql.getList(org.nutz.dao.entity.Record.class), new Each<org.nutz.dao.entity.Record>() {
			@Override
			public void invoke(int index, org.nutz.dao.entity.Record ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				NutMap bean = Lang.obj2nutmap(ele);
				long id = bean.getLong("id");
				Sql sql = Sqls.create("select f.id,f.name,f.price,f.image,oi.number,s.name as sname,s.telephone,s.url,s.address,s.province from meal_order_info as oi left join meal_food as f on f.id=oi.item_id left join meal_seller as s on s.id=f.seller_id $condition");
				sql.setCallback(Sqls.callback.records());
				sql.setCondition(Cnd.where("oi.meal_order_id", "=", id));
				mealOrderService.dao().execute(sql);
				bean.put("id", bean.getString("id"));
				bean.put("info", sql.getList(Record.class));
				dataList.add(bean);
			}
		});
		return Result.success().addData("pager", result);
	}

	@At
	@NutzRequiresPermissions(value = "meal.order.list", name = "订单列表", tag = "订餐管理", enable = false)
	public Result search(@Param(value = "page", df = "1") int page, @Param(value = "key", df = "") String key) {
		String time = Times.format("yyyyMMdd", Times.now());
		PageredData<NutMap> result = new PageredData<NutMap>();
		List<NutMap> dataList = new ArrayList<>();
		result.setDataList(dataList);
		Cnd cnd = Cnd.where("o.time", "=", time).and("o.is_deleted", "=", false).and("u.name", "like", "%" + key + "%");
		Sql sql = Sqls.create("select o.*,u.name from meal_order as o left join system_user as u on u.openid=o.ding_talk_id $condition");
		sql.setCondition(cnd);
		sql.setCallback(Sqls.callback.records());
		mealOrderService.dao().execute(sql);
		Pager pager = mealOrderService.dao().createPager(page, 20);
		Sql csql = Sqls.create("select count(1) from meal_order as o left join system_user as u on u.openid=o.ding_talk_id $condition");
		csql.setCondition(cnd);
		csql.setCallback(Sqls.callback.integer());
		mealOrderService.dao().execute(csql);
		pager.setRecordCount(csql.getInt(0));
		sql.setPager(pager);
		result.setPager(pager);
		Lang.each(sql.getList(org.nutz.dao.entity.Record.class), new Each<org.nutz.dao.entity.Record>() {
			@Override
			public void invoke(int index, org.nutz.dao.entity.Record ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				NutMap bean = Lang.obj2nutmap(ele);
				bean.put("id", bean.getString("id"));
				dataList.add(bean);
			}
		});
		return Result.success().addData("pager", result.addParam("key", key));
	}
}
