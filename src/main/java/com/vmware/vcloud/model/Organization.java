package com.vmware.vcloud.model;

public class Organization {
	private String name;
	private String description;
	private String fullName;
	private boolean enabled;
	private User user;
	private OrgSettings orgSettings;
	
	public Organization() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Organization(String name, String description, String fullName, boolean enabled, User user,
			OrgSettings orgSettings) {
		super();
		this.name = name;
		this.description = description;
		this.fullName = fullName;
		this.enabled = enabled;
		this.user = user;
		this.orgSettings = orgSettings;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public OrgSettings getOrgSettings() {
		return orgSettings;
	}

	public void setOrgSettings(OrgSettings orgSettings) {
		this.orgSettings = orgSettings;
	}

}