package com.vmware.vcloud.model;

public class EdgeGateway {
	private GatewayParams gatewayParams;

	public EdgeGateway() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EdgeGateway(GatewayParams gatewayParams) {
		super();
		this.gatewayParams = gatewayParams;
	}

	public GatewayParams getGatewayParams() {
		return gatewayParams;
	}

	public void setGatewayParams(GatewayParams gatewayParams) {
		this.gatewayParams = gatewayParams;
	}
	
}
