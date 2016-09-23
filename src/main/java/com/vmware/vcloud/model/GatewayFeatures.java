package com.vmware.vcloud.model;

public class GatewayFeatures {
	private FirewallService firewallService;

	public GatewayFeatures() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GatewayFeatures(FirewallService firewallService) {
		super();
		this.setFirewallService(firewallService);
	}

	public FirewallService getFirewallService() {
		return firewallService;
	}

	public void setFirewallService(FirewallService firewallService) {
		this.firewallService = firewallService;
	}
	
}
