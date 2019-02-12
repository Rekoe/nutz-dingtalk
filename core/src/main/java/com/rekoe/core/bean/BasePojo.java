package com.rekoe.core.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.lang.Times;

public class BasePojo extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Prev(els = @EL("$me.now()"))
	@Column("ct")
	protected Date createTime;

	@Prev(els = @EL("$me.now()"))
	@Column("ut")
	protected Date updateTime;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date now() {
		return Times.now();
	}
}
