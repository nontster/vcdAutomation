package com.vmware.vcloud.model;

public class OrgVdcNetwork {
	private String name;
	private String description;
	private Configuration configuration;
	
	public OrgVdcNetwork() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrgVdcNetwork(String name, String description, Configuration configuration) {
		super();
		this.name = name;
		this.description = description;
		this.setConfiguration(configuration);
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

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
}
