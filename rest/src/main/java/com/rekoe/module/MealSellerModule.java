package com.rekoe.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.rekoe.common.vo.Result;
import com.rekoe.core.bean.meal.FoodCategory;
import com.rekoe.core.bean.meal.MealFood;
import com.rekoe.core.bean.meal.MealSeller;
import com.rekoe.service.MealSellerService;

import us.codecraft.xsoup.Xsoup;

@IocBean
@Ok("json")
@At("/meal/seller")
public class MealSellerModule {

	@Inject
	private MealSellerService mealSellerService;

	@At
	@NutzRequiresPermissions(value = "meal.seller.list", name = "商家列表", tag = "订餐管理", enable = true)
	public Result list(@Param(value = "page", df = "1") int page, @Attr(scope = Scope.SESSION, value = "me") long uid) {
		Sql sqlu = Sqls.create("select u.is_system,d.userid,d.user_province from system_user as u left join ding_talk_user as d on d.open_id=u.openid $condition");
		sqlu.setCallback(Sqls.callback.record());
		sqlu.setCondition(Cnd.where("u.id", "=", uid));
		dao.execute(sqlu);
		Record record = sqlu.getObject(Record.class);
		String userProvince = record.getString("user_province");
		return Result.success().addData("pager", mealSellerService.searchByPage(page, StringUtils.isNotBlank(userProvince)?Cnd.where("province", "=", userProvince).desc("id"):Cnd.NEW().desc("id")));
	}

