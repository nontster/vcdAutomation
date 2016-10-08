package com.vmware.vcloud.model;

public class EdgeGateway {
	private String name;
	private String description;
	private GatewayConfiguration gatewayConfiguration;
	private GatewayFeatures gatewayFeatures;
	
	public EdgeGateway() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EdgeGateway(String name, String description, GatewayConfiguration gatewayConfiguration,
			GatewayFeatures gatewayFeatures) {
		super();
		this.name = name;
		this.description = description;
		this.gatewayConfiguration = gatewayConfiguration;
		this.gatewayFeatures = gatewayFeatures;
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

	public GatewayFeatures getGatewayFeatures() {
		return gatewayFeatures;
	}

	public void setGatewayFeatures(GatewayFeatures gatewayFeatures) {
		this.gatewayFeatures = gatewayFeatures;
	}
		
}
