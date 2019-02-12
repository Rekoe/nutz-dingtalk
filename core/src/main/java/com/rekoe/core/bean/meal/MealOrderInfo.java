package com.rekoe.core.bean.meal;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

import com.rekoe.core.bean.DataBaseEntity;

@Table("meal_order_info")
public class MealOrderInfo extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = -518479485306295187L;

	@Id(auto = true)
	private long id;

	@Column(hump = true)
	private long itemId;

	@Column
	private int number;

	@Column(hump = true)
	private long mealOrderId;

	@One(target = MealOrder.class, field = "mealOrderId")
	private MealOrder mealOrde;

	public MealOrderInfo(long itemId, int number, long mealOrderId) {
		super();
		this.itemId = itemId;
		this.number = number;
		this.mealOrderId = mealOrderId;
	}

	public MealOrderInfo() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public long getMealOrderId() {
		return mealOrderId;
	}

	public void setMealOrderId(long mealOrderId) {
		this.mealOrderId = mealOrderId;
	}

	public MealOrder getMealOrde() {
		return mealOrde;
	}

	public void setMealOrde(MealOrder mealOrde) {
		this.mealOrde = mealOrde;
	}

}
