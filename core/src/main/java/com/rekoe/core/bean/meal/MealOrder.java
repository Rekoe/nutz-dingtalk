package com.rekoe.core.bean.meal;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.lang.Times;

import com.rekoe.core.bean.DataBaseEntity;

@Table("meal_order")
@TableIndexes({ @Index(name = "m_o_unique_index", fields = { "dingTalkId", "time", "deleted" }, unique = false) })
public class MealOrder extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = 727836576675773576L;

	@Id(auto = false)
	@Prev(els = @EL("ig()"))
	private long id;

	@Column
	private String time;

	@Column(hump = true)
	private String dingTalkId;

	@Column(hump = true)
	@ColDefine(type = ColType.TIMESTAMP, insert = true)
	private Date createTime;

	@Column("is_deleted")
	private boolean deleted;

	@Many(target = MealOrderInfo.class, field = "mealOrderId")
	private List<MealOrderInfo> mealOrderInfos;

	public MealOrder() {
		super();
	}

	public MealOrder(String dingTalkId, String time) {
		super();
		this.dingTalkId = dingTalkId;
		this.createTime = Times.now();
		this.time = time;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDingTalkId() {
		return dingTalkId;
	}

	public void setDingTalkId(String dingTalkId) {
		this.dingTalkId = dingTalkId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public List<MealOrderInfo> getMealOrderInfos() {
		return mealOrderInfos;
	}

	public void setMealOrderInfos(List<MealOrderInfo> mealOrderInfos) {
		this.mealOrderInfos = mealOrderInfos;
	}

}
