package com.vmware.vcloud.model;

public class OrgVdcNetwork {
	private String name;
	private String description;
	
	public OrgVdcNetwork() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgVdcNetwork(String name, String description) {
		super();
		this.name = name;
		this.description = description;
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
	
}
