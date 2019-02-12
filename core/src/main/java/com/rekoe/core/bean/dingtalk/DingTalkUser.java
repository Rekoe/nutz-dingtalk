package com.rekoe.core.bean.dingtalk;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import com.rekoe.core.bean.DataBaseEntity;

@Table("ding_talk_user")
@TableIndexes({ @Index(name = "d_user_userid_index", fields = { "userid" }, unique = true) })
public class DingTalkUser extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = 4754977073745895453L;

	@Name
	@Column(hump = true)
	private String openId;

	@Column
	private String userid;

	@Column(hump = true)
	private String dingId;

	@Column
	private String name;

	@Default("1")
	@Column("is_active")
	private boolean active;

	@Column
	@Default("http://momshop-media-hk.oss-cn-hongkong.aliyuncs.com/momshop-image-temp/15483326259653F.jpg")
	@ColDefine(width = 1024)
	private String avatar;

	@Default("")
	@Column(hump = true)
	private String userProvince;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDingId() {
		return dingId;
	}

	public void setDingId(String dingId) {
		this.dingId = dingId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUserProvince() {
		return userProvince;
	}

	public void setUserProvince(String userProvince) {
		this.userProvince = userProvince;
	}

}
