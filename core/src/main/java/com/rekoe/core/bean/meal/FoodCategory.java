package com.rekoe.core.bean.meal;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import com.rekoe.core.bean.DataBaseEntity;

@Table("food_category")
@TableIndexes({ @Index(name = "m_f_c_unique_index", fields = { "name", "sellerId" }, unique = true) })
public class FoodCategory extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = 8599145812856391655L;

	@Id
	private long id;

	@Column
	private String name;

	@Column
	private int type;

	@Column(hump = true)
	private long sellerId;

	@Default("1")
	@Column(hump = true)
	private boolean onSale;
	
	@ManyMany(target = MealFood.class, relation = "meal_food_category", from = "category_id", to = "food_id")
	private List<MealFood> foods;


	public FoodCategory() {
		super();
	}

	public FoodCategory(String name, int type, long sellerId, boolean onSale) {
		super();
		this.name = name;
		this.type = type;
		this.sellerId = sellerId;
		this.onSale = onSale;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<MealFood> getFoods() {
		return foods;
	}

	public void setFoods(List<MealFood> foods) {
		this.foods = foods;
	}

	public long getSellerId() {
		return sellerId;
	}

	public void setSellerId(long sellerId) {
		this.sellerId = sellerId;
	}

	public boolean isOnSale() {
		return onSale;
	}

	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}

}
