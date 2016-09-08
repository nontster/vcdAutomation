package com.vmware.vcloud.model;

public class GatewayParams {
	private String name;
	private String description;
	private GatewayConfiguration gatewayConfiguration;
	
	public GatewayParams() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GatewayParams(String name, String description, GatewayConfiguration gatewayConfiguration) {
		super();
		this.name = name;
		this.description = description;
		this.gatewayConfiguration = gatewayConfiguration;
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

	public GatewayConfiguration getGatewayConfiguration() {
		return gatewayConfiguration;
	}

	public void setGatewayConfiguration(GatewayConfiguration gatewayConfiguration) {
		this.gatewayConfiguration = gatewayConfiguration;
	}
	
}
