package com.rekoe.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.annotation.ReqHeader;
import org.nutz.plugins.event.Event;
import org.nutz.plugins.event.EventListener;

import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.dingtalk.DingTalkUser;
import com.rekoe.core.bean.meal.FoodCategory;
import com.rekoe.core.bean.meal.MealFood;
import com.rekoe.core.bean.meal.MealOrder;
import com.rekoe.core.bean.meal.MealOrderInfo;
import com.rekoe.core.bean.meal.MealSeller;
import com.rekoe.service.DingTalkService;
import com.rekoe.service.DingTalkUserService;

import net.sf.ehcache.util.concurrent.ConcurrentHashMap;

@At("/dingcan")
@IocBean(create = "init")
public class DingCanModule implements EventListener {

	/**
	 * 专场
	 */
	private NutMap special = NutMap.NEW();
	/**
	 * 供应商
	 */
	private NutMap poiInfo = NutMap.NEW();
	/**
	 * 菜单列表
	 */

	private Map<Long, NutMap> sellerFoodSpuTags = new ConcurrentHashMap<Long, NutMap>();

	public void init() {
		special.addv("operation_source_list", new ArrayList<NutMap>() {
			private static final long serialVersionUID = 1212390368609360652L;
			{
				add(NutMap.NEW().addv("type", 1).addv("sequence", 0).addv("pic_url", "http://p1.meituan.net/xianfu/6588840adbcfbfe2c7a43aa69d4b36da107767.jpg").addv("content", "").addv("scheme", "meituanwaimai://waimai.meituan.com/productset?wmpoiid=495579&productsettag=1239222&provider=0"));
				add(NutMap.NEW().addv("type", 1).addv("sequence", 0).addv("pic_url", "http://p1.meituan.net/xianfu/378446763167442a511dc9d4c675d877144571.jpg").addv("content", "").addv("scheme", "meituanwaimai://waimai.meituan.com/productset?wmpoiid=495579&productsettag=1239645&provider=0"));
			}
		}).addv("tag_name", "专场").addv("tag_icon", "http://p1.meituan.net/xianfu/a4167074795cfb33d819fd0590d1545b2048.png");
		special.put("operation_source_list", new ArrayList<NutMap>());
		loadSeller();
	}

	private void loadSeller() {
		poiInfo.put("name", "泛阿网络科技有限公司");
		poiInfo.put("shipping_time", "周一~周五(18:00),周六(10:00~11:00,18:00)");
		poiInfo.put("wm_poi_score", 5);
		poiInfo.put("discounts2", new ArrayList<NutMap>() {
			private static final long serialVersionUID = 1010130327807565668L;
			{
				add(NutMap.NEW().addv("icon_url", "http://meal.rekoe.com/style/img/bulletin@2x.png").addv("info", poiInfo.getString("shipping_time")));
			}
		});
		poiInfo.put("shipping_fee_tip", "限额¥20");
		poiInfo.put("price_tip", "起送 ¥20");
		poiInfo.put("delivery_time_tip", "每天19:00前");
		poiInfo.put("poi_back_pic_url", "http://p1.meituan.net/aichequan/dfd39d77d47dfd6df4e1c07ab2e87458193641.png");
		poiInfo.put("pic_url", "http://meal.rekoe.com/style/img/default.jpeg");
		poiInfo.put("head_pic_url", "http://meal.rekoe.com/style/img/154901190572945.png");
	}

