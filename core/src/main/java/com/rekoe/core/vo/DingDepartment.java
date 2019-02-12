package com.rekoe.core.vo;

public class DingDepartment {

	private int id;
	private String createDeptGroup;
	private String name;
	private boolean autoAddUser;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreateDeptGroup() {
		return createDeptGroup;
	}

	public void setCreateDeptGroup(String createDeptGroup) {
		this.createDeptGroup = createDeptGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutoAddUser() {
		return autoAddUser;
	}

	public void setAutoAddUser(boolean autoAddUser) {
		this.autoAddUser = autoAddUser;
	}

}
