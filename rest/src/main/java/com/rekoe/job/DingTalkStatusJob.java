package com.rekoe.job;

import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.rekoe.service.DingTalkService;

@IocBean
@Scheduled(cron = "0 0 08 ? * MON-SAT")
public class DingTalkStatusJob implements Job {

	private final static Log log = Logs.get();

	@Inject
	private DingTalkService dingTalkService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			dingTalkService.loadDingUser();
		} catch (Exception e) {
			log.error(e);
		}
	}
}