	private List<NutMap> loadFoods(long sellerId) {
		List<NutMap> foodSpuTags = new ArrayList<NutMap>();
		dao.each(FoodCategory.class, Cnd.where("seller_id", "=", sellerId).asc("id"), new Each<FoodCategory>() {
			@Override
			public void invoke(int index, FoodCategory category, int length) throws ExitLoop, ContinueLoop, LoopException {
				NutMap foodSpuTag = NutMap.NEW();

				int type = category.getType();
				switch (type) {
				case 1: {
					foodSpuTag.put("icon", "http://p1.meituan.net/xianfu/10ba72e043ef4eca806cafb1a90a22662048.png");
					break;
				}
				case 2: {
					foodSpuTag.put("icon", "http://p1.meituan.net/xianfu/a4167074795cfb33d819fd0590d1545b2048.png");
					break;
				}
				default:
					foodSpuTag.put("icon", "");
					break;
				}
				List<NutMap> foods = new ArrayList<>();
				category = dao.fetchLinks(category, "foods", Cnd.where("onSale", "=", true));
				Lang.each(category.getFoods(), new Each<MealFood>() {
					@Override
					public void invoke(int index, MealFood ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						NutMap food = NutMap.NEW();
						foods.add(food);
						food.put("id", ele.getId());
						food.put("name", ele.getName());
						food.put("price", ele.getPrice());
						food.put("praise_num", 3);
						food.put("praise_content", "赞3");
						food.put("tread_num", 0);
						food.put("praise_num_new", 3);
						food.put("unit", "份");
						food.put("description", "");
						food.put("picture", ele.getImage());
						food.put("month_saled", ele.getSellCount());
						food.put("month_saled_content", "月售" + ele.getSellCount());
						food.put("status", 3);
						food.put("status_description", "非可售时间");
						food.put("product_label_picture", "");
						food.put("rating", new ArrayList<>());
					}
				});
				if (Lang.isNotEmpty(foods)) {
					foodSpuTag.put("name", category.getName());
					foodSpuTags.add(foodSpuTag);
					foodSpuTag.put("spus", foods);
				}
			}
		});
		return foodSpuTags;
	}

	@Inject
	private Dao dao;

	@Inject
	private DingTalkService dingTalkService;

	@Inject
	private DingTalkUserService dingTalkUserService;

	@POST
	@Ok("json")
	@At("/submit_order")
	@AdaptBy(type = JsonAdaptor.class)
	public Result submitOrder(List<NutMap> orders, @ReqHeader("X-Auth-Token") String dingTalkId) {
		Date now = Times.now();
		int hour = NumberUtils.toInt(Times.format("HH", Times.nextMinute(now, -1)));
		if (hour >= 18) {
			return Result.fail("订餐时间截止到当天18:00");
		}
		if (StringUtils.isBlank(dingTalkId)) {
			return Result.fail("请在钉钉内部使用");
		}
		if (StringUtils.isBlank(dingTalkId)) {
			return Result.fail("请在钉钉内部使用");
		}
		DingTalkUser dingTalkUser = dingTalkUserService.fetch(Cnd.where("userid", "=", dingTalkId));
		if (Lang.isEmpty(dingTalkUser)) {
			return Result.fail("请先登陆获取授权");
		}
		String userProvince = dingTalkUser.getUserProvince();
		switch (userProvince) {
		case "北京市":
		case "陕西省": {
			break;
		}
		case "": {
			return Result.fail("请先定位您的位置");
		}
		default:
			return Result.fail("暂时只开通北京、西安两地订餐");
		}
		String time = Times.format("yyyyMMdd", now);
		MealOrder mealOrder = dao.fetch(MealOrder.class, Cnd.where("dingTalkId", "=", dingTalkId).and("time", "=", time).and("is_deleted", "=", false));
		if (Lang.isEmpty(mealOrder)) {
			mealOrder = dao.insert(new MealOrder(dingTalkId, time));
			final long id = mealOrder.getId();
			dao.fastInsert(new ArrayList<MealOrderInfo>() {
				private static final long serialVersionUID = 1010130327807565668L;
				{
					Lang.each(orders, new Each<NutMap>() {
						@Override
						public void invoke(int index, NutMap ele, int length) throws ExitLoop, ContinueLoop, LoopException {
							add(new MealOrderInfo(ele.getLong("id"), ele.getInt("count"), id));
						}
					});
				}
			});
			com.rekoe.core.dingtalk.isv.message.Markdown markdown = new com.rekoe.core.dingtalk.isv.message.Markdown();
			markdown.setTitle("订餐成功提醒");
			markdown.setText(dingTalkService.markDownMsg(new ArrayList<NutMap>() {
				private static final long serialVersionUID = -5027701551397877522L;
				{
					Lang.each(orders, new Each<NutMap>() {
						@Override
						public void invoke(int index, NutMap ele, int length) throws ExitLoop, ContinueLoop, LoopException {
							long id = ele.getLong("id");
							int number = ele.getInt("count");
							MealFood food = dao.fetch(MealFood.class, Cnd.where("id", "=", id));
							add(NutMap.NEW().addv("number", number).addv("name", food.getName()).addv("image", food.getImage()));
							dao.update(MealFood.class, Chain.makeSpecial("sell_count", "+1"), Cnd.where("id", "=", id));
						}
					});
				}
			}));
			dingTalkService.sendMarkdownMessage(dingTalkId, markdown);
		} else {
			return Result.fail("防止重复下单");
		}
		return Result.success();
	}

