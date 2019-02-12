package com.rekoe.core.bean.meal;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import com.rekoe.core.bean.DataBaseEntity;

@Table("meal_seller")
@TableIndexes({ @Index(name = "m_s_province_index", fields = { "province" }, unique = false) })
public class MealSeller extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = -7621769621117344931L;

	@Id
	private long id;

	@Column
	private String name;

	@Column
	@ColDefine(width = 1024)
	private String description;

	@Column
	@ColDefine(width = 1024)
	private String avatar;

	@Column
	private String saleTime;

	@Column
	@ColDefine(width = 1024)
	private String address;

	@Column
	private String telephone;

	@Column
	@ColDefine(width = 1024)
	private String url;

	@Default("0")
	@Column("is_deleted")
	private boolean deleted;

	@Column
	@Default("北京市")
	private String province;

	public MealSeller() {
		super();
	}

	public MealSeller(String name, String description, String avatar, String saleTime, String address, String telephone, String url, String province) {
		super();
		this.name = name;
		this.description = description;
		this.avatar = avatar;
		this.saleTime = saleTime;
		this.address = address;
		this.telephone = telephone;
		this.url = url;
		this.province = province;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSaleTime() {
		return saleTime;
	}

	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
}
