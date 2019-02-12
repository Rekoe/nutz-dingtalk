package com.rekoe.service;

import org.nutz.ioc.loader.annotation.IocBean;

import com.rekoe.core.bean.dingtalk.DingTalkUser;

@IocBean(fields = { "dao" })
public class DingTalkUserService extends BaseService<DingTalkUser>{

}
