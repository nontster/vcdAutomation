package com.vmware.vcloud.model;

public class User {
	private String name;
	private String password;
	private boolean enabled;
	private String roleName;
	private String fullName;
	private String emailAddress;
	private String phone;
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String name, String password, boolean enabled, String roleName, String fullName, String emailAddress,
			String phone) {
		super();
		this.name = name;
		this.password = password;
		this.enabled = enabled;
		this.roleName = roleName;
		this.fullName = fullName;
		this.emailAddress = emailAddress;
		this.setPhone(phone);
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
