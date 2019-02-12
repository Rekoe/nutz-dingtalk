package com.rekoe.core.bean.meal;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import com.rekoe.core.bean.DataBaseEntity;

@Comment("菜单")
@Table("meal_food")
@TableIndexes({ @Index(name = "m_f_unique_index", fields = { "sellerId", "itemId" }, unique = true) })
public class MealFood extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = -1369829503394783183L;

	@Id
	private long id;

	@Column
	@ColDefine(width = 200)
	private String name;

	@Column(hump = true)
	private long sellerId;

	@Default("1")
	@Column(hump = true)
	private boolean onSale;

	@Default("0")
	@Column(hump = true)
	private long itemId;

	@Column
	@ColDefine(type = ColType.FLOAT, width = 7, precision = 2)
	private float price;

	@Column
	private String description;

	@Column
	@ColDefine(type = ColType.TEXT)
	private String info;

	@Column(hump = true)
	private int sellCount;

	@Column
	@Default("100")
	private int rating;

	@Column(hump = true)
	@ColDefine(type = ColType.FLOAT, width = 7, precision = 2)
	private float oldPrice;

	@Column
	@ColDefine(width = 1024)
	private String image;

	@Column
	@ColDefine(width = 1024)
	private String icon;

	@ManyMany(target = FoodCategory.class, relation = "meal_food_category", from = "category_d", to = "food_id")
	private List<FoodCategory> categories;

	@Many(target = FoodRating.class, field = "foodId")
	private List<FoodRating> foodRatings;

	public MealFood() {
		super();
	}

	public MealFood(String name, long sellerId, boolean onSale, long itemId, float price, String description, String info, int sellCount, int rating, float oldPrice, String image, String icon) {
		super();
		this.name = name;
		this.sellerId = sellerId;
		this.onSale = onSale;
		this.itemId = itemId;
		this.price = price;
		this.description = description;
		this.info = info;
		this.sellCount = sellCount;
		this.rating = rating;
		this.oldPrice = oldPrice;
		this.image = image;
		this.icon = icon;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getSellCount() {
		return sellCount;
	}

	public void setSellCount(int sellCount) {
		this.sellCount = sellCount;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public float getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(float oldPrice) {
		this.oldPrice = oldPrice;
	}

	public List<FoodCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<FoodCategory> categories) {
		this.categories = categories;
	}

	public List<FoodRating> getFoodRatings() {
		return foodRatings;
	}

	public void setFoodRatings(List<FoodRating> foodRatings) {
		this.foodRatings = foodRatings;
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

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
}
