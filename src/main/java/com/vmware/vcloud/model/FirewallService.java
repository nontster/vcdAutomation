package com.vmware.vcloud.model;

import java.util.List;

public class FirewallService {
	private Boolean enabled;
	private FirewallAction defaultAction;
	private Boolean logDefaultAction;	
	private List <FirewallRule> firewallRules;
	
	public FirewallService() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FirewallService(Boolean enabled, FirewallAction defaultAction, Boolean logDefaultAction,
			List<FirewallRule> firewallRules) {
		super();
		this.enabled = enabled;
		this.defaultAction = defaultAction;
		this.logDefaultAction = logDefaultAction;
		this.firewallRules = firewallRules;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public FirewallAction getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(FirewallAction defaultAction) {
		this.defaultAction = defaultAction;
	}

	public Boolean isLogDefaultAction() {
		return logDefaultAction;
	}

	public void setLogDefaultAction(Boolean logDefaultAction) {
		this.logDefaultAction = logDefaultAction;
	}

	public List<FirewallRule> getFirewallRules() {
		return firewallRules;
	}

	public void setFirewallRules(List<FirewallRule> firewallRules) {
		this.firewallRules = firewallRules;
	}
	
}
