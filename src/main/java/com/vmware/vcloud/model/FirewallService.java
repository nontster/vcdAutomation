package com.vmware.vcloud.model;

import java.util.List;

public class FirewallService {
	private boolean enabled;
	private FirewallAction defaultAction;
	private boolean logDefaultAction;	
	private List <FirewallRule> firewallRules;
	
	public FirewallService() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FirewallService(boolean enabled, FirewallAction defaultAction, boolean logDefaultAction,
			List<FirewallRule> firewallRules) {
		super();
		this.enabled = enabled;
		this.defaultAction = defaultAction;
		this.logDefaultAction = logDefaultAction;
		this.firewallRules = firewallRules;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public FirewallAction getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(FirewallAction defaultAction) {
		this.defaultAction = defaultAction;
	}

	public boolean isLogDefaultAction() {
		return logDefaultAction;
	}

	public void setLogDefaultAction(boolean logDefaultAction) {
		this.logDefaultAction = logDefaultAction;
	}

	public List<FirewallRule> getFirewallRules() {
		return firewallRules;
	}

	public void setFirewallRules(List<FirewallRule> firewallRules) {
		this.firewallRules = firewallRules;
	}
	
}
