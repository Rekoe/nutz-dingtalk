package com.rekoe.core.bean.meal;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

import com.rekoe.core.bean.DataBaseEntity;

@Table("food_ratings")
public class FoodRating extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = -4520622931825744912L;

	@Id
	private long id;

	@Column(hump = true)
	private String dingTalkId;

	@Column(hump = true)
	@ColDefine(type = ColType.TIMESTAMP)
	private Date rateTime;

	@Column(hump = true)
	private int rateType;

	@Column
	@ColDefine(type = ColType.TEXT)
	private String text;

	@Column(hump = true)
	private long foodId;

	@One(target = MealFood.class, field = "foodId")
	private MealFood food;

	@Column
	//@Default("")
	@ColDefine(type = ColType.MYSQL_JSON)
	private List<String> recommend;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<String> getRecommend() {
		return recommend;
	}

	public void setRecommend(List<String> recommend) {
		this.recommend = recommend;
	}

	public String getDingTalkId() {
		return dingTalkId;
	}

	public void setDingTalkId(String dingTalkId) {
		this.dingTalkId = dingTalkId;
	}

	public Date getRateTime() {
		return rateTime;
	}

	public void setRateTime(Date rateTime) {
		this.rateTime = rateTime;
	}

	public int getRateType() {
		return rateType;
	}

	public void setRateType(int rateType) {
		this.rateType = rateType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getFoodId() {
		return foodId;
	}

	public void setFoodId(long foodId) {
		this.foodId = foodId;
	}

	public MealFood getFood() {
		return food;
	}

	public void setFood(MealFood food) {
		this.food = food;
	}

}
