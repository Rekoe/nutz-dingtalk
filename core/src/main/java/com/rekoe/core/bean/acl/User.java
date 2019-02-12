package com.rekoe.core.bean.acl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import com.rekoe.core.bean.DataBaseEntity;

/**
 * @author 科技㊣²º¹³ <br />
 *         2014年2月3日 下午4:48:45<br />
 *         http://www.rekoe.com<br />
 *         QQ:5382211
 */
@Table("system_user")
@TableIndexes({ @Index(name = "user_name", fields = { "name" }, unique = false), @Index(name = "user_openid", fields = { "openid" }, unique = true) })
public class User extends DataBaseEntity implements Serializable {

	private static final long serialVersionUID = -965829144356813385L;

	@Id
	private Long id;

	@Column
	@ColDefine(type = ColType.VARCHAR, width = 20)
	private String name;

	@Column(hump = true)
	private String realName;

	@Column
	@ColDefine(type = ColType.CHAR, width = 44)
	private String password;
	@Column
	@ColDefine(type = ColType.CHAR, width = 24)
	private String salt;
	@Column
	@ColDefine(type = ColType.VARCHAR, width = 64)
	private String openid;
	@Column
	@ColDefine(type = ColType.VARCHAR, width = 10)
	private String providerid;
	@Column("is_locked")
	@ColDefine(type = ColType.BOOLEAN)
	private boolean locked;
	@Column
	@ColDefine(type = ColType.VARCHAR, width = 500)
	private String description;
	@Column(hump = true)
	@ColDefine(type = ColType.TIMESTAMP)
	private Date createDate;
	@Column(hump = true)
	@ColDefine(type = ColType.VARCHAR, width = 15)
	private String registerIp;
	@ManyMany(target = Role.class, relation = "system_user_role", from = "userid", to = "roleid")
	private List<Role> roles;

	@Column("is_system")
	@ColDefine(type = ColType.BOOLEAN)
	private boolean system;

	@Column("u_status")
	@Comment("用户状态")
	@Default("ACTIVED")
	private com.rekoe.core.vo.Status status = com.rekoe.core.vo.Status.ACTIVED;

	@Column("u_head_key")
	@Comment("用户头像")
	@ColDefine(width = 200)
	private String headKey;

	public String getHeadKey() {
		return headKey;
	}

	public void setHeadKey(String headKey) {
		this.headKey = headKey;
	}

	public com.rekoe.core.vo.Status getStatus() {
		return status;
	}

	public void setStatus(com.rekoe.core.vo.Status status) {
		this.status = status;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getProviderid() {
		return providerid;
	}

	public void setProviderid(String providerid) {
		this.providerid = providerid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

}