	@Ok("json")
	@At("/cancel_order")
	public Result cancelOrder(@ReqHeader("X-Auth-Token") String dingTalkId) {
		if (StringUtils.isBlank(dingTalkId)) {
			return Result.fail("请在钉钉内部使用");
		}
		String time = Times.format("yyyyMMdd", Times.now());
		int result = dao.update(MealOrder.class, Chain.make("is_deleted", true), Cnd.where("dingTalkId", "=", dingTalkId).and("time", "=", time));
		if (result > 0) {
			com.rekoe.core.dingtalk.isv.message.Markdown markdown = new com.rekoe.core.dingtalk.isv.message.Markdown();
			markdown.setTitle("订餐取消提醒");
			markdown.setText("已成功取消当日订单\n" + System.currentTimeMillis());
			dingTalkService.sendMarkdownMessage(dingTalkId, markdown);
		}
		return Result.success();
	}

	@At
	@Ok("json")
	public Result new_ratings(@Param("::") NutMap param) {
		return Result.success().addData(Json.fromJson(NutMap.class, Files.read("ratings.json")));
	}

	private final Object lock = new Object();

	@At
	@Ok("json")
	public Result new_goods(@Param("id") long id) {
		NutMap tags = sellerFoodSpuTags.get(id);
		if (tags == null) {
			synchronized (lock) {
				tags = sellerFoodSpuTags.get(id);
				if (tags == null) {
					tags = NutMap.NEW();
					tags.put("container_operation_source", special);
					tags.put("food_spu_tags", loadFoods(id));
					tags.put("code", 0);
					tags.put("msg", "成功");
					sellerFoodSpuTags.put(id, tags);
				}
			}
		}
		return Result.success().addData("data", tags);
	}

	@At
	@Ok("json")
	public Result new_seller(@Param("::") NutMap param) {
		return Result.success().addData(Json.fromJson(NutMap.class, Files.read("seller.json")));
	}

	@At
	@Ok("json")
	public Result sellers(@Param(value = "user_province", df = "定位..") String userProvince) {
		System.err.println("userProvince .. " + userProvince);
		switch (userProvince) {
		case "北京市":
		case "陕西省": {
			break;
		}
		default:
			userProvince = "北京市";
			break;
		}
		List<NutMap> list = new ArrayList<>();
		dao.each(MealSeller.class, Cnd.where("is_deleted", "=", false).and("province", "=", userProvince), new Each<MealSeller>() {
			@Override
			public void invoke(int index, MealSeller ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				String name = StringUtils.replace(ele.getName(), "（", "(");
				list.add(NutMap.NEW().addv("name", StringUtils.substringBefore(name, "(")).addv("id", ele.getId()));
			}
		});
		return Result.success().addData("data", list);
	}

	@At
	@Ok("json")
	public Result header() {
		return Result.success().addData("data", poiInfo);
	}

	@Override
	public String subscribeTopic() {
		return "FOOD_UPDATE";
	}

	@Override
	public void onEvent(Event e) {
		sellerFoodSpuTags.remove(e.getParam());
	}
}
