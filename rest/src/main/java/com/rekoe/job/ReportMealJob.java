package com.rekoe.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Times;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rekoe.service.DingTalkService;

/**
 * 统计订餐人的信息
 * 
 * @author kouxian
 *
 */
@IocBean
@Scheduled(cron = "0 10 18 * * ?")
public class ReportMealJob implements Job {

	@Inject
	private DingTalkService dingTalkService;

	@Inject
	private Dao dao;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String time = Times.format("yyyyMMdd", new Date());
		Sql mealSql = Sqls.createf("select d.name,d.avatar,d.user_province as province from meal_order as o left join ding_talk_user as d on d.userid=o.ding_talk_id where o.time='%s' and o.is_deleted=0", time);
		mealSql.setCallback(Sqls.callback.records());
		dao.execute(mealSql);
		List<Record> orders = mealSql.getList(Record.class);
		Map<String, List<Record>> orderByGrouy = new HashMap<>();
		Lang.each(orders, new Each<Record>() {
			@Override
			public void invoke(int index, Record ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				String province = ele.getString("province");
				List<Record> os = orderByGrouy.get(province);
				if (os == null) {
					os = Lists.newArrayList();
					orderByGrouy.put(province, os);
				}
				os.add(ele);
			}
		});

		if (Lang.isNotEmpty(orders)) {
			Sql sql = Sqls.create("select d.userid,s.name,d.user_province as province from system_user as s left join ding_talk_user as d on d.open_id=s.openid where s.is_system=true and d.userid is not null and s.is_locked=0");
			sql.setCallback(Sqls.callback.records());
			dao.execute(sql);
			List<Record> records = sql.getList(Record.class);
			Map<String, List<String>> managers = Maps.newHashMap();
			Lang.each(records, new Each<Record>() {
				@Override
				public void invoke(int index, Record ele, int length) throws ExitLoop, ContinueLoop, LoopException {
					String province = ele.getString("province");
					List<String> os = managers.get(province);
					if (os == null) {
						os = Lists.newArrayList();
						managers.put(province, os);
					}
					os.add(ele.getString("userid"));
				}
			});
			Lang.each(managers.entrySet(), new Each<Entry<String, List<String>>>() {
				@Override
				public void invoke(int index, Entry<String, List<String>> entry, int length) throws ExitLoop, ContinueLoop, LoopException {
					String province = entry.getKey();
					List<String> uids = entry.getValue();
					List<Record> orders = orderByGrouy.get(province);
					if (Lang.isNotEmpty(records)) {
						StringBuffer sb = new StringBuffer();
						Lang.each(orders, new Each<Record>() {
							@Override
							public void invoke(int index, Record ele, int length) throws ExitLoop, ContinueLoop, LoopException {
								sb.append(index + 1).append(")=>>").append(ele.getString("name")).append("-(").append(province).append(")\n");
							}
						});
						dingTalkService.sendDingTalkMarkDownMsg(uids, "订餐统计报告", sb.toString());
					}
				}
			});
		}
	}

}
