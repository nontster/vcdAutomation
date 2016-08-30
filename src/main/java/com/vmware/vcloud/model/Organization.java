package com.vmware.vcloud.model;

public class Organization {
	private String name;
	private String description;
	private String fullName;
	private boolean enabled;
	private User user;
	private OrgSettings orgSettings;
	private CloudResources cloudResources;
	private Vdc vdc;
	
	public Organization() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Organization(String name, String description, String fullName, boolean enabled, User user,
			OrgSettings orgSettings, CloudResources cloudResources, Vdc vdc) {
		super();
		this.name = name;
		this.description = description;
		this.fullName = fullName;
		this.enabled = enabled;
		this.user = user;
		this.orgSettings = orgSettings;
		this.cloudResources = cloudResources;
		this.vdc = vdc;
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

	public CloudResources getCloudResources() {
		return cloudResources;
	}

	public void setCloudResources(CloudResources cloudResources) {
		this.cloudResources = cloudResources;
	}

	public Vdc getVdc() {
		return vdc;
	}

	public void setVdc(Vdc vdc) {
		this.vdc = vdc;
	}
}