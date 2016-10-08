package com.vmware.vcloud.model;

import java.util.List;

public class GatewayConfiguration {
	
	private GatewayBackingConfigEnums gatewayBackingConfig;
	private boolean haEnabled;
	private boolean useDefaultRouteForDnsRelay;
	private List<GatewayInterface> gatewayInterfaces;
	
	public GatewayConfiguration() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GatewayConfiguration(GatewayBackingConfigEnums gatewayBackingConfig, boolean haEnabled,
			boolean useDefaultRouteForDnsRelay, List<GatewayInterface> gatewayInterfaces) {
		super();
		this.gatewayBackingConfig = gatewayBackingConfig;
		this.haEnabled = haEnabled;
		this.useDefaultRouteForDnsRelay = useDefaultRouteForDnsRelay;
		this.gatewayInterfaces = gatewayInterfaces;
	}

	public GatewayBackingConfigEnums getGatewayBackingConfig() {
		return gatewayBackingConfig;
	}

	public void setGatewayBackingConfig(GatewayBackingConfigEnums gatewayBackingConfig) {
		this.gatewayBackingConfig = gatewayBackingConfig;
	}

	
	public boolean isHaEnabled() {
		return haEnabled;
	}

	public void setHaEnabled(boolean haEnabled) {
		this.haEnabled = haEnabled;
	}

	public boolean isUseDefaultRouteForDnsRelay() {
		return useDefaultRouteForDnsRelay;
	}

	public void setUseDefaultRouteForDnsRelay(boolean useDefaultRouteForDnsRelay) {
		this.useDefaultRouteForDnsRelay = useDefaultRouteForDnsRelay;
	}

	public List<GatewayInterface> getGatewayInterfaces() {
		return gatewayInterfaces;
	}

	public void setGatewayInterfaces(List<GatewayInterface> gatewayInterfaces) {
		this.gatewayInterfaces = gatewayInterfaces;
	}

}