	@At
	@NutzRequiresPermissions(value = "meal.seller.list", name = "商家列表", tag = "订餐管理", enable = false)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", mealSellerService.searchByKeyAndPage(key, page, "name", "description").addParam("key", key));
	}

	@Inject
	private Dao dao;

	@At
	@Ok("json")
	@NutzRequiresPermissions(value = "meal.seller.collectiont", name = "商家采集", tag = "订餐管理", enable = true)
	public Result collection(@Param("url") String url,@Param("province")String province) {
		if (StringUtils.isBlank(url)) {
			return Result.fail("采集地址不可以为空");
		}
		MealSeller mealSeller = dao.fetch(MealSeller.class, Cnd.where("url", "=", url));
		if (Lang.isNotEmpty(mealSeller)) {
			return Result.fail("重复采集");
		}
		String html = fetchHtml(url);
		Document document = Jsoup.parse(html);
		String sellerLogo = Xsoup.compile("//img[@class='scroll-loading']/@data-src").evaluate(document).get();
		String seller = Xsoup.compile("//div[@class='na']/a/span/text()").evaluate(document).get();
		String saleTime = Xsoup.compile("//div[@class='clearfix sale-time']/p[2]/span/text()").evaluate(document).get();
		String address = Xsoup.compile("//div[@class='rest-info-thirdpart poi-address']/p[2]/text()").evaluate(document).get();
		String telephone = Xsoup.compile("//div[@class='telephone']/p[2]/text()").evaluate(document).get();
		String description = Xsoup.compile("//head/meta[2]/@content").evaluate(document).get();
		if(StringUtils.isBlank(seller)) {
			return Result.fail("请重试");
		}
		if (Lang.isEmpty(mealSeller)) {
			mealSeller = dao.insert(new MealSeller(seller, description, sellerLogo, saleTime, address, telephone, url,province));
		}
		long sellerId = mealSeller.getId();
		List<String> menues = Xsoup.compile("//div[@class='category']").evaluate(document).list();
		Lang.each(menues, new Each<String>() {
			@Override
			public void invoke(int index, String menue, int length) throws ExitLoop, ContinueLoop, LoopException {
				Document document = Jsoup.parse(menue);
				String categoryName = Xsoup.compile("//div[@class='category']/h3/@title").evaluate(document).get();
				FoodCategory foodCategory = dao.fetch(FoodCategory.class, Cnd.where("name", "=", categoryName).and("sellerId", "=", sellerId));
				if (Lang.isEmpty(foodCategory)) {
					foodCategory = dao.insert(new FoodCategory(categoryName, -1, sellerId, true));
				}
				long categoryId = foodCategory.getId();
				List<String> images = Xsoup.compile("//div[@class='j-pic-food pic-food']/div[@class='avatar']/img").evaluate(document).list();
				Map<String, String> param = new HashMap<>();
				Lang.each(images, new Each<String>() {
					@Override
					public void invoke(int index, String ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						Document imageDoc = Jsoup.parse(ele);
						String imgName = Xsoup.compile("//img/@alt").evaluate(imageDoc).get();
						String img = Xsoup.compile("//img/@data-src").evaluate(imageDoc).get();
						param.put(StringUtils.remove(imgName, "美团外卖-"), img);
					}
				});
				List<String> prices = Xsoup.compile("//script[@type='text/template']").evaluate(document).list();
				Lang.each(prices, new Each<String>() {
					@Override
					public void invoke(int index, String ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						ele = clearStr(ele, "<script(.*?)>", "");
						ele = clearStr(ele, "</script(.*?)>", "");
						String str = StringUtils.trim(ele.replace("\n", ""));
						NutMap data = Json.fromJson(NutMap.class, str);
						long itemId = data.getLong("id");
						String name = data.getString("name");
						float price = data.getFloat("price");
						float oldPrice = data.getFloat("origin_price");
						String image = param.get(name);
						MealFood food = dao.fetch(MealFood.class, Cnd.where("name", "=", name).and("sellerId", "=", sellerId));
						if (Lang.isEmpty(food)) {
							food = dao.insert(new MealFood(name, sellerId, true, itemId, price, name, name, 1, 100, oldPrice, image, image));
						}
						Record r = dao.fetch("meal_food_category", Cnd.where("food_id", "=", food.getId()).and("category_id", "=", categoryId));
						if (Lang.isEmpty(r)) {
							dao.insert("meal_food_category", Chain.make("food_id", food.getId()).add("category_id", categoryId));
						}
					}
				});
			}
		});
		return Result.success();
	}

	private String fetchHtml(String url) {
		Header header = Header.create();
		header.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		header.set("Accept-Encoding", "gzip, deflate");
		header.set("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
		header.set("Cache-Control", "max-age=0");
		header.set("Connection", "keep-alive");
		header.set("Cookie",
				"mta=213628587.1548718945702.1548719997060.1548723354021.10; wm_order_channel=default; _lxsdk_cuid=1689495e4f7c8-06083ee1a0778-10316653-13c680-1689495e4f7c8; _lxsdk=1689495e4f7c8-06083ee1a0778-10316653-13c680-1689495e4f7c8; openh5_uuid=; ci=1; lat=39.980744; lng=116.423532; w_utmz=\"utm_campaign=(direct)&utm_source=(direct)&utm_medium=(none)&utm_content=(none)&utm_term=(none)\"; w_uuid=ovRVMj8Hue3BqHbTxAmvJslata4I1z3dyh6419MSn4mrhKwWiR15mUNLo78ivXUD; _lx_utm=utm_source%3DBaidu%26utm_medium%3Dorganic; _ga=GA1.3.1827700658.1548718943; _gid=GA1.3.2144463235.1548718943; uuid=440b3996aeec410aab87.1548718988.1.0.0; mtcdn=K; u=151905790; lsu=; cookie_phone=13691366833; w_cid=110105; w_cpy=chaoyangqu; w_cpy_cn=\"%E6%9C%9D%E9%98%B3%E5%8C%BA\"; n=FQj727261384; lt=18eNKCqcQxbNFoLt-WrrZ1cVDn4AAAAA0wcAAHy5FRbS4BeZMeFabAZc7-p0pPJWaL1P1wmJfNzdmzGbgEPkYmgSCVll_AcAiZki3w; __mta=213628587.1548718945702.1548719049602.1548719792030.3; w_visitid=5f2072be-7ffb-4eab-8ee7-20e163961aef; _gat=1; waddrname=\"%E9%87%91%E5%8B%BA%E5%B1%85%E5%AE%B6%E5%B8%B8%E8%8F%9C\"; w_geoid=wx4g3xrytkbp; w_ah=\"39.987256955355406,116.44397377967834,%E9%87%91%E5%8B%BA%E5%B1%85%E5%AE%B6%E5%B8%B8%E8%8F%9C|39.99248391017318,116.4819498360157,%E6%96%B0%E7%96%86%E7%BE%8E%E9%A3%9F%E6%9D%91%28%E6%9C%9B%E4%BA%AC%E5%BA%97%29|39.993512872606516,116.49578467011452,%E5%BA%86%E4%B8%B0%E5%8C%85%E5%AD%90%E9%93%BA%28798%E5%BA%97%29\"; JSESSIONID=hxztbxrf2d93h9j4psys5mvv; _lxsdk_s=168971a9042-d30-30d-7bd%7C%7C8");
		header.set("Host", "waimai.meituan.com");
		header.set("Upgrade-Insecure-Requests", "1");
		header.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
		Response res = Http.get(url, header, 6000);
		String html = res.getContent();
		html = StringUtils.remove(html, "pic-food-col2");
		html = StringUtils.remove(html, "pic-food-rowlast");
		html = StringUtils.remove(html, "tag-");
		return StringUtils.remove(html, "pic-food-col2");
	}

	private String clearStr(String str, String regex, String replace) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher m = p.matcher(str);
		while (m.find()) {
			str = m.replaceAll(replace);
		}
		return str;
	}

	@At
	@Ok("json")
	@NutzRequiresPermissions(value = "meal.seller.edit", name = "商家编辑", tag = "订餐管理", enable = true)
	public Result status(@Param("id") long id) {
		MealSeller mealSeller = mealSellerService.fetch(id);
		if (Lang.isNotEmpty(mealSeller)) {
			boolean onSale = mealSeller.isDeleted();
			mealSellerService.update(Chain.make("is_deleted", !onSale), Cnd.where("id", "=", id));
			return Result.success();
		}
		return Result.fail("未找到数据");
	}
}
