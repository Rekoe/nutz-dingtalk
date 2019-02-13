package com.rekoe.job;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
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
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rekoe.core.bean.acl.User;
import com.rekoe.core.bean.dingtalk.DingTalkUser;
import com.rekoe.service.DingTalkService;

@IocBean(create = "exec")
@Scheduled(cron = "0 0 09 ? * MON-SAT")
public class DingUserStatusJob implements Job {

	private final static Log log = Logs.get();

	private LinkedBlockingQueue<Record> uids = new LinkedBlockingQueue<Record>(10000);

	@Inject
	private Dao dao;

	@Inject
	private DingTalkService dingTalkService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Sql sql = Sqls.create("select userid,open_id from ding_talk_user where is_active = true");
		sql.setCallback(Sqls.callback.records());
		dao.execute(sql);
		List<Record> uids = sql.getList(Record.class);
		if (Lang.isNotEmpty(uids)) {
			this.uids.addAll(uids);
		}
	}

	public void exec() {
		while (true) {
			try {
				final Record record = uids.take();
				String userid = record.getString("userid");
				if (StringUtils.isNotBlank(userid)) {
					NutMap d = dingTalkService.listcontact(userid);
					Lang.each(d.getAsList("result", NutMap.class), new Each<NutMap>() {
						@Override
						public void invoke(int index, NutMap ele, int length) throws ExitLoop, ContinueLoop, LoopException {
							String userid = ele.getString("userid");
							Chain chain = Chain.make("active", false);
							dao.update(DingTalkUser.class, chain, Cnd.where("userid", "=", userid));
							dao.update(User.class, Chain.make("locked", true), Cnd.where("openid", "=", record.getString("open_id")));
						}
					});
				}
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

}
