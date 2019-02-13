package com.rekoe.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rekoe.core.bean.dingtalk.DingTalkUser;
import com.rekoe.service.DingTalkService;
import com.rekoe.service.DingTalkUserService;

@IocBean
@Scheduled(cron = "0 0 17 ? * MON-SAT")
public class NotificationMealJob implements Job {

	@Inject
	private DingTalkUserService dingTalkUserService;

	@Inject
	private DingTalkService dingTalkService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<String> uids = new ArrayList<>();
		dingTalkUserService.each(Cnd.where("active", "=", true), new Each<DingTalkUser>() {
			@Override
			public void invoke(int index, DingTalkUser user, int length) throws ExitLoop, ContinueLoop, LoopException {
				String uid = user.getUserid();
				if (StringUtils.isNotBlank(uid)) {
					uids.add(uid);
				}
			}
		});
		dingTalkService.sendDingTalkMarkDownMsg(uids, "加班餐订餐提醒", "# 加班餐订餐提醒-(测试请忽略) \n 如果需要定加班餐请在\n 北京时间 18:00前\n 通过钉钉手机客户端[OM美食]进入订餐系统\n");
	}

}
