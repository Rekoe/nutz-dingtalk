package com.rekoe.module;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.annotation.ReqHeader;

import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.dingtalk.DingTalkUser;
import com.rekoe.service.DingTalkService;
import com.rekoe.service.DingTalkUserService;
import com.rekoe.service.MealOrderService;
import com.rekoe.service.UserService;
import com.rekoe.utils.DingTalkJsApiSingnature;

import redis.clients.jedis.Jedis;

@IocBean
public class DingTalkModule {

	@Inject
	private DingTalkService dingTalkService;

	@Inject
	private UserService userService;

	@Inject
	private DingTalkUserService dingTalkUserService;

	@Inject
	private MealOrderService mealOrderService;

	@Ok("json")
	@At("/dingtalk/auth")
	public Result auth(@Param("code") String authCode, HttpServletRequest req) {
		if (StringUtils.isBlank(authCode)) {
			return Result.fail("授权失败");
		}
		System.out.println("=========================== request dingTalk auth start ================");
		NutMap userInfo = dingTalkService.getuserinfo(authCode);
		System.out.println("=========================== request dingTalk auth end ===================");
		int status = userInfo.getInt("errcode");
		if (status != 0) {
			return Result.fail(userInfo.getString("errmsg"));
		}
		String uid = userInfo.getString("userid");
		DingTalkUser dingTalkUser = dingTalkUserService.fetch(Cnd.where("userid", "=", uid));
		Result result = Result.success();
		if (Lang.isEmpty(dingTalkUser)) {
			NutMap dingUser = dingTalkService.get(userInfo.getString("userid"));
			String workPlace = dingUser.getString("workPlace");
			workPlace = StringUtils.defaultString(workPlace, "");
			switch (workPlace) {
			case "天溪园":
			case "天朗园":
			case "天畅园":
			case "北京": {
				workPlace = "北京市";
				break;
			}
			case "西安": {
				workPlace = "陕西省";
				break;
			}
			default:
				break;
			}
			String openId = dingUser.getString("openId");
			String name = dingUser.getString("name");
			dingTalkUser = dingTalkUserService.fetch(Cnd.where("open_id", "=", openId));
			if (Lang.isEmpty(dingTalkUser)) {
				Chain chain = Chain.make("openId", openId).add("userid", uid).add("name", name).add("dingId", dingUser.getString("dingId"));
				if (StringUtils.isNotBlank(workPlace)) {
					chain.add("userProvince", workPlace);
				}
				dingTalkUserService.insert(chain);
				dingTalkUser = dingTalkUserService.fetch(Cnd.where("open_id", "=", openId));
			} else {
				Chain chain = Chain.make("userid", uid);
				if (StringUtils.isNotBlank(workPlace)) {
					chain.add("userProvince", workPlace);
				}
				dingTalkUserService.update(chain, Cnd.where("open_id", "=", openId));
			}
			result.addData("name", name);
		} else {
			result.addData("name", dingTalkUser.getName());
		}
		List<Record> orderInfo = mealOrderService.orderInfo(uid);
		result.addData("user_id", uid).addData("meal", Lang.isNotEmpty(orderInfo));
		result.addData("order_info", orderInfo);
		result.addData("avatar", dingTalkUser.getAvatar());
		result.addData("is_position", StringUtils.isBlank(dingTalkUser.getUserProvince()));
		result.addData("province", StringUtils.defaultIfEmpty(dingTalkUser.getUserProvince(), "定位.."));
		System.out.println(Json.toJson(result));
		return result;
	}

	@Aop("redis")
	public String getTicket() {
		String TICKET_KEY = "dingtalk.ticket." + dingTalkService.agentId;
		Jedis jedis = jedis();
		// jedis.del(TICKET_KEY);
		String ticket = jedis.get(TICKET_KEY);
		if (StringUtils.isBlank(ticket)) {
			ticket = dingTalkService.getJsapiTicket().getString("ticket");
			jedis.set(TICKET_KEY, ticket);
			jedis.expire(TICKET_KEY, 60 * 115);
		}
		return ticket;
	}

	@At
	@Ok("json")
	public Result sign(@Param("::") NutMap param) throws Exception {
		String ticket = getTicket();
		NutMap data = NutMap.NEW();
		data.put("agentId", dingTalkService.agentId);
		data.put("corpId", dingTalkService.CORPID);
		data.put("timeStamp", System.currentTimeMillis() / 1000);
		data.put("nonceStr", "abcdefg");
		data.put("signature", DingTalkJsApiSingnature.getJsApiSingnature("http://" + param.getString("url", "meal.rekoe.com") + "/", data.getString("nonceStr"), data.getLong("timeStamp"), ticket));
		data.put("jsApiList", new ArrayList<String>() {
			private static final long serialVersionUID = 6978888486973671148L;
			{
				add("device.geolocation.get");
				add("device.geolocation.start");
				add("device.geolocation.stop");
				add("biz.ding.create");
				add("biz.map.locate");
				add("biz.map.search");
				add("biz.map.view");
			}
		});
		data.put("type", 0);
		Result result = Result.success().addData(data);
		System.out.println(Json.toJson(result));
		return result;
	}

	@Ok("json")
	@At("/update_work_place")
	public Result updateWorkPlace(@Param("work_place") String workPlace, @ReqHeader("X-Auth-Token") String dingTalkId) {
		if (StringUtils.isBlank(dingTalkId)) {
			return Result.fail("请在钉钉内部使用");
		}
		if (StringUtils.isNotBlank(workPlace)) {
			if ("西安".contentEquals(workPlace)) {
				workPlace = "陕西省";
			}
			Chain chain = Chain.make("userProvince", workPlace);
			dingTalkUserService.update(chain, Cnd.where("userid", "=", dingTalkId));
		}
		return Result.success();
	}
}
