package com.rekoe.core.bean.acl;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

import com.rekoe.core.bean.DataBaseEntity;

/**
 * @author 科技㊣²º¹³<br />
 *         2014年2月3日 下午4:48:45<br />
 *         http://www.rekoe.com<br />
 *         QQ:5382211
 */
@Table("system_permission")
@TableIndexes({ @Index(name = "permission_name_index", fields = { "name" }, unique = true) })
public class Permission extends DataBaseEntity implements Serializable {
	private static final long serialVersionUID = -8140799124476746216L;
	@Id
	private Long id;
	@Column
	@ColDefine(type = ColType.VARCHAR, width = 200)
	private String name;
	@Column
	@ColDefine(type = ColType.VARCHAR, width = 500)
	private String description;
	@ManyMany(target = Role.class, relation = "system_role_permission", from = "permissionid", to = "roleid")
	private List<Role> roles;

	@Column("is_lock")
	private boolean lock;

	public Permission() {
		super();
	}

	public Permission(String name, String description, boolean lock) {
		super();
		this.name = name;
		this.description = description;
		this.lock = lock;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}